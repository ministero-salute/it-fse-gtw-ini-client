/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import it.finanze.sanita.fse2.ms.iniclient.TestConstants;
import it.finanze.sanita.fse2.ms.iniclient.TestUtility;
import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.*;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.*;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.IniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(IniInvocationSRV.class)
class IniInvocationIsolationTest {
    @Autowired private IniInvocationSRV service;
    @MockitoBean private IIniClient client;
    @MockitoBean private IniInvocationRepo repository;
    @MockitoBean private IConfigSRV configuration;
    @MockitoBean private LoggerHelper logger;
    @MockitoBean private SamlHeaderBuilderUtility headers;
    private static final String WARNING = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Warning";

    private RegistryResponseType result(String severity, String code, String context) {
        var result = new RegistryResponseType();
        var errors = new RegistryErrorList();
        var error = new RegistryError();
        error.setSeverity(severity);
        error.setErrorCode(code);
        error.setCodeContext(context);
        errors.getRegistryError().add(error);
        result.setRegistryErrorList(errors);
        return result;
    }

    private IniEdsInvocationETY publication() {
        Document data = Document.parse(TestConstants.TEST_INI_EDS_ENTRY);
        var entity = new IniEdsInvocationETY();
        entity.setWorkflowInstanceId("workflow");
        entity.setRiferimentoIni("urn:uuid:previous");
        entity.setMetadata(data.getList("metadata", Document.class));
        when(headers.extractTokenEntry(any())).thenReturn(TestUtility.mockBasicToken());
        return entity;
    }

    @ParameterizedTest @CsvSource({"PUBLISH,false", "PUBLISH,true", "REPLACE,false", "REPLACE,true"})
    void publishAndReplacePreserveMetadataAndDistinguishErrorsFromWarnings(ProcessorOperationEnum operation, boolean error) {
        var entity = publication();
        RegistryResponseType response = result(error ? "Error" : WARNING, "XDSRegistryError", "registry detail");
        when(client.sendPublicationData(any(), any(), any(), anyString(), any(), anyString())).thenReturn(response);
        when(client.sendReplaceData(any(), any(), any(), anyString(), anyString(), any(), anyString())).thenReturn(response);
        var actual = service.publishOrReplaceOnIni("workflow", operation, entity, "TRUE");
        assertEquals(!error, actual.getEsito());
        assertTrue(actual.getMessage().contains("registry detail"));
        assertTrue(actual.getMessage().contains("XDSRegistryError"));
        if (operation == ProcessorOperationEnum.PUBLISH) {
            verify(client).sendPublicationData(argThat(d -> d.getTypeCode() != null), notNull(), notNull(),
                    eq("workflow"), any(Date.class), eq("TRUE"));
        } else {
            verify(client).sendReplaceData(argThat(d -> d.getTypeCode() != null), notNull(), notNull(),
                    eq("urn:uuid:previous"), eq("workflow"), any(Date.class), eq("TRUE"));
        }
    }

    @ParameterizedTest @ValueSource(strings = {"PUBLISH", "REPLACE"})
    void successfulPublishAndReplaceHaveNoErrorMessage(String operation) {
        var entity = publication();
        when(client.sendPublicationData(any(), any(), any(), anyString(), any(), anyString())).thenReturn(new RegistryResponseType());
        when(client.sendReplaceData(any(), any(), any(), anyString(), anyString(), any(), anyString())).thenReturn(new RegistryResponseType());
        var actual = service.publishOrReplaceOnIni("workflow", ProcessorOperationEnum.valueOf(operation), entity, "FALSE");
        assertTrue(actual.getEsito());
        assertNull(actual.getMessage());
    }

