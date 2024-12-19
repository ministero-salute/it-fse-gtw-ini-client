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

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.Profile.TEST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.Collections;
import java.util.Optional;
import javax.xml.bind.JAXBElement;

import lombok.var;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentTreeDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.enums.DocumentTypeEnum;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST)
class CommonUtilityTest {


    @Test
    void testPrivateConstructor() throws Exception {
        var constructor = CommonUtility.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void testExtractDocumentEntry() {
        Document doc = new Document("hash", "102312");
        DocumentEntryDTO dto = CommonUtility.extractDocumentEntry(doc);
        assertNotNull(dto);
    }

    @Test
    void testExtractSubmissionSetEntry() {
        Document doc = new Document("sourceId", "TEST");
        SubmissionSetEntryDTO dto = CommonUtility.extractSubmissionSetEntry(doc);
        assertNotNull(dto);
    }

    @Test
    void testBuildJwtPayloadFromDeleteRequest() {
        DeleteRequestDTO deleteRequestDTO = new DeleteRequestDTO();
        deleteRequestDTO.setAction_id("action");
        deleteRequestDTO.setPatient_consent(true);
        JWTPayloadDTO payload = CommonUtility.buildJwtPayloadFromDeleteRequest(deleteRequestDTO);
        assertEquals("action", payload.getAction_id());
    }

    @Test
    void testExtractFiscalCodeFromDocumentTree_NullDTO() {
        String result = CommonUtility.extractFiscalCodeFromDocumentTree(null);
        assertEquals(Constants.IniClientConstants.JWT_MISSING_SUBJECT, result);
    }

    @Test
    void testExtractFiscalCodeFromDocumentTree_NoPayload() {
        DocumentTreeDTO dto = new DocumentTreeDTO();
        dto.setTokenEntry(new Document());
        String result = CommonUtility.extractFiscalCodeFromDocumentTree(dto);
        assertEquals(Constants.IniClientConstants.JWT_MISSING_SUBJECT, result);
    }

    @Test
    void testExtractFiscalCodeFromDocumentTree_ValidPayload() {
        DocumentTreeDTO dto = new DocumentTreeDTO();
        Document payload = new Document();
        payload.put("sub", "FISCALCODE12345&" + Constants.OIDS.OID_MEF);
        Document tokenEntry = new Document();
        tokenEntry.put("payload", payload);
        dto.setTokenEntry(tokenEntry);

        String result = CommonUtility.extractFiscalCodeFromDocumentTree(dto);
        assertEquals("FISCALCODE12345", result);
    }

    @Test
    void testExtractFiscalCodeFromJwtSub_NullSub() {
        String result = CommonUtility.extractFiscalCodeFromJwtSub(null);
        assertEquals(Constants.IniClientConstants.JWT_MISSING_SUBJECT, result);
    }

    @Test
    void testExtractFiscalCodeFromJwtSub_InvalidFormat() {
        String result = CommonUtility.extractFiscalCodeFromJwtSub("invalid_sub");
        assertEquals(Constants.IniClientConstants.JWT_MISSING_SUBJECT, result);
    }

    @Test
    void testCheckMetadata_NullResponse() {
        assertFalse(CommonUtility.checkMetadata(null));
    }

    @Test
    void testCheckMetadata_EmptyResponse() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        response.setRegistryObjectList(new RegistryObjectListType());
        assertFalse(CommonUtility.checkMetadata(response));
    }

    @Test
    void testExtractDocumentTypeFromQueryResponse_NoMetadata() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        String type = CommonUtility.extractDocumentTypeFromQueryResponse(response);
        assertEquals(Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER, type);
    }

    @Test
    void testExtractAuthorInstitutionFromQueryResponse_NoMetadata() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        String institution = CommonUtility.extractAuthorInstitutionFromQueryResponse(response);
        assertEquals(Constants.IniClientConstants.MISSING_AUTHOR_INSTITUTION_PLACEHOLDER, institution);
    }

    @Test
    void testExtractAdministrativeRequestFromQueryResponse_NoMetadata() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        assertTrue(CommonUtility.extractAdministrativeRequestFromQueryResponse(response).isEmpty());
    }

    @Test
    void testBuildAuthorSlot() {
        var authorSlot = CommonUtility.buildAuthorSlot("role", "institution", "person");
        assertNotNull(authorSlot);
        assertEquals("role", authorSlot.getAuthorRoleSlot().getValueList().getValue().get(0));

    }

    @Test
    void testExtractDocumentTypeFromQueryResponse_ValidMetadata() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        RegistryObjectListType rol = new RegistryObjectListType();

        ExtrinsicObjectType ext = new ExtrinsicObjectType();
        ClassificationType ct = new ClassificationType();
        ct.setClassificationScheme("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983");

        LocalizedStringType lst = new LocalizedStringType();
        lst.setValue("SomeDocType");
        InternationalStringType ist = new InternationalStringType();
        ist.getLocalizedString().add(lst);
        ct.setName(ist);

        ext.getClassification().add(ct);

        JAXBElement<ExtrinsicObjectType> element = mock(JAXBElement.class);
        when(element.getValue()).thenReturn(ext);

        rol.getIdentifiable().add(element);
        response.setRegistryObjectList(rol);

        String docType = CommonUtility.extractDocumentTypeFromQueryResponse(response);
        assertEquals("SomeDocType", docType);
    }

    @Test
    void testExtractAuthorInstitutionFromQueryResponse_ValidMetadata() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        RegistryObjectListType rol = new RegistryObjectListType();

        ExtrinsicObjectType ext = new ExtrinsicObjectType();
        ClassificationType authorClassification = new ClassificationType();
        authorClassification.setClassificationScheme("urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d");

        SlotType1 slot = new SlotType1();
        slot.setName("authorInstitution");
        ValueListType vlt = new ValueListType();
        vlt.getValue().add("TestInstitution");
        slot.setValueList(vlt);
        authorClassification.getSlot().add(slot);

        ext.getClassification().add(authorClassification);

        JAXBElement<ExtrinsicObjectType> element = mock(JAXBElement.class);
        when(element.getValue()).thenReturn(ext);

        rol.getIdentifiable().add(element);
        response.setRegistryObjectList(rol);

        String institution = CommonUtility.extractAuthorInstitutionFromQueryResponse(response);
        assertEquals("TestInstitution", institution);
    }

    @Test
    void testExtractAdministrativeRequestFromQueryResponse_ValidMetadata() {
        AdhocQueryResponse response = new AdhocQueryResponse();
        RegistryObjectListType rol = new RegistryObjectListType();

        ExtrinsicObjectType ext = new ExtrinsicObjectType();
        SlotType1 adminSlot = new SlotType1();
        adminSlot.setName("administrativeRequestSomething");
        ValueListType vlt = new ValueListType();
        vlt.getValue().add("Request1");
        vlt.getValue().add("Request2");
        adminSlot.setValueList(vlt);

        ext.getSlot().add(adminSlot);

        JAXBElement<ExtrinsicObjectType> element = mock(JAXBElement.class);
        when(element.getValue()).thenReturn(ext);

        rol.getIdentifiable().add(element);
        response.setRegistryObjectList(rol);

        var requests = CommonUtility.extractAdministrativeRequestFromQueryResponse(response);
        assertEquals(2, requests.size());
        assertTrue(requests.contains("Request1"));
        assertTrue(requests.contains("Request2"));
    }
}
