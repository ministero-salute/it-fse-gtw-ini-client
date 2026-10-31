/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.*;
import java.util.*;
import javax.xml.ws.*;
import javax.xml.ws.handler.MessageContext;
import com.sun.xml.ws.developer.WSBindingProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import it.finanze.sanita.fse2.ms.iniclient.TestConstants;
import it.finanze.sanita.fse2.ms.iniclient.TestUtility;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.IniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.GovwayCfg;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.*;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.DocumentReferenceNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.*;
import ihe.iti.xds_b._2007.*;
import ihe.iti.xds_b._2010.XDSDeletetWS;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.*;
import org.bson.Document;

@Tag("ISOLATION")
class SoapTransportIsolationTest {
    IniClient client;
    DocumentRegistryPortType registry, reference;
    UpdateDocumentRegistryPortType update;
    XDSDeletetWS delete;
    SamlHeaderBuilderUtility headers;
    GovwayCfg gateway;
    Map<String,Object> registryContext,referenceContext,updateContext,deleteContext;
    private <T> T port(Class<T> type){
        return mock(type,withSettings().extraInterfaces(WSBindingProvider.class));
    }
    private Map<String,Object> context(Object port){
        var context=new HashMap<String,Object>();when(((WSBindingProvider)port).getRequestContext()).thenReturn(context);return context;
    }
    @BeforeEach void setup(){
        client=new IniClient();registry=port(DocumentRegistryPortType.class);reference=port(DocumentRegistryPortType.class);
        update=port(UpdateDocumentRegistryPortType.class);delete=port(XDSDeletetWS.class);
        registryContext=context(registry);referenceContext=context(reference);updateContext=context(update);deleteContext=context(delete);
        headers=mock(SamlHeaderBuilderUtility.class);gateway=mock(GovwayCfg.class);
        ReflectionTestUtils.setField(client,"documentRegistryPort",registry);ReflectionTestUtils.setField(client,"recuperoRiferimentoPort",reference);
        ReflectionTestUtils.setField(client,"updateDocumentRegistryPort",update);ReflectionTestUtils.setField(client,"deletePort",delete);
        ReflectionTestUtils.setField(client,"samlHeaderBuilderUtility",headers);ReflectionTestUtils.setField(client,"govwayCfg",gateway);
        when(headers.buildHeader(any(),any())).thenReturn(List.of());
    }
    private void auth(boolean enabled){if(enabled){when(gateway.getGovwayUser()).thenReturn("user");when(gateway.getGovwayPass()).thenReturn("pass");}}
    private void assertContext(Map<String,Object> context,Date date,boolean auth){
        assertEquals("workflow",context.get(WII));assertSame(date,context.get(EVENT_DATE));assertNotNull(context.get(EVENT_TYPE));
        if(auth)assertEquals(Map.of("Authorization",List.of("Basic dXNlcjpwYXNz")),context.get(MessageContext.HTTP_REQUEST_HEADERS));
        else assertFalse(context.containsKey(MessageContext.HTTP_REQUEST_HEADERS));
    }
    @ParameterizedTest @ValueSource(booleans={false,true})
    void publishReplaceUpdatesAndDeleteBuildRealRequestsAndPropagateResponses(boolean auth){
        auth(auth);var date=new Date();var token=TestUtility.mockBasicToken();
        var tree=RequestUtility.extractDocumentsFromMetadata(Document.parse(TestConstants.TEST_INI_EDS_ENTRY).getList("metadata",Document.class));
        var doc=CommonUtility.extractDocumentEntry(tree.getDocumentEntry());var submission=CommonUtility.extractSubmissionSetEntry(tree.getSubmissionSetEntry());
        var response=new RegistryResponseType();when(registry.documentRegistryRegisterDocumentSetB(any())).thenReturn(response);
        when(update.documentRegistryUpdateDocumentSet(any())).thenReturn(response);
        assertSame(response,client.sendPublicationData(doc,submission,token,"workflow",date,"TRUE"));
        assertSame(response,client.sendReplaceData(doc,submission,token,"previous","workflow",date,"FALSE"));
        var request=new SubmitObjectsRequest();
        assertSame(response,client.sendUpdateData(request,token,"workflow",date));
        assertSame(response,client.sendUpdateV2Data(request,token,"workflow",date));
        var remove=new DeleteRequestDTO();remove.setUuid(List.of("first","second"));remove.setWorkflow_instance_id("workflow");
        doAnswer(c->{assertEquals(List.of("first","second"),c.getArgument(0,oasis.names.tc.ebxml_regrep.xsd.lcm._3.RemoveObjectsRequestType.class)
                .getObjectRefList().getObjectRef().stream().map(e->e.getId()).toList());c.getArgument(1,Holder.class).value=response;return null;})
                .when(delete).documentRegistryDeleteDocumentSet(any(),any());
        assertSame(response,client.sendDeleteData(remove,token.getPayload(),remove.getUuid(),date));
        assertContext(registryContext,date,auth);assertContext(updateContext,date,auth);assertContext(deleteContext,date,auth);
        var capture=org.mockito.ArgumentCaptor.forClass(SubmitObjectsRequest.class);
        verify(registry,times(3)).documentRegistryRegisterDocumentSetB(capture.capture());
        assertEquals(4,capture.getAllValues().get(0).getRegistryObjectList().getIdentifiable().size());
        assertEquals(5,capture.getAllValues().get(1).getRegistryObjectList().getIdentifiable().size());
        assertSame(request,capture.getAllValues().get(2));
        verify(headers).buildHeader(token,ActionEnumType.CREATE);verify(headers).buildHeader(token,ActionEnumType.REPLACE);
    }
    @ParameterizedTest @ValueSource(booleans={false,true})
    void readMetadataUsesCorrectPortAndReconfiguresOnlyACopy(boolean auth){
        auth(auth);var token=TestUtility.mockBasicToken();var response=new AdhocQueryResponse();
        when(registry.documentRegistryRegistryStoredQuery(any())).thenReturn(response);
        when(reference.documentRegistryRegistryStoredQuery(any())).thenReturn(response);
        assertSame(response,client.getReferenceUUID("oid","LeafClass",token));
        for(var action:List.of(ActionEnumType.READ_METADATA,ActionEnumType.READ_REFERENCE)){
            var date=new Date();
            assertSame(response,client.getReferenceMetadata("oid","ObjectRef",token,action,"workflow",date));
            assertContext(action==ActionEnumType.READ_METADATA?registryContext:referenceContext,date,auth);
        }
        assertSame(response,client.getReferenceMetadata("oid","LeafClass",token,"workflow"));
        verify(headers,times(2)).buildHeader(argThat(t->t!=token&&t.getPayload().getAction_id().equals(ActionEnumType.READ_METADATA.getActionId())),eq(ActionEnumType.READ_METADATA));
    }
    @ParameterizedTest @ValueSource(strings={"missing","QND1","other"})
    void registryErrorsAreNotReturnedAsSuccessfulMetadata(String kind){
        var response=new AdhocQueryResponse();var errors=new RegistryErrorList();var error=new RegistryError();
        error.setCodeContext(kind.equals("missing")?"No results from the query":"detail");error.setErrorCode(kind);errors.getRegistryError().add(error);response.setRegistryErrorList(errors);
        when(registry.documentRegistryRegistryStoredQuery(any())).thenReturn(response);
        if(kind.equals("other"))assertTrue(assertThrows(BusinessException.class,()->client.getReferenceMetadata("oid","LeafClass",TestUtility.mockBasicToken(),"workflow")).getMessage().contains("detail"));
        else assertThrows(DocumentReferenceNotFoundException.class,()->client.getReferenceMetadata("oid","LeafClass",TestUtility.mockBasicToken(),"workflow"));
    }
}
