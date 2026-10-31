/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.bson.Document;
import org.bson.types.Binary;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import io.opentelemetry.api.trace.Tracer;
import it.finanze.sanita.fse2.ms.iniclient.TestUtility;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.config.mongo.*;
import it.finanze.sanita.fse2.ms.iniclient.controller.impl.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.*;
import it.finanze.sanita.fse2.ms.iniclient.service.*;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.*;

@Tag("ISOLATION") @ActiveProfiles("ISOLATION")
@TestPropertySource(properties = "uar.client.mock-enable=false")
@SpringJUnitConfig({IniOperationCTL.class, IniOperation_V2CTL.class})
class IniControllerIsolationTest {
    @Autowired IniOperationCTL controller;
    @Autowired IniOperation_V2CTL v2;
    @MockitoBean IIniInvocationSRV real;
    @MockitoBean IIniInvocationMockedSRV simulated;
    @MockitoBean IIssuerSRV issuers;
    @MockitoBean IniCFG config;
    @MockitoBean IAuditIniSrv audits;
    @MockitoBean MongoDatabaseCFG mongo;
    @MockitoBean MongoPropertiesCFG props;
    @MockitoBean Tracer tracer;

    private void route(int mode) {
        when(config.isMockEnable()).thenReturn(mode > 0);
        if (mode > 0 && mode != 3) {
            var issuer = new IssuerETY(); issuer.setMock(mode == 2); issuer.setMockUar(false);
            when(issuers.findByIssuer("issuer")).thenReturn(issuer);
        }
        when(issuers.isMocked("issuer")).thenReturn(mode >= 2);
    }
    private IniResponseDTO outcome() { var result=new IniResponseDTO();result.setEsito(true);result.setMessage("accepted");return result; }
    private JWTPayloadDTO token() { var p = new JWTPayloadDTO(); p.setIss("issuer"); return p; }
    private void accepted(IniTraceResponseDTO response) {
        assertTrue(response.getEsito()); assertEquals("accepted", response.getMessage());
    }

    @ParameterizedTest @ValueSource(ints = {0,1,2,3})
    void publishAndReplaceRouteByGlobalAndIssuerFlagsAndDecryptMetadata(int mode) {
        route(mode);
        when(props.isEncryptionEnabled()).thenReturn(true);
        var binary = new Binary(new byte[]{1,2});
        var decrypted = new Document("field", "plaintext");
        when(mongo.decryptAsDocument(binary)).thenReturn(decrypted);
        for (var operation : List.of(ProcessorOperationEnum.PUBLISH, ProcessorOperationEnum.REPLACE)) {
            var entity = new IniEdsInvocationETY(); entity.setIssuer("issuer");
            var metadata = new Document("submissionSetEntry",binary).append("documentEntry",binary)
                    .append("tokenEntry",new Document("payload",binary));
            entity.setMetadata(List.of(metadata, new Document()));
            when(real.findByWII(eq("workflow"),eq(operation),any())).thenReturn(entity);
            when(real.publishOrReplaceOnIni(eq("workflow"),eq(operation),same(entity),eq("TRUE"))).thenReturn(outcome());
            when(simulated.publishOrReplaceOnIni("workflow",operation,"TRUE")).thenReturn(outcome());
            accepted(operation == ProcessorOperationEnum.PUBLISH ? controller.create("workflow",null) : controller.replace("workflow",null));
            assertSame(decrypted,metadata.get("documentEntry"));
            assertSame(decrypted,metadata.get("submissionSetEntry"));
            assertSame(decrypted,metadata.get("tokenEntry",Document.class).get("payload"));
            if (mode >= 2) verify(simulated).publishOrReplaceOnIni("workflow",operation,"TRUE");
            else verify(real).publishOrReplaceOnIni("workflow",operation,entity,"TRUE");
        }
    }

    @ParameterizedTest @ValueSource(ints = {0,1,2,3})
    void deleteUpdateVersionsAndObscuringPreserveBusinessOutcomeAndRouting(int mode) {
        route(mode);
        var delete = new DeleteRequestDTO(); delete.setIss("issuer");
        when(real.deleteByDocumentId(delete)).thenReturn(outcome());
        when(simulated.deleteByDocumentId(delete)).thenReturn(outcome());
        accepted(controller.delete(delete,null));
        var update = new UpdateRequestDTO(); update.setToken(token());
        update.setMarshallData("<SubmitObjectsRequest xmlns=\"urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0\"><RegistryObjectList xmlns=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\"/></SubmitObjectsRequest>");
        when(real.updateByRequestBody(any(),same(update),anyBoolean())).thenReturn(outcome());
        when(simulated.updateByRequestBody(any(),same(update))).thenReturn(outcome());
        accepted(controller.update(update,null)); accepted(v2.update(update,null));
        var obscure = new UpdateOscuramentoRequestDTO(); obscure.setToken(token());
        when(real.updateOscuramentoByRequestBody(obscure)).thenReturn(outcome());
        when(simulated.updateOscuramentoByRequestBody(obscure)).thenReturn(outcome());
        accepted(controller.updateOscuramentoCatena(obscure,null));
        if (mode >= 2) {
            verify(simulated).deleteByDocumentId(delete);
            verify(simulated,times(2)).updateByRequestBody(argThat(r -> r.getRegistryObjectList()!=null),same(update));
            verify(simulated).updateOscuramentoByRequestBody(obscure);
        } else {
            verify(real).deleteByDocumentId(delete);
            verify(real).updateByRequestBody(any(),same(update),eq(false));
            verify(real).updateByRequestBody(any(),same(update),eq(true));
            verify(real).updateOscuramentoByRequestBody(obscure);
        }
    }