    @ParameterizedTest @ValueSource(strings = {"PUBLISH", "REPLACE"})
    void failedPublicationTransportPreservesCause(String operation) {
        var entity = publication();
        var failure = new IllegalStateException("INI unavailable");
        when(client.sendPublicationData(any(), any(), any(), anyString(), any(), anyString())).thenThrow(failure);
        when(client.sendReplaceData(any(), any(), any(), anyString(), anyString(), any(), anyString())).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class, () -> service.publishOrReplaceOnIni(
                "workflow", ProcessorOperationEnum.valueOf(operation), entity, "FALSE")).getCause());
    }

    @Test void workflowLookupReportsMissingDocumentRatherThanCallingIni() {
        assertThrows(IdDocumentNotFoundException.class,
                () -> service.findByWII("missing", ProcessorOperationEnum.PUBLISH, new Date()));
        verifyNoInteractions(client);
        var entity = new IniEdsInvocationETY();
        when(repository.findByWorkflowInstanceId("present")).thenReturn(entity);
        assertSame(entity, service.findByWII("present", ProcessorOperationEnum.REPLACE, new Date()));
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void deletionReturnsRegistryOutcomeAndTransmitsIdentity(boolean error) {
        var request = JsonUtility.jsonToObject(TestConstants.TEST_DELETE_REQ, DeleteRequestDTO.class);
        request.setUuid(List.of("urn:uuid:delete"));
        when(client.sendDeleteData(eq(request), any(), eq(List.of("urn:uuid:delete")), any()))
                .thenReturn(error ? result("Error", "XDSUnknownPatientId", "patient not found") : new RegistryResponseType());
        var actual = service.deleteByDocumentId(request);
        assertEquals(!error, actual.getEsito());
        if (error) assertTrue(actual.getMessage().contains("patient not found"));
        else assertNull(actual.getMessage());
        verify(client).sendDeleteData(eq(request), argThat(p -> p.getIss().equals(request.getIss())),
                eq(List.of("urn:uuid:delete")), any());
    }

    @Test void deleteTransportFailureIsWrapped() {
        var failure = new IllegalStateException("timeout");
        when(client.sendDeleteData(any(), any(), any(), any())).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class,
                () -> service.deleteByDocumentId(new DeleteRequestDTO())).getCause());
    }

    @ParameterizedTest @CsvSource({"false,false", "false,true", "true,false", "true,true"})
    void updateRoutesVersionsAndAcceptsWarningsButRejectsErrors(boolean v2, boolean error) {
        var request = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ_NEW, UpdateRequestDTO.class);
        request.setWorkflow_instance_id("workflow");
        var body = new SubmitObjectsRequest();
        var response = result(error ? "Error" : WARNING, "XDSRegistryError", "update detail");
        when(client.sendUpdateData(eq(body), any(), eq("workflow"), any())).thenReturn(response);
        when(client.sendUpdateV2Data(eq(body), any(), eq("workflow"), any())).thenReturn(response);
        if (error) assertTrue(assertThrows(BusinessException.class,
                () -> service.updateByRequestBody(body, request, v2)).getCause().getMessage().contains("update detail"));
        else assertTrue(service.updateByRequestBody(body, request, v2).getEsito());
        if (v2) {
            verify(client).sendUpdateV2Data(eq(body), any(), eq("workflow"), any());
            verify(client, never()).sendUpdateData(any(), any(), any(), any());
        } else {
            verify(client).sendUpdateData(eq(body), any(), eq("workflow"), any());
            verify(client, never()).sendUpdateV2Data(any(), any(), any(), any());
        }
    }

    @Test void updateTransportFailurePreservesCause() {
        var request = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ_NEW, UpdateRequestDTO.class);
        var failure = new IllegalStateException("timeout");
        when(client.sendUpdateData(any(), any(), any(), any())).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class,
                () -> service.updateByRequestBody(new SubmitObjectsRequest(), request, false)).getCause());
    }

    private void metadataResponse(AdhocQueryResponse response) {
        when(client.getReferenceMetadata(eq("oid"), anyString(), any(), any(ActionEnumType.class), eq("workflow"), any()))
                .thenReturn(response);
    }

    @Test void documentMetadataFindsEntryUuidRatherThanLidOrFirstObjectRef() throws Exception {
        var response = TestUtility.mockQueryResponse();
        var factory = new ObjectFactory();
        var reference = new ObjectRefType();
        reference.setId("urn:uuid:not-the-document");
        response.getRegistryObjectList().getIdentifiable().add(0, factory.createObjectRef(reference));
        response.getRegistryObjectList().getIdentifiable().stream().map(e -> e.getValue())
                .filter(ExtrinsicObjectType.class::isInstance).map(ExtrinsicObjectType.class::cast)
                .forEach(e -> e.setLid("urn:uuid:different-lineage"));
        metadataResponse(response);
        var token = TestUtility.mockBasicToken();
        token.getPayload().setAction_id("CREATE");
        var actual = service.getDocumentMetadata("oid", token, "workflow");
        var document = (ExtrinsicObjectType) response.getRegistryObjectList().getIdentifiable().stream()
                .map(e -> e.getValue()).filter(ExtrinsicObjectType.class::isInstance).findFirst().orElseThrow();
        assertEquals(document.getId(), actual.getUuid());
        assertNotEquals(document.getLid(), actual.getUuid());
        assertFalse(actual.getMetadata().isEmpty());
        assertNotNull(actual.getDocumentType());
        assertEquals("CREATE", token.getPayload().getAction_id());
        verify(client).getReferenceMetadata(eq("oid"), eq(SearchTypeEnum.LEAF_CLASS.getSearchKey()),
                argThat(t -> t != token && t.getPayload().getAction_id().equals(ActionEnumType.READ_METADATA.getActionId())),
                eq(ActionEnumType.READ_METADATA), eq("workflow"), any());
    }

    @ParameterizedTest @ValueSource(strings = {"null", "no-list", "empty", "object-ref", "empty-id"})
    void documentMetadataRejectsAbsentOrInvalidDocumentEntries(String scenario) {
        var response = new AdhocQueryResponse();
        if (!scenario.equals("no-list")) response.setRegistryObjectList(new RegistryObjectListType());
        if (scenario.equals("object-ref")) response.getRegistryObjectList().getIdentifiable()
                .add(new ObjectFactory().createObjectRef(new ObjectRefType()));
        if (scenario.equals("empty-id")) response.getRegistryObjectList().getIdentifiable()
                .add(new ObjectFactory().createExtrinsicObject(new ExtrinsicObjectType()));
        metadataResponse(scenario.equals("null") ? null : response);
        assertThrows(MergeMetadatoNotFoundException.class,
                () -> service.getDocumentMetadata("oid", TestUtility.mockBasicToken(), "workflow"));
    }

    @Test void metadataReadDelegatesAndPropagatesTransportFailure() {
        var response = new AdhocQueryResponse();
        var token = TestUtility.mockBasicToken();
        when(client.getReferenceUUID("oid", SearchTypeEnum.LEAF_CLASS.getSearchKey(), token)).thenReturn(response);
        assertSame(response, service.getMetadata("oid", token));
        var failure = new IllegalStateException("offline");
        when(client.getReferenceUUID("oid", SearchTypeEnum.LEAF_CLASS.getSearchKey(), token)).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.getMetadata("oid", token)));
    }

    @Test void referenceReadExtractsIdsAndReportsEmptyAndErrorResults() throws Exception {
        var response = TestUtility.mockQueryResponse();
        metadataResponse(response);
        var actual = service.getReference("oid", TestUtility.mockBasicToken(), "workflow");
        assertEquals(response.getRegistryObjectList().getIdentifiable().stream().map(e -> e.getValue().getId()).toList(), actual.getUuid());
        response.setTotalResultCount(BigInteger.ZERO);
        assertEquals("No record found", service.getReference("oid", TestUtility.mockBasicToken(), "workflow").getErrorMessage());
        response.setTotalResultCount(BigInteger.ONE);
        response.setRegistryErrorList(result("Error", "error", "registry failure").getRegistryErrorList());
        assertEquals("registry failure", service.getReference("oid", TestUtility.mockBasicToken(), "workflow").getErrorMessage());
        response.getRegistryErrorList().getRegistryError().get(0).setCodeContext("No results from the query");
        assertThrows(DocumentReferenceNotFoundException.class,
                () -> service.getReference("oid", TestUtility.mockBasicToken(), "workflow"));
    }

    @ParameterizedTest @CsvSource({"true,true,true", "true,false,false", "false,true,false"})
    void obscuringRequiresR220WarningNotJustAnySuccessfulResponse(boolean warning, boolean r220, boolean success) {
        var request = new UpdateOscuramentoRequestDTO();
        request.setToken(new JWTPayloadDTO());
        request.setWorkflowInstanceId("workflow");
        request.setResourceHl7Type("('11502-2^^2.16.840.1.113883.6.1')");
        request.setClassCode("WOR");
        var response = result(warning ? WARNING : "Error", r220 ? "R220" : "other", r220 ? "R220 patient is RDA" : "other warning");
        when(client.sendOscuramentoData(any(), isNull(), any(), eq("workflow"), any(), any(), any()))
                .thenAnswer(call -> {
                    DocumentEntryDTO entry = call.getArgument(0);
                    JWTTokenDTO token = call.getArgument(2);
                    assertEquals("WOR", entry.getClassCode());
                    assertEquals(Constants.IniClientConstants.GTW_SUBJECT_APPLICATION_ID, token.getPayload().getSubject_application_id());
                    assertEquals(request.getResourceHl7Type(), token.getPayload().getResource_hl7_type());
                    return response;
                });
        if (success) {
            var actual = service.updateOscuramentoByRequestBody(request);
            assertTrue(actual.getEsito());
            assertTrue(actual.getMessage().contains("R220"));
        } else assertThrows(BusinessException.class, () -> service.updateOscuramentoByRequestBody(request));
    }

    @Test void obscuringRejectsMissingWarningAndTransportFailures() {
        var request = new UpdateOscuramentoRequestDTO();
        request.setToken(new JWTPayloadDTO());
        when(client.sendOscuramentoData(any(), any(), any(), any(), any(), any(), any())).thenReturn(new RegistryResponseType());
        assertTrue(assertThrows(BusinessException.class, () -> service.updateOscuramentoByRequestBody(request))
                .getCause().getMessage().contains("R220"));
        var failure = new IllegalStateException("unavailable");
        when(client.sendOscuramentoData(any(), any(), any(), any(), any(), any(), any())).thenThrow(failure);
        assertSame(failure, assertThrows(BusinessException.class, () -> service.updateOscuramentoByRequestBody(request)).getCause());
    }
}
