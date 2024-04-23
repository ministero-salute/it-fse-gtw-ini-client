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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentTreeDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.enums.SearchTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.PublishReplaceBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.SubmissionSetEntryBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.delete.DeleteBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.read.ReadBodyBuilderUtility;
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
    	samlHeaderBuilderUtility.initialize();
        Document entity = JsonUtility.jsonToObject(TestConstants.TEST_INI_EDS_ENTRY, Document.class);
        mongoTemplate.save(entity, Constants.Profile.TEST_PREFIX + Constants.Collections.INI_EDS_INVOCATION);
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
        assertTrue(documentTreeDTO.checkIntegrity());
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
        SubmissionSetEntryBuilderUtility.setObjectFactory(Mockito.mock(ObjectFactory.class));
        ObjectFactory objectFactory = SubmissionSetEntryBuilderUtility.getObjectFactory();
        when(objectFactory.createRegistryPackage(any(RegistryPackageType.class))).thenThrow(new BusinessException("Error creating object"));
        when(objectFactory.createSlot(any(SlotType1.class))).thenThrow(new BusinessException("Error creating object"));
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
        MergedMetadatiRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ_NEW, MergedMetadatiRequestDTO.class);
        JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
        jwtTokenDTO.setPayload(updateRequestDTO.getToken());
        JWTTokenDTO reconfiguredToken = RequestUtility.configureReadTokenPerAction(jwtTokenDTO, ActionEnumType.UPDATE);
        RegistryObjectListType oldMetadata = new RegistryObjectListType();
        assertThrows(BusinessException.class, () -> UpdateBodyBuilderUtility.buildSubmitObjectRequest(oldMetadata,
                updateRequestDTO,
                TestConstants.TEST_UUID,
                reconfiguredToken,""
        ));
     
    }

    @Test
    @DisplayName("Update - merge metadata success test")
    void updateBodyBuilderSuccessTest() throws JAXBException {
        MergedMetadatiRequestDTO updateRequestDTO = JsonUtility.jsonToObject(TestConstants.TEST_UPDATE_REQ_WITH_BODY, MergedMetadatiRequestDTO.class);
        JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
        jwtTokenDTO.setPayload(updateRequestDTO.getToken());
        JWTTokenDTO reconfiguredToken = RequestUtility.configureReadTokenPerAction(jwtTokenDTO, ActionEnumType.UPDATE);
        RegistryObjectListType oldMetadata = TestUtility.mockQueryResponse().getRegistryObjectList();
        assertDoesNotThrow(() -> UpdateBodyBuilderUtility.buildSubmitObjectRequest(oldMetadata,
                updateRequestDTO,
                TestConstants.TEST_UUID,
                reconfiguredToken,""
        ));
    }

    @Test
    @DisplayName("PUBLISH - Header builder success test")
    void publishHeaderBuilderSuccessTest() {
        IniEdsInvocationETY entity = iniInvocationRepo.findByWorkflowInstanceId(TestConstants.TEST_WII);
        DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(entity.getMetadata());
        assertDoesNotThrow(() -> samlHeaderBuilderUtility.buildHeader(
                new JWTTokenDTO(samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload()),
                ActionEnumType.CREATE
        ));
    }

    @Test
    @DisplayName("UPDATE - Header builder success test")
    void updateHeaderBuilderSuccessTest() {
        IniEdsInvocationETY entity = iniInvocationRepo.findByWorkflowInstanceId(TestConstants.TEST_WII);
        DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(entity.getMetadata());
        assertDoesNotThrow(() -> samlHeaderBuilderUtility.buildHeader(
                new JWTTokenDTO(samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload()),
                ActionEnumType.UPDATE
        ));
    }

    @Test
    @DisplayName("REPLACE - Header builder success test")
    void replaceHeaderBuilderSuccessTest() {
        IniEdsInvocationETY entity = iniInvocationRepo.findByWorkflowInstanceId(TestConstants.TEST_WII);
        DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(entity.getMetadata());
        assertDoesNotThrow(() -> samlHeaderBuilderUtility.buildHeader(
                new JWTTokenDTO(samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload()),
                ActionEnumType.REPLACE
        ));
    }

    @Test
    @DisplayName("DELETE - Header builder success test")
    void deleteHeaderBuilderSuccessTest() {
        IniEdsInvocationETY entity = iniInvocationRepo.findByWorkflowInstanceId(TestConstants.TEST_WII);
        DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(entity.getMetadata());
        assertDoesNotThrow(() -> samlHeaderBuilderUtility.buildHeader(
                new JWTTokenDTO(samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry()).getPayload()),
                ActionEnumType.DELETE
        ));
    }

    @Test
    @DisplayName("READ REF - Header builder success test")
    void readReferenceBuilderSuccessTest() {
        assertDoesNotThrow(() -> ReadBodyBuilderUtility.buildAdHocQueryRequest("searchId", SearchTypeEnum.LEAF_CLASS.getSearchKey()));
    }

    @Test
    @DisplayName("READ METADATA - Header builder success test")
    void readMetadataBuilderSuccessTest() {
        assertDoesNotThrow(() -> ReadBodyBuilderUtility.buildAdHocQueryRequest("searchId", SearchTypeEnum.LEAF_CLASS.getSearchKey()));
    }
}
