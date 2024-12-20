
/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.enums.SearchTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.IdDocumentNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.MergeMetadatoNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import javax.xml.namespace.QName;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IniInvocationTest {

    @Autowired
    private IIniInvocationSRV iniInvocationSRV;

    @Autowired
    private MongoTemplate mongoTemplate;

    @MockBean
    private IIniClient iniClient;

    @MockBean
    private IConfigSRV config;

    @BeforeEach
    void dataInit() {
        Document entity = JsonUtility.jsonToObject(TestConstants.TEST_INI_EDS_ENTRY, Document.class);
        mongoTemplate.save(entity, Constants.Profile.TEST_PREFIX + Constants.Collections.INI_EDS_INVOCATION);
        when(config.isControlLogPersistenceEnable()).thenReturn(true);
    }

    @AfterEach
    void dropCollection() {
        mongoTemplate.dropCollection(IniEdsInvocationETY.class);
    }

    @Test
    @DisplayName("Publish - success test")
    void publishSuccessTest() {
        when(iniClient.sendPublicationData(any(DocumentEntryDTO.class), any(SubmissionSetEntryDTO.class), any(JWTTokenDTO.class)
        		, any(String.class), any(Date.class)))
                .thenReturn(new RegistryResponseType());
        IniResponseDTO response = iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.PUBLISH,getIniEdsInvocationTest());
        assertTrue(response.getEsito());
        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Publish - error test")
    void publishErrorTest() {
        when(iniClient.sendPublicationData(any(DocumentEntryDTO.class), any(SubmissionSetEntryDTO.class), any(JWTTokenDTO.class), any(String.class), any(Date.class)))
                .thenThrow(new BusinessException(""));
        IniEdsInvocationETY ety = getIniEdsInvocationTest();
        assertThrows(BusinessException.class, () -> iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.PUBLISH,ety));
    }


    @Test
    @DisplayName("Publish - error response test")
    void publishErrorResponseTest() {
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        when(iniClient.sendPublicationData(any(DocumentEntryDTO.class), any(SubmissionSetEntryDTO.class), any(JWTTokenDTO.class), any(String.class), any(Date.class)))
                .thenReturn(registryResponseType);
        IniResponseDTO response = iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.PUBLISH,getIniEdsInvocationTest());
        assertFalse(response.getEsito());
        assertNotNull(response.getMessage());
    }

    @Test
    @DisplayName("Replace - success test")
    void replaceSuccessTest() {
        RegistryResponseType registryResponseType = TestUtility.mockRegistrySuccess();
        when(iniClient.sendReplaceData(any(), any(), any(),any(), any(),any()))
                .thenReturn(registryResponseType);
        IniResponseDTO response = iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.REPLACE,getIniEdsInvocationTest());
        assertTrue(response.getEsito());
        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Replace - error test")
    void replaceErrorTest() {
        when(iniClient.sendReplaceData(any(), any(), any(), any(), any(),any())).thenThrow(new BusinessException(""));
        ReplaceRequestDTO requestDTO = new ReplaceRequestDTO();
        requestDTO.setRiferimentoIni("identificativoDoc");
        requestDTO.setWorkflowInstanceId(TestConstants.TEST_WII);
        assertThrows(BusinessException.class, () -> iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.REPLACE,getIniEdsInvocationTest()));
    }

    @Test
    @DisplayName("Replace - error response test")
    void replaceErrorResponseTest() {
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        when(iniClient.sendReplaceData(any(), any(), any(),any(), any(),any()))
                .thenReturn(registryResponseType);
        ReplaceRequestDTO requestDTO = new ReplaceRequestDTO();
        requestDTO.setRiferimentoIni("identificativoDoc");
        requestDTO.setWorkflowInstanceId(TestConstants.TEST_WII);
        IniResponseDTO response = iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.REPLACE, getIniEdsInvocationTest());
        assertFalse(response.getEsito());
        assertNotNull(response.getMessage());
    }

    @Test
    @DisplayName("Update - success test")
    void updateSuccessTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        RegistryResponseType registryResponseType = TestUtility.mockRegistrySuccess();
        when(iniClient.sendUpdateData(any(), any(), any(),any())).thenReturn(registryResponseType);

        String json = TestConstants.TEST_UPDATE_REQ_NEW ;
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(json, UpdateRequestDTO.class);
        IniResponseDTO iniResponse = iniInvocationSRV.updateByRequestBody(new SubmitObjectsRequest(),updateRequestDTO,false);
        assertTrue(iniResponse.getEsito());
        assertNull(iniResponse.getMessage());
    }

    @Test
    @DisplayName("Update - error test")
    void updateErrorTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        when(iniClient.sendUpdateData(any(), any(), any(),any()))
                .thenThrow(new BusinessException(""));
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ_NEW, UpdateRequestDTO.class);
        assertThrows(BusinessException.class, () -> iniInvocationSRV.updateByRequestBody(new SubmitObjectsRequest(),updateRequestDTO,false));
    }

    @Test
    @DisplayName("Update - error response test")
    void updateErrorResponseTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        when(iniClient.getReferenceMetadata(anyString(), anyString(),any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        when(iniClient.sendUpdateData(any(), any(), any(),any())).thenReturn(registryResponseType);
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ_NEW, UpdateRequestDTO.class);
        assertThrows(BusinessException.class, () -> iniInvocationSRV.updateByRequestBody(null,updateRequestDTO,false));
    }

    @Test
    @DisplayName("Delete - success test")
    void deleteSuccessTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        when(iniClient.getReferenceMetadata(anyString(), anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        when(iniClient.sendDeleteData(any(), any(), any(), any()))
                .thenReturn(new RegistryResponseType());
        DeleteRequestDTO deleteRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_DELETE_REQ, DeleteRequestDTO.class);
        IniResponseDTO iniResponse = iniInvocationSRV.deleteByDocumentId(deleteRequestDTO);
        assertTrue(iniResponse.getEsito());
        assertNull(iniResponse.getMessage());
    }

    @Test
    @DisplayName("Delete - error test")
    void deleteErrorTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        when(iniClient.sendDeleteData(any(), any(), any(), any()))
                .thenThrow(new BusinessException(""));
        DeleteRequestDTO deleteRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_DELETE_REQ, DeleteRequestDTO.class);
        assertThrows(BusinessException.class, () ->iniInvocationSRV.deleteByDocumentId(deleteRequestDTO));
    }

    @Test
    @DisplayName("Delete - error response test")
    void deleteErrorResponseTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        when(iniClient.sendDeleteData(any(), any(), any(), any()))
                .thenReturn(registryResponseType);
        DeleteRequestDTO deleteRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_DELETE_REQ, DeleteRequestDTO.class);
        IniResponseDTO iniResponse = iniInvocationSRV.deleteByDocumentId(deleteRequestDTO);
        assertFalse(iniResponse.getEsito());
        assertNotNull(iniResponse.getMessage());
    }

    @Test
    @DisplayName("Get Metadata - success test")
    void getMetadatiSuccessTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        AdhocQueryResponse apiResponse = iniInvocationSRV.getMetadata("oid", TestUtility.mockBasicToken());
        assertEquals(response, apiResponse);
        assertNotNull(apiResponse);
        assertNotNull(apiResponse.getRegistryObjectList());
        assertNotEquals(0, apiResponse.getRegistryObjectList().getIdentifiable().size());
    }

    @Test
    @DisplayName("Get Metadata - error test when get reference uuid fails")
    void getMetadatiErrorWhenGetReferenceErrorTest() {
        when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenThrow(new BusinessException(""));
        assertThrows(BusinessException.class, () -> iniInvocationSRV.getMetadata("oid", TestUtility.mockBasicToken()));
    }

    private IniEdsInvocationETY getIniEdsInvocationTest(){
        IniEdsInvocationETY ety = new IniEdsInvocationETY();
        ety.setWorkflowInstanceId("testWII1");
        Document documentEntry = new Document()
            .append("mimeType", "application/pdf")
            .append("entryUUID", "uuid-1234")
            .append("creationTime", "2024-08-28T07:00:00Z")
            .append("hash", "abcd1234")
            .append("size", 123456L)
            .append("administrativeRequest", Arrays.asList("MOCK-ADM_REQUEST"))
            .append("status", "active")
            .append("patientId", "testPatientId")
            .append("confidentialityCode", "confCode")
            .append("confidentialityCodeDisplayName", "Confidential")
            .append("typeCode", "typeCode123")
            .append("typeCodeName", "Type Code Name")
            .append("formatCode", "formatCode123")
            .append("formatCodeName", "Format Code Name")
            .append("legalAuthenticator", "authenticatorName")
            .append("sourcePatientInfo", "sourcePatientInfoValue")
            .append("author", "authorName")
            .append("authorRole", "authorRoleValue")
            .append("authorInstitution", "MOCK-AUTHOR_INSTITUTION")
            .append("representedOrganizationName", "Organization Name")
            .append("representedOrganizationCode", "OrgCode123")
            .append("uniqueId", "testIdDoc")
            .append("referenceIdList", Arrays.asList("refId1", "refId2"))
            .append("healthcareFacilityTypeCode", "facilityTypeCode123")
            .append("healthcareFacilityTypeCodeName", "Facility Type Name")
            .append("eventCodeList", Arrays.asList("eventCode1", "eventCode2"))
            .append("description", Arrays.asList("description1", "description2"))
            .append("repositoryUniqueId", "repository123")
            .append("classCode", "classCode123")
            .append("classCodeName", "Class Code Name")
            .append("practiceSettingCode", "practiceCode123")
            .append("practiceSettingCodeName", "Practice Setting Name")
            .append("sourcePatientId", "sourcePatientIdValue")
            .append("serviceStartTime", "2024-08-28T08:00:00Z")
            .append("serviceStopTime", "2024-08-28T10:00:00Z")
            .append("conservazioneANorma", "someValue");

        Document tokenEntry = new Document("payload", JWTPayloadDTO.getMockedDocument());
        List<Document> metadata = new ArrayList<>();
        metadata.add(new Document("submissionSetEntry", new Document()));
        metadata.add(new Document("documentEntry", documentEntry));
        metadata.add(new Document("tokenEntry", tokenEntry));
        ety.setMetadata(metadata);

        return ety;
    }

    @Test
    @DisplayName("Get Reference - No record found scenario")
    void testGetReferenceNoRecordFound() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        response.setTotalResultCount(BigInteger.valueOf(0));
        when(iniClient.getReferenceMetadata(eq("TEST_OID"), any(), any(), any(), any(), any()))
                .thenReturn(response);

        JWTTokenDTO tokenDTO = new JWTTokenDTO();
        JWTPayloadDTO payload = new JWTPayloadDTO();
        tokenDTO.setPayload(payload);
        GetReferenceResponseDTO out = iniInvocationSRV.getReference("TEST_OID", tokenDTO, "WII123");
        assertEquals("No record found", out.getErrorMessage());
        assertNull(out.getUuid());
    }

    @Test
    @DisplayName("Get Reference - Registry error 'No results from the query' scenario")
    void testGetReferenceRegistryErrorNoResults() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        RegistryErrorList errorList = new RegistryErrorList();
        RegistryError error = new RegistryError();
        error.setCodeContext("No results from the query");
        errorList.getRegistryError().add(error);
        response.setRegistryErrorList(errorList);
        response.setTotalResultCount(BigInteger.valueOf(1));

        when(iniClient.getReferenceMetadata(eq("TEST_OID"), any(), any(), any(), any(), any()))
                .thenReturn(response);

        JWTTokenDTO tokenDTO = new JWTTokenDTO();
        JWTPayloadDTO payload = new JWTPayloadDTO();
        tokenDTO.setPayload(payload);
        assertThrows(IdDocumentNotFoundException.class, () -> {
            iniInvocationSRV.getReference("TEST_OID", tokenDTO, "WII123");
        });
    }

    @Test
    @DisplayName("Get Reference - Registry error with different codeContext")
    void testGetReferenceRegistryErrorDifferentContext() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        RegistryErrorList errorList = new RegistryErrorList();
        RegistryError error = new RegistryError();
        error.setCodeContext("Some other error");
        errorList.getRegistryError().add(error);
        response.setRegistryErrorList(errorList);
        response.setTotalResultCount(BigInteger.valueOf(1));

        when(iniClient.getReferenceMetadata(eq("TEST_OID"), any(), any(), any(), any(), any()))
                .thenReturn(response);

        JWTTokenDTO tokenDTO = new JWTTokenDTO();
        JWTPayloadDTO payload = new JWTPayloadDTO();
        tokenDTO.setPayload(payload);
        GetReferenceResponseDTO out = iniInvocationSRV.getReference("TEST_OID", tokenDTO, "WII123");
        assertTrue(out.getErrorMessage().contains("Some other error"));
    }

    @Test
    @DisplayName("Get Reference - Success scenario")
    void testGetReferenceSuccess() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        response.setTotalResultCount(BigInteger.valueOf(1));

        ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();
        extrinsicObject.setId("uuid-12345");

        JAXBElement<IdentifiableType> identifiable = new JAXBElement<>(
                new QName("ExtrinsicObject"),
                IdentifiableType.class,
                extrinsicObject
        );
        response.setRegistryObjectList(new RegistryObjectListType());
        response.getRegistryObjectList().getIdentifiable().add(identifiable);


        when(iniClient.getReferenceMetadata(eq("TEST_OID"), any(), any(), any(), any(), any()))
                .thenReturn(response);
        JWTTokenDTO tokenDTO = new JWTTokenDTO();
        JWTPayloadDTO payload = new JWTPayloadDTO();
        tokenDTO.setPayload(payload);
        GetReferenceResponseDTO out = iniInvocationSRV.getReference("TEST_OID", tokenDTO, "WII123");
        assertNull(out.getErrorMessage());
        assertEquals(1, out.getUuid().size());
        assertEquals("uuid-12345", out.getUuid().get(0));
    }


}
