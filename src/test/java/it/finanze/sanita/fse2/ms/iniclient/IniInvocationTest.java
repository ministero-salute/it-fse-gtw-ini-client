
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import ihe.iti.xds_b._2007.DocumentRegistryPortType;
import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ReplaceRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
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
        Mockito.when(config.isControlLogPersistenceEnable()).thenReturn(true);
    }

    @AfterEach
    void dropCollection() {
        mongoTemplate.dropCollection(IniEdsInvocationETY.class);
    }

    @Test
    @DisplayName("Publish - success test")
    void publishSuccessTest() {
        Mockito.when(iniClient.sendPublicationData(any(DocumentEntryDTO.class), any(SubmissionSetEntryDTO.class), any(JWTTokenDTO.class)
        		, any(String.class), any(Date.class)))
                .thenReturn(new RegistryResponseType());
        IniResponseDTO response = iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.PUBLISH,getIniEdsInvocationTest());
        assertTrue(response.getEsito());
        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Publish - error test")
    void publishErrorTest() {
        Mockito.when(iniClient.sendPublicationData(any(DocumentEntryDTO.class), any(SubmissionSetEntryDTO.class), any(JWTTokenDTO.class), any(String.class), any(Date.class)))
                .thenThrow(new BusinessException(""));
        IniEdsInvocationETY ety = getIniEdsInvocationTest();
        assertThrows(BusinessException.class, () -> iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.PUBLISH,ety));
    }


    @Test
    @DisplayName("Publish - error response test")
    void publishErrorResponseTest() {
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        Mockito.when(iniClient.sendPublicationData(any(DocumentEntryDTO.class), any(SubmissionSetEntryDTO.class), any(JWTTokenDTO.class), any(String.class), any(Date.class)))
                .thenReturn(registryResponseType);
        IniResponseDTO response = iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.PUBLISH,getIniEdsInvocationTest());
        assertFalse(response.getEsito());
        assertNotNull(response.getMessage());
    }

    @Test
    @DisplayName("Replace - success test")
    void replaceSuccessTest() {
        RegistryResponseType registryResponseType = TestUtility.mockRegistrySuccess();
        Mockito.when(iniClient.sendReplaceData(any(), any(), any(),any(), any(),any()))
                .thenReturn(registryResponseType);
        IniResponseDTO response = iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.REPLACE,getIniEdsInvocationTest());
        assertTrue(response.getEsito());
        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Replace - error test")
    void replaceErrorTest() {
        Mockito.when(iniClient.sendReplaceData(any(), any(), any(), any(), any(),any())).thenThrow(new BusinessException(""));
        ReplaceRequestDTO requestDTO = new ReplaceRequestDTO();
        requestDTO.setRiferimentoIni("identificativoDoc");
        requestDTO.setWorkflowInstanceId(TestConstants.TEST_WII);
        assertThrows(BusinessException.class, () -> iniInvocationSRV.publishOrReplaceOnIni(TestConstants.TEST_WII, ProcessorOperationEnum.REPLACE,getIniEdsInvocationTest()));
    }

    @Test
    @DisplayName("Replace - error response test")
    void replaceErrorResponseTest() {
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        Mockito.when(iniClient.sendReplaceData(any(), any(), any(),any(), any(),any()))
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
        Mockito.when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        RegistryResponseType registryResponseType = TestUtility.mockRegistrySuccess();
        Mockito.when(iniClient.sendUpdateData(any(), any(), any(),any())).thenReturn(registryResponseType);

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
        Mockito.when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        Mockito.when(iniClient.sendUpdateData(any(), any(), any(),any()))
                .thenThrow(new BusinessException(""));
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ_NEW, UpdateRequestDTO.class);
        assertThrows(BusinessException.class, () -> iniInvocationSRV.updateByRequestBody(new SubmitObjectsRequest(),updateRequestDTO,false));
    }

    @Test
    @DisplayName("Update - error response test")
    void updateErrorResponseTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.getReferenceMetadata(anyString(), anyString(),any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        Mockito.when(iniClient.sendUpdateData(any(), any(), any(),any())).thenReturn(registryResponseType);
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ_NEW, UpdateRequestDTO.class);
        assertThrows(BusinessException.class, () -> iniInvocationSRV.updateByRequestBody(null,updateRequestDTO,false));
    }

    @Test
    @DisplayName("Delete - success test")
    void deleteSuccessTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.getReferenceMetadata(anyString(), anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        Mockito.when(iniClient.sendDeleteData(any(), any(), any(), any()))
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
        Mockito.when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        Mockito.when(iniClient.sendDeleteData(any(), any(), any(), any()))
                .thenThrow(new BusinessException(""));
        DeleteRequestDTO deleteRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_DELETE_REQ, DeleteRequestDTO.class);
        assertThrows(BusinessException.class, () ->iniInvocationSRV.deleteByDocumentId(deleteRequestDTO));
    }

    @Test
    @DisplayName("Delete - error response test")
    void deleteErrorResponseTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
                .thenReturn(response);
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        Mockito.when(iniClient.sendDeleteData(any(), any(), any(), any()))
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
        Mockito.when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.getReferenceMetadata(anyString(),anyString(), any(JWTTokenDTO.class),anyString()))
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
        Mockito.when(iniClient.getReferenceUUID(anyString(),anyString(), any(JWTTokenDTO.class)))
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
}
