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

import javax.xml.bind.JAXBException;

import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IniInvocationTest {

    @Autowired
    private IIniInvocationSRV iniInvocationSRV;

    @Autowired
    private MongoTemplate mongoTemplate;

    @MockBean
    private IIniClient iniClient;

    @BeforeEach
    void dataInit() {
        Document entity = JsonUtility.jsonToObject(TestConstants.TEST_INI_EDS_ENTRY, Document.class);
        mongoTemplate.save(entity, Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.INI_EDS_INVOCATION);
    }

    @AfterEach
    void dropCollection() {
        mongoTemplate.dropCollection(IniEdsInvocationETY.class);
    }

    @Test
    @DisplayName("Publish - success test")
    void publishSuccessTest() {
        Mockito.when(iniClient.sendPublicationData(any(Document.class), any(Document.class), any(Document.class)))
                .thenReturn(new RegistryResponseType());
        IniResponseDTO response = iniInvocationSRV.publishByWorkflowInstanceId(TestConstants.TEST_WII);
        assertTrue(response.getEsito());
        assertNull(response.getErrorMessage());
    }

    @Test
    @DisplayName("Publish - error test")
    void publishErrorTest() {
        Mockito.when(iniClient.sendPublicationData(any(Document.class), any(Document.class), any(Document.class)))
                .thenThrow(new BusinessException(""));
        IniResponseDTO response = iniInvocationSRV.publishByWorkflowInstanceId(TestConstants.TEST_WII);
        assertFalse(response.getEsito());
        assertNotNull(response.getErrorMessage());
    }

    @Test
    @DisplayName("Publish - error response test")
    void publishErrorResponseTest() {
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        Mockito.when(iniClient.sendPublicationData(any(Document.class), any(Document.class), any(Document.class)))
                .thenReturn(registryResponseType);
        IniResponseDTO response = iniInvocationSRV.publishByWorkflowInstanceId(TestConstants.TEST_WII);
        assertFalse(response.getEsito());
        assertNotNull(response.getErrorMessage());
    }

    @Test
    @DisplayName("Replace - success test")
    void replaceSuccessTest() {
        Mockito.when(iniClient.sendReplaceData(any(ReplaceRequestDTO.class), any(Document.class), any(Document.class), any(Document.class)))
                .thenReturn(new RegistryResponseType());
        ReplaceRequestDTO requestDTO = new ReplaceRequestDTO();
        requestDTO.setIdDoc("identificativoDoc");
        requestDTO.setWorkflowInstanceId(TestConstants.TEST_WII);
        IniResponseDTO response = iniInvocationSRV.replaceByWorkflowInstanceId(requestDTO);
        assertTrue(response.getEsito());
        assertNull(response.getErrorMessage());
    }

    @Test
    @DisplayName("Replace - error test")
    void replaceErrorTest() {
        Mockito.when(iniClient.sendReplaceData(any(ReplaceRequestDTO.class), any(Document.class), any(Document.class), any(Document.class)))
                .thenThrow(new BusinessException(""));
        ReplaceRequestDTO requestDTO = new ReplaceRequestDTO();
        requestDTO.setIdDoc("identificativoDoc");
        requestDTO.setWorkflowInstanceId(TestConstants.TEST_WII);
        IniResponseDTO response = iniInvocationSRV.replaceByWorkflowInstanceId(requestDTO);
        assertFalse(response.getEsito());
        assertNotNull(response.getErrorMessage());
    }

    @Test
    @DisplayName("Replace - error response test")
    void replaceErrorResponseTest() {
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        Mockito.when(iniClient.sendReplaceData(any(ReplaceRequestDTO.class), any(Document.class), any(Document.class), any(Document.class)))
                .thenReturn(registryResponseType);
        ReplaceRequestDTO requestDTO = new ReplaceRequestDTO();
        requestDTO.setIdDoc("identificativoDoc");
        requestDTO.setWorkflowInstanceId(TestConstants.TEST_WII);
        IniResponseDTO response = iniInvocationSRV.replaceByWorkflowInstanceId(requestDTO);
        assertFalse(response.getEsito());
        assertNotNull(response.getErrorMessage());
    }

    @Test
    @DisplayName("Update - success test")
    void updateSuccessTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(), any(JWTTokenDTO.class)))
                .thenReturn("uuid");
        Mockito.when(iniClient.getReferenceMetadata(anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        RegistryResponseType registryResponseType = TestUtility.mockRegistrySuccess();
        Mockito.when(iniClient.sendUpdateData(any(UpdateRequestDTO.class), any(), any()))
                .thenReturn(registryResponseType);
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ, UpdateRequestDTO.class);
        IniResponseDTO iniResponse = iniInvocationSRV.updateByRequestBody(updateRequestDTO);
        assertTrue(iniResponse.getEsito());
        assertNull(iniResponse.getErrorMessage());
    }

    @Test
    @DisplayName("Update - error test")
    void updateErrorTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(), any(JWTTokenDTO.class)))
                .thenReturn("uuid");
        Mockito.when(iniClient.getReferenceMetadata(anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.sendUpdateData(any(UpdateRequestDTO.class), any(), any()))
                .thenThrow(new BusinessException(""));
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ, UpdateRequestDTO.class);
        IniResponseDTO iniResponse = iniInvocationSRV.updateByRequestBody(updateRequestDTO);
        assertFalse(iniResponse.getEsito());
        assertNotNull(iniResponse.getErrorMessage());
    }

    @Test
    @DisplayName("Update - error response test")
    void updateErrorResponseTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(), any(JWTTokenDTO.class)))
                .thenReturn("uuid");
        Mockito.when(iniClient.getReferenceMetadata(anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        Mockito.when(iniClient.sendUpdateData(any(UpdateRequestDTO.class), any(), any()))
                .thenReturn(registryResponseType);
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ, UpdateRequestDTO.class);
        IniResponseDTO iniResponse = iniInvocationSRV.updateByRequestBody(updateRequestDTO);
        assertFalse(iniResponse.getEsito());
        assertNotNull(iniResponse.getErrorMessage());
    }

    @Test
    @DisplayName("Delete - success test")
    void deleteSuccessTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(), any(JWTTokenDTO.class)))
                .thenReturn("uuid");
        Mockito.when(iniClient.getReferenceMetadata(anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.sendDeleteData(anyString(), any(JWTPayloadDTO.class), anyString()))
                .thenReturn(new RegistryResponseType());
        DeleteRequestDTO deleteRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_DELETE_REQ, DeleteRequestDTO.class);
        IniResponseDTO iniResponse = iniInvocationSRV.deleteByDocumentId(deleteRequestDTO);
        assertTrue(iniResponse.getEsito());
        assertNull(iniResponse.getErrorMessage());
    }

    @Test
    @DisplayName("Delete - error test")
    void deleteErrorTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(), any(JWTTokenDTO.class)))
                .thenReturn("uuid");
        Mockito.when(iniClient.getReferenceMetadata(anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        Mockito.when(iniClient.sendDeleteData(anyString(), any(JWTPayloadDTO.class), anyString()))
                .thenThrow(new BusinessException(""));
        DeleteRequestDTO deleteRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_DELETE_REQ, DeleteRequestDTO.class);
        IniResponseDTO iniResponse = iniInvocationSRV.deleteByDocumentId(deleteRequestDTO);
        assertFalse(iniResponse.getEsito());
        assertNotNull(iniResponse.getErrorMessage());
    }

    @Test
    @DisplayName("Delete - error response test")
    void deleteErrorResponseTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(), any(JWTTokenDTO.class)))
                .thenReturn("uuid");
        Mockito.when(iniClient.getReferenceMetadata(anyString(), any(JWTTokenDTO.class)))
                .thenReturn(response);
        RegistryResponseType registryResponseType = TestUtility.mockRegistryError();
        Mockito.when(iniClient.sendDeleteData(anyString(), any(JWTPayloadDTO.class), anyString()))
                .thenReturn(registryResponseType);
        DeleteRequestDTO deleteRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_DELETE_REQ, DeleteRequestDTO.class);
        IniResponseDTO iniResponse = iniInvocationSRV.deleteByDocumentId(deleteRequestDTO);
        assertFalse(iniResponse.getEsito());
        assertNotNull(iniResponse.getErrorMessage());
    }

    @Test
    @DisplayName("Get Metadata - success test")
    void getMetadatiSuccessTest() throws JAXBException {
        AdhocQueryResponse response = TestUtility.mockQueryResponse();
        Mockito.when(iniClient.getReferenceUUID(anyString(), any(JWTTokenDTO.class)))
                .thenReturn("uuid");
        Mockito.when(iniClient.getReferenceMetadata(anyString(), any(JWTTokenDTO.class)))
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
        Mockito.when(iniClient.getReferenceUUID(anyString(), any(JWTTokenDTO.class)))
                .thenThrow(new BusinessException(""));
        assertThrows(BusinessException.class, () -> iniInvocationSRV.getMetadata("oid", TestUtility.mockBasicToken()));
    }

    @Test
    @DisplayName("Get Metadata - error test when get reference uuid fails")
    void getMetadatiGenericErrorTest() {
        Mockito.when(iniClient.getReferenceUUID(anyString(), any(JWTTokenDTO.class)))
                .thenReturn("uuid");
        Mockito.when(iniClient.getReferenceMetadata(anyString(), any(JWTTokenDTO.class)))
                .thenThrow(new BusinessException(""));
        assertThrows(BusinessException.class, () -> iniInvocationSRV.getMetadata("oid", TestUtility.mockBasicToken()));
    }
}
