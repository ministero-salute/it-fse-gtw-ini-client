package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentTreeDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.PublishReplaceBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.delete.DeleteBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.UpdateBodyBuilderUtility;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestBuilderTest {

    @Autowired
    private IniInvocationRepo iniInvocationRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SamlHeaderBuilderUtility samlHeaderBuilderUtility;

    @BeforeAll
    void dataInit() {
        Document entity = JsonUtility.jsonToObject(TestConstants.TEST_INI_EDS_ENTRY, Document.class);
        mongoTemplate.save(entity, Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.INI_EDS_INVOCATION);
    }

    @AfterAll
    void dropCollection() {
        mongoTemplate.dropCollection(IniEdsInvocationETY.class);
    }

    @Test
    @DisplayName("Publish - success test")
    void createBodyBuilderSuccessTest() {
        IniEdsInvocationETY entity = iniInvocationRepo.findByWorkflowInstanceId(TestConstants.TEST_WII);
        DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(entity.getMetadata());
        assertDoesNotThrow(() -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(
                CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry()),
                CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry()),
                samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload(),
                null
        ));
    }

    @Test
    @DisplayName("Publish - Error test when invalid body")
    void createBodyBuilderGenericErrorTest() {
        DocumentTreeDTO documentTreeDTO = new DocumentTreeDTO();
        documentTreeDTO.setDocumentEntry(new Document());
        documentTreeDTO.setTokenEntry(new Document());
        documentTreeDTO.setSubmissionSetEntry(new Document());
        assertThrows(BusinessException.class, () -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(
                CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry()),
                CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry()),
                samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload(),
                null
        ));
    }

    @Test
    @DisplayName("Publish - Exception test when failed to create objectFactory")
    void createBodyBuilderGenericExceptionTest() {
        IniEdsInvocationETY entity = iniInvocationRepo.findByWorkflowInstanceId(TestConstants.TEST_WII);
        DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(entity.getMetadata());
        PublishReplaceBodyBuilderUtility.setObjectFactory(Mockito.mock(ObjectFactory.class));
        ObjectFactory objectFactory = PublishReplaceBodyBuilderUtility.getObjectFactory();
        when(objectFactory.createRegistryPackage(any(RegistryPackageType.class))).thenThrow(new BusinessException("Error creating object"));
        assertThrows(BusinessException.class, () -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(
                CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry()),
                CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry()),
                samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload(),
                null
        ));

        when(objectFactory.createClassification(any(ClassificationType.class))).thenThrow(new BusinessException("Error creating object"));
        assertThrows(BusinessException.class, () -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(
                CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry()),
                CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry()),
                samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload(),
                null
        ));

        when(objectFactory.createSlot(any(SlotType1.class))).thenThrow(new BusinessException("Error creating object"));
        assertThrows(BusinessException.class, () -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(
                CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry()),
                CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry()),
                samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload(),
                null
        ));

        when(objectFactory.createAssociation(any(AssociationType1.class))).thenThrow(new BusinessException("Error creating object"));
        assertThrows(BusinessException.class, () -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(
                CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry()),
                CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry()),
                samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload(),
                null
        ));

        when(objectFactory.createExternalIdentifier(any(ExternalIdentifierType.class))).thenThrow(new BusinessException("Error creating object"));
        assertThrows(BusinessException.class, () -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(
                CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry()),
                CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry()),
                samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload(),
                null
        ));

        when(objectFactory.createExtrinsicObject(any(ExtrinsicObjectType.class))).thenThrow(new BusinessException("Error creating object"));
        assertThrows(BusinessException.class, () -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(
                CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry()),
                CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry()),
                samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload(),
                null
        ));
    }

    @Test
    @DisplayName("Replace - success test")
    void replaceBodyBuilderSuccessTest() {
        IniEdsInvocationETY entity = iniInvocationRepo.findByWorkflowInstanceId(TestConstants.TEST_WII);
        DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(entity.getMetadata());
        assertDoesNotThrow(() -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(
                CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry()),
                CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry()),
                samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload(),
                TestConstants.TEST_UUID
        ));
    }

    @Test
    @DisplayName("Delete - success test")
    void deleteBodyBuilderSuccessTest() {
        assertDoesNotThrow(() -> DeleteBodyBuilderUtility.buildRemoveObjectsRequest(TestConstants.TEST_UUID));
    }

    @Test
    @DisplayName("Update - Extrinsic object not found")
    void errorUpdateBodyBuilderTest() {
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ, UpdateRequestDTO.class);
        JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
        jwtTokenDTO.setPayload(updateRequestDTO.getToken());
        JWTTokenDTO reconfiguredToken = RequestUtility.configureReadTokenPerAction(jwtTokenDTO, ActionEnumType.UPDATE);
        RegistryObjectListType oldMetadata = new RegistryObjectListType();
        assertThrows(BusinessException.class, () -> UpdateBodyBuilderUtility.buildSubmitObjectRequest(
                updateRequestDTO,
                oldMetadata,
                TestConstants.TEST_UUID,
                reconfiguredToken
        ));
    }

    @Test
    @DisplayName("Update - success test")
    void successUpdateBodyBuilderTest() throws JAXBException, IOException {
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ, UpdateRequestDTO.class);
        JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
        jwtTokenDTO.setPayload(updateRequestDTO.getToken());
        JWTTokenDTO reconfiguredToken = RequestUtility.configureReadTokenPerAction(jwtTokenDTO, ActionEnumType.UPDATE);

        assertDoesNotThrow(() -> UpdateBodyBuilderUtility.buildSubmitObjectRequest(
                updateRequestDTO,
                TestUtility.mockQueryResponse().getRegistryObjectList(),
                TestConstants.TEST_UUID,
                reconfiguredToken
        ));
    }
}
