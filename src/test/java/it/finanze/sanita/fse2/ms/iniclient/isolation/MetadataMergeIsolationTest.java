/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import it.finanze.sanita.fse2.ms.iniclient.dto.PublicationMetadataReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.*;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;

@Tag("ISOLATION")
class MetadataMergeIsolationTest {
    private SlotType1 slot(String name, String value) {
        SlotType1 slot = new SlotType1();
        slot.setName(name);
        slot.setValueList(new ValueListType());
        slot.getValueList().getValue().add(value);
        return slot;
    }

    @Test void omittedFieldsPreserveExistingMetadata() {
        var request = new PublicationMetadataReqDTO();
        var document = new ExtrinsicObjectType();
        var existing = slot("serviceStartTime", "old");
        var classification = new ClassificationType();
        classification.setClassificationScheme(ClassificationEnum.CLASS_CODE.getClassificationScheme());
        document.getSlot().add(existing);
        document.getClassification().add(classification);
        MergeMetadataUtility.mergeServiceTime(request, document);
        MergeMetadataUtility.mergeDescription(request, document);
        MergeMetadataUtility.mergeAdministrativeRequest(request, document);
        MergeMetadataUtility.mergeClassCode(request, document);
        MergeMetadataUtility.mergeHealthcareFacilityTypeCode(request, document);
        MergeMetadataUtility.mergePracticeSettingCode(request, document);
        MergeMetadataUtility.mergeEventTypeCode(request, document);
        assertEquals(List.of(existing), document.getSlot());
        assertEquals(List.of(classification), document.getClassification());
    }

    @Test void replacesRequestedSlotsWithoutRemovingUnrelatedMetadata() {
        var request = new PublicationMetadataReqDTO();
        request.setDataInizioPrestazione("20260101000000");
        request.setDataFinePrestazione("20260102000000");
        request.setDescriptions(List.of("new description", "unused"));
        request.setAdministrativeRequest(List.of(AdministrativeReqEnum.values()[0]));
        var document = new ExtrinsicObjectType();
        var unrelated = slot("unrelated", "preserved");
        document.getSlot().add(unrelated);
        document.getSlot().add(slot("serviceStartTime", "old"));
        MergeMetadataUtility.mergeServiceTime(request, document);
        MergeMetadataUtility.mergeDescription(request, document);
        MergeMetadataUtility.mergeAdministrativeRequest(request, document);
        assertTrue(document.getSlot().contains(unrelated));
        assertEquals(5, document.getSlot().size());
        assertEquals(List.of("20260101000000"), document.getSlot().stream()
                .filter(s -> s.getName().equals("serviceStartTime")).findFirst().orElseThrow().getValueList().getValue());
        assertEquals(List.of("new description"), document.getSlot().stream()
                .filter(s -> s.getName().equals("urn:ita:2022:description")).findFirst().orElseThrow().getValueList().getValue());
    }

    @Test void replacesClassCodeWithRequestedCodeAndKeepsOtherClassifications() {
        var request = new PublicationMetadataReqDTO();
        var selected = TipoDocAltoLivEnum.values()[0];
        request.setTipoDocumentoLivAlto(selected);
        var document = new ExtrinsicObjectType();
        var old = new ClassificationType();
        old.setClassificationScheme(ClassificationEnum.CLASS_CODE.getClassificationScheme());
        var unrelated = new ClassificationType();
        unrelated.setClassificationScheme("other");
        document.getClassification().addAll(List.of(old, unrelated));
        MergeMetadataUtility.mergeClassCode(request, document);
        assertEquals(2, document.getClassification().size());
        assertTrue(document.getClassification().contains(unrelated));
        assertFalse(document.getClassification().contains(old));
        var replacement = document.getClassification().stream().filter(c -> c != unrelated).findFirst().orElseThrow();
        assertEquals(selected.getCode(), replacement.getNodeRepresentation());
        assertEquals(selected.getDescription(), replacement.getName().getLocalizedString().get(0).getValue());
    }

    @Test void facilityAndPracticeSettingUseTheirOwnCodingSchemes() {
        var request = new PublicationMetadataReqDTO();
        request.setTipologiaStruttura(HealthcareFacilityEnum.values()[0]);
        request.setAssettoOrganizzativo(PracticeSettingCodeEnum.values()[0]);
        var document = new ExtrinsicObjectType();
        MergeMetadataUtility.mergeHealthcareFacilityTypeCode(request, document);
        MergeMetadataUtility.mergePracticeSettingCode(request, document);
        assertEquals(2, document.getClassification().size());
        var facility = document.getClassification().stream().filter(c -> c.getClassificationScheme()
                .equals(ClassificationEnum.HEALTH_CARE_FACILITY_TYPE_CODE.getClassificationScheme())).findFirst().orElseThrow();
        assertEquals(request.getTipologiaStruttura().getCode(), facility.getNodeRepresentation());
        var practice = document.getClassification().stream().filter(c -> c.getClassificationScheme()
                .equals(ClassificationEnum.PRACTICE_SETTING_CODE.getClassificationScheme())).findFirst().orElseThrow();
        assertEquals(request.getAssettoOrganizzativo().getCode(), practice.getNodeRepresentation());
    }

    @Test void explicitlyEmptyEventsRemoveExistingClassification() {
        var request = new PublicationMetadataReqDTO();
        request.setAttiCliniciRegoleAccesso(List.of());
        var document = new ExtrinsicObjectType();
        var old = new ClassificationType();
        old.setClassificationScheme(ClassificationEnum.EVENT_CODE.getClassificationScheme());
        document.getClassification().add(old);
        MergeMetadataUtility.mergeEventTypeCode(request, document);
        assertTrue(document.getClassification().isEmpty());
    }

    @Test void eventCodesAreExpandedToCodedClassifications() {
        var request = new PublicationMetadataReqDTO();
        var event = EventCodeEnum.values()[0];
        request.setAttiCliniciRegoleAccesso(List.of(event.getCode()));
        var document = new ExtrinsicObjectType();
        MergeMetadataUtility.mergeEventTypeCode(request, document);
        assertEquals(1, document.getClassification().size());
        var classification = document.getClassification().get(0);
        assertEquals(event.getCode(), classification.getNodeRepresentation());
        assertEquals(event.getDescription(), classification.getName().getLocalizedString().get(0).getValue());
    }

    @Test void malformedExistingMetadataReportsBusinessFailure() {
        var request = new PublicationMetadataReqDTO();
        request.setDataInizioPrestazione("20260101000000");
        var document = new ExtrinsicObjectType();
        document.getSlot().add(new SlotType1());
        assertInstanceOf(NullPointerException.class, assertThrows(BusinessException.class,
                () -> MergeMetadataUtility.mergeServiceTime(request, document)).getCause());
    }
}