    @ParameterizedTest @ValueSource(ints = {0,1,2,3})
    void metadataReadsAndMergesReturnSelectedServicePayload(int mode) throws Exception {
        route(mode);
        var query = TestUtility.mockQueryResponse();
        when(real.getMetadata(eq("oid"),any())).thenReturn(query);
        when(simulated.getMetadata(eq("oid"),any())).thenReturn(query);
        var req = new GetMetadatiReqDTO(); req.setIss("issuer");
        assertSame(query,controller.getMetadati("oid",req,null).getBody().getResponse());
        var reference = new GetReferenceReqDTO();reference.setToken(token());reference.setWorkflowInstanceId("workflow");
        var document = new GetDocumentMetadataResponseDTO();document.setUuid("uuid");
        when(real.getDocumentMetadata(eq("oid"),any(),eq("workflow"))).thenReturn(document);
        when(simulated.getDocumentMetadata(eq("oid"),any(),eq("workflow"),eq("TRUE"))).thenReturn(document);
        assertSame(document,controller.getDocumentMetadata("oid",reference,null).getBody());
        var merge = new MergedMetadatiRequestDTO();merge.setToken(token());merge.setIdDoc("oid");
        var merged = new GetMergedMetadatiDTO();merged.setMarshallResponse("xml");merged.setEdsPublished("TRUE");
        when(real.getMergedMetadati("oid",merge)).thenReturn(merged);
        when(simulated.getMergedMetadati("oid",merge,"TRUE")).thenReturn(merged);
        var output=controller.getMergedMetadati(merge,null);
        assertEquals("xml",output.getMarshallResponse());assertEquals("TRUE",output.getEdsPublished());
        if(mode>=2) verify(simulated).getMetadata(eq("oid"),any()); else verify(real).getMetadata(eq("oid"),any());
    }

    @Test void documentReadReportsServiceFailureAndAuditDelegatesByWorkflow() {
        var req=new GetReferenceReqDTO();req.setToken(token());
        when(real.getDocumentMetadata(any(),any(),any())).thenThrow(new IllegalStateException("unavailable"));
        assertEquals("unavailable",controller.getDocumentMetadata("oid",req,null).getBody().getErrorMessage());
        var events=new IniAuditsDto();when(audits.findByWii("workflow")).thenReturn(events);
        assertSame(events,controller.getEventByWii("workflow",null));
    }

    @Test void crashProgramExtractsSlotsClassificationsAndDistinguishesMissingDocument() throws Exception {
        var query=TestUtility.mockQueryResponse();
        var entry=(ExtrinsicObjectType)query.getRegistryObjectList().getIdentifiable().stream().map(e->e.getValue())
                .filter(ExtrinsicObjectType.class::isInstance).findFirst().orElseThrow();
        entry.getSlot().clear();entry.getClassification().clear();
        for(String name:List.of("repositoryUniqueId","urn:ita:2022:administrativeRequest","serviceStartTime","serviceStopTime",
                "urn:ita:2017:repository-type","urn:ita:2022:urn:ita:2022:description")) {
            var slot=new SlotType1();slot.setName(name);slot.setValueList(new ValueListType());slot.getValueList().getValue().add("value");entry.getSlot().add(slot);
        }
        for(String id:List.of("ClassCodeId_1","healthcareFacilityTypeCode_1","practiceSettingCode_1","EventCodeList_1_1")) {
            var c=new ClassificationType();c.setId(id);c.setNodeRepresentation("code");entry.getClassification().add(c);
        }
        when(real.getMetadata(eq("oid"),any())).thenReturn(query);
        var found=controller.getMetadatiPostCrash("oid",new GetMetadatiReqDTO(),null).getBody();
        assertTrue(found.getFoundDocument());assertEquals("value",found.getMetadati().getSlotIdentificativoRep());
        assertEquals("code",found.getMetadati().getClassificationTipoDocumentoLivAlto());
        var errors=new RegistryErrorList();var error=new RegistryError();error.setCodeContext("No results from the query");errors.getRegistryError().add(error);query.setRegistryErrorList(errors);
        var missing=controller.getMetadatiPostCrash("oid",new GetMetadatiReqDTO(),null).getBody();
        assertFalse(missing.getFoundDocument());assertNull(missing.getMetadati());
    }
}
