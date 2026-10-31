/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.*;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;

@Tag("ISOLATION")
class MetadataReadIsolationTest {
    private SlotType1 slot(String name, String... values) {
        var slot = new SlotType1();
        slot.setName(name);
        slot.setValueList(new ValueListType());
        slot.getValueList().getValue().addAll(List.of(values));
        return slot;
    }
    private AdhocQueryResponse response(ExtrinsicObjectType entry) {
        var response = new AdhocQueryResponse();
        response.setRegistryObjectList(new RegistryObjectListType());
        response.getRegistryObjectList().getIdentifiable().add(new ObjectFactory().createObjectRef(new ObjectRefType()));
        if (entry != null) response.getRegistryObjectList().getIdentifiable().add(new ObjectFactory().createExtrinsicObject(entry));
        return response;
    }

    @Test void typedMetadataUsesDocumentClassificationNotObjectReferenceOrder() {
        var entry = new ExtrinsicObjectType();
        var type = new ClassificationType();
        type.setClassificationScheme(ClassificationEnum.TYPE_CODE.getClassificationScheme());
        type.setNodeRepresentation("11502-2");
        type.setName(new InternationalStringType());
        var name = new LocalizedStringType();
        name.setValue("laboratory report");
        type.getName().getLocalizedString().add(name);
        entry.getClassification().add(type);
        var author = new ClassificationType();
        author.setClassificationScheme("urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d");
        author.getSlot().add(slot("authorRole", "doctor"));
        author.getSlot().add(slot("authorInstitution", "hospital"));
        entry.getClassification().add(author);
        entry.getSlot().add(slot("urn:ita:2022:administrativeRequest", "first", "second"));
        entry.getSlot().add(slot("urn:ita:fse:2025:EDSpublished", "TRUE"));
        entry.getSlot().add(slot("creationTime", "20260101"));
        var response = response(entry);
        assertEquals("laboratory report", CommonUtility.extractDocumentTypeFromQueryResponse(response));
        assertEquals("('11502-2^^" + ClassificationEnum.TYPE_CODE.getCodingScheme() + "')", CommonUtility.extractResourceHl7TypeFromQueryResponse(response));
        assertEquals("hospital", CommonUtility.extractAuthorInstitutionFromQueryResponse(response));
        assertEquals(List.of("first", "second"), CommonUtility.extractAdministrativeRequestFromQueryResponse(response));
        assertEquals("TRUE", CommonUtility.extractEdsPublishedFromQueryResponse(response));
        assertEquals("TRUE", CommonUtility.extractEdsPublishedSlotValue(response));
        assertEquals("20260101", CommonUtility.extractSlotMetadataMap(response).get("creationTime"));
    }

    @Test void absentMetadataReturnsDocumentedDefaults() {
        for (var response : List.of(new AdhocQueryResponse(), response(null), response(new ExtrinsicObjectType()))) {
            assertEquals(Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER, CommonUtility.extractDocumentTypeFromQueryResponse(response));
            assertEquals(Constants.IniClientConstants.MISSING_AUTHOR_INSTITUTION_PLACEHOLDER, CommonUtility.extractAuthorInstitutionFromQueryResponse(response));
            assertNull(CommonUtility.extractResourceHl7TypeFromQueryResponse(response));
            assertNull(CommonUtility.extractEdsPublishedSlotValue(response));
            assertEquals("", CommonUtility.extractEdsPublishedFromQueryResponse(response));
            assertTrue(CommonUtility.extractAdministrativeRequestFromQueryResponse(response).isEmpty());
            assertTrue(CommonUtility.extractSlotMetadataMap(response).isEmpty());
        }
        assertFalse(CommonUtility.checkMetadata(null));
        assertTrue(CommonUtility.extractSlotMetadataMap(null).isEmpty());
    }

    @Test void emptySlotValuesDoNotInventMetadata() {
        var entry = new ExtrinsicObjectType();
        entry.getSlot().add(slot("urn:ita:fse:2025:EDSpublished"));
        var absentValues = new SlotType1();
        absentValues.setName("absent");
        entry.getSlot().add(absentValues);
        assertNull(CommonUtility.extractEdsPublishedSlotValue(response(entry)));
        assertEquals("", CommonUtility.extractEdsPublishedFromQueryResponse(response(entry)));
        assertTrue(CommonUtility.extractSlotMetadataMap(response(entry)).isEmpty());
    }

    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"malformed", "CF^^^&wrong-system&ISO"})
    void invalidSubjectCannotBeMistakenForMefFiscalCode(String subject) {
        assertEquals(Constants.IniClientConstants.JWT_MISSING_SUBJECT, CommonUtility.extractFiscalCodeFromJwtSub(subject));
    }

    @Test void mefFiscalCodeIsExtractedFromQualifiedSubject() {
        assertEquals("RSSMRA80A01H501U", CommonUtility.extractFiscalCodeFromJwtSub("RSSMRA80A01H501U^^^&" + Constants.OIDS.OID_MEF + "&ISO"));
    }

    @Test void readTokenCopiesPayloadAndPreservesExplicitGatewayApplicationAttributes() {
        var payload = new JWTPayloadDTO();
        payload.setAction_id("CREATE");
        payload.setPurpose_of_use("original-purpose");
        payload.setSubject_application_id("caller-app");
        payload.setSubject_application_vendor("caller-vendor");
        payload.setSubject_application_version("caller-version");
        var source = new JWTTokenDTO(payload);
        var read = RequestUtility.configureReadTokenPerAction(source, ActionEnumType.READ_REFERENCE);
        assertNotSame(payload, read.getPayload());
        assertEquals("CREATE", payload.getAction_id());
        assertEquals("original-purpose", payload.getPurpose_of_use());
        assertEquals(ActionEnumType.READ_REFERENCE.getActionId(), read.getPayload().getAction_id());
        assertEquals(ActionEnumType.READ_REFERENCE.getPurposeOfUse(), read.getPayload().getPurpose_of_use());
        assertEquals("caller-app", read.getPayload().getSubject_application_id());
        assertEquals("caller-vendor", read.getPayload().getSubject_application_vendor());
        assertEquals("caller-version", read.getPayload().getSubject_application_version());
    }
}
