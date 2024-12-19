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

import it.finanze.sanita.fse2.ms.iniclient.dto.PublicationMetadataReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.*;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MergeMetadataUtilityTest {

    private PublicationMetadataReqDTO updateRequestDTO;
    private ExtrinsicObjectType extrinsicObject;

    @BeforeEach
    void setUp() {
        updateRequestDTO = new PublicationMetadataReqDTO();
        updateRequestDTO.setTipologiaStruttura(HealthcareFacilityEnum.Cittadino);
        updateRequestDTO.setAssettoOrganizzativo(PracticeSettingCodeEnum.AD_PSC001);
        updateRequestDTO.setTipoDocumentoLivAlto(TipoDocAltoLivEnum.CER);
        updateRequestDTO.setAttiCliniciRegoleAccesso(Arrays.asList("J07AC", "P99"));
        updateRequestDTO.setDataInizioPrestazione("20240101");
        updateRequestDTO.setDataFinePrestazione("20241231");
        updateRequestDTO.setTipoAttivitaClinica(AttivitaClinicaEnum.CON);
        updateRequestDTO.setIdentificativoSottomissione("SUB_ID");
        updateRequestDTO.setAdministrativeRequest(Collections.singletonList(AdministrativeReqEnum.valueOf(AdministrativeReqEnum.DONOR.name())));
        updateRequestDTO.setDescription(Collections.singletonList("DescValue"));

        extrinsicObject = new ExtrinsicObjectType();
    }

    @Test
    void testMergeHealthcareFacilityTypeCodeNormal() {
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeHealthcareFacilityTypeCode(updateRequestDTO, extrinsicObject));
        assertFalse(extrinsicObject.getClassification().isEmpty());
    }

    @Test
    void testMergeHealthcareFacilityTypeCodeNoAssettoOrganizzativo() {
        updateRequestDTO.setAssettoOrganizzativo(null);
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeHealthcareFacilityTypeCode(updateRequestDTO, extrinsicObject));
    }

    @Test
    void testMergeClassCodeNormal() {
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeClassCode(updateRequestDTO, extrinsicObject));
        assertFalse(extrinsicObject.getClassification().isEmpty());
    }

    @Test
    void testMergeClassCodeNoAssettoOrganizzativo() {
        updateRequestDTO.setAssettoOrganizzativo(null);
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeClassCode(updateRequestDTO, extrinsicObject));
    }

    @Test
    void testMergePracticeSettingCodeNormal() {
        assertDoesNotThrow(() -> MergeMetadataUtility.mergePracticeSettingCode(updateRequestDTO, extrinsicObject));
        assertFalse(extrinsicObject.getClassification().isEmpty());
    }

    @Test
    void testMergePracticeSettingCodeNoAssettoOrganizzativo() {
        updateRequestDTO.setAssettoOrganizzativo(null);
        assertDoesNotThrow(() -> MergeMetadataUtility.mergePracticeSettingCode(updateRequestDTO, extrinsicObject));
    }

    @Test
    void testMergeEventTypeCodeNormal() {
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeEventTypeCode(updateRequestDTO, extrinsicObject));
        // Some classifications may be added
    }

    @Test
    void testMergeEventTypeCodeNullEvents() {
        updateRequestDTO.setAttiCliniciRegoleAccesso(null);
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeEventTypeCode(updateRequestDTO, extrinsicObject));
    }

    @Test
    void testMergeServiceTime() {
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeServiceTime(updateRequestDTO, extrinsicObject));
        // Check that two slots (serviceStartTime, serviceStopTime) are added
        assertTrue(extrinsicObject.getSlot().stream().anyMatch(s -> "serviceStartTime".equals(s.getName())));
        assertTrue(extrinsicObject.getSlot().stream().anyMatch(s -> "serviceStopTime".equals(s.getName())));
    }

    @Test
    void testMergeDescriptionNormal() {
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeDescription(updateRequestDTO, extrinsicObject));
        assertTrue(extrinsicObject.getSlot().stream().anyMatch(s->"urn:ita:2022:description".equals(s.getName())));
    }

    @Test
    void testMergeDescriptionNullDescription() {
        updateRequestDTO.setDescription(null);
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeDescription(updateRequestDTO, extrinsicObject));
    }

    @Test
    void testMergeAdministrativeRequestNormal() {
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeAdministrativeRequest(updateRequestDTO, extrinsicObject));
        assertTrue(extrinsicObject.getSlot().stream().anyMatch(s->"urn:ita:2022:administrativeRequest".equals(s.getName())));
    }

    @Test
    void testMergeAdministrativeRequestNull() {
        updateRequestDTO.setAdministrativeRequest(null);
        assertDoesNotThrow(() -> MergeMetadataUtility.mergeAdministrativeRequest(updateRequestDTO, extrinsicObject));
    }

    @Test
    void testMergeSlotException() {
        // Cause an exception by adding a slot with a null name
        SlotType1 nullNamedSlot = new SlotType1();
        // null name will cause NullPointerException in filter (slot.getName().equals(slotName))
        extrinsicObject.getSlot().add(nullNamedSlot);

        // This triggers the catch block
        BusinessException ex = assertThrows(BusinessException.class,
                () -> MergeMetadataUtility.mergeDescription(updateRequestDTO, extrinsicObject));
        assertTrue(ex.getMessage().contains("Error while performing merge for urn:ita:2022:description"));
    }

    @Test
    void testMergeClassificationException() {
        // Add a classification with a null ClassificationScheme to cause NPE
        ClassificationType faultyClassification = new ClassificationType();
        // classificationSchemeName is accessed via classification.getClassificationScheme().equals(...)
        extrinsicObject.getClassification().add(faultyClassification);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> MergeMetadataUtility.mergeClassCode(updateRequestDTO, extrinsicObject));
        assertTrue(ex.getMessage().contains("Error while performing merge for"));
    }

    @Test
    void testEventCodeEnumFromValueKnown() {
        EventCodeEnum code = EventCodeEnum.fromValue("J07AC");
        assertNotNull(code);
        assertEquals("J07AC", code.getCode());
    }

    @Test
    void testEventCodeEnumFromValueUnknown() {
        EventCodeEnum code = EventCodeEnum.fromValue("UNKNOWN_CODE");
        assertNull(code);
    }
}