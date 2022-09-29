package it.finanze.sanita.fse2.ms.iniclient;

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
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        JWTTokenDTO reconfiguredToken = RequestUtility.configureTokenPerAction(jwtTokenDTO, ActionEnumType.UPDATE);
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
        JWTTokenDTO reconfiguredToken = RequestUtility.configureTokenPerAction(jwtTokenDTO, ActionEnumType.UPDATE);

        assertDoesNotThrow(() -> UpdateBodyBuilderUtility.buildSubmitObjectRequest(
                updateRequestDTO,
                TestUtility.mockQueryResponse().getRegistryObjectList(),
                TestConstants.TEST_UUID,
                reconfiguredToken
        ));
    }
}
