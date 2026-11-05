package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.finanze.sanita.fse2.ms.iniclient.dto.PublicationMetadataReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.MergeMetadataUtility;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;

class MergeMetadataUtilityTest {

	private static final String DESCRIPTION_SLOT = "urn:ita:2022:description";

	private static ExtrinsicObjectType extrinsicWithDescription(final String oldValue) {
		SlotType1 slot = new SlotType1();
		slot.setName(DESCRIPTION_SLOT);
		ValueListType valueList = new ValueListType();
		valueList.getValue().add(oldValue);
		slot.setValueList(valueList);

		ExtrinsicObjectType extrinsic = new ExtrinsicObjectType();
		extrinsic.getSlot().add(slot);
		return extrinsic;
	}

	private static List<SlotType1> descriptionSlots(final ExtrinsicObjectType extrinsic) {
		List<SlotType1> out = new ArrayList<>();
		for (SlotType1 slot : extrinsic.getSlot()) {
			if (DESCRIPTION_SLOT.equals(slot.getName())) {
				out.add(slot);
			}
		}
		return out;
	}

	@Test
	@DisplayName("descriptions vuoto: nessuna eccezione e slot description rimosso")
	void emptyDescriptionsRemovesSlot() {
		ExtrinsicObjectType extrinsic = extrinsicWithDescription("OLD^old^1.2.3");
		PublicationMetadataReqDTO req = new PublicationMetadataReqDTO();
		req.setDescriptions(new ArrayList<>());

		assertDoesNotThrow(() -> MergeMetadataUtility.mergeDescription(req, extrinsic));
		assertTrue(descriptionSlots(extrinsic).isEmpty());
	}

	@Test
	@DisplayName("descriptions null: slot description rimosso")
	void nullDescriptionsRemovesSlot() {
		ExtrinsicObjectType extrinsic = extrinsicWithDescription("OLD^old^1.2.3");
		PublicationMetadataReqDTO req = new PublicationMetadataReqDTO();
		req.setDescriptions(null);

		assertDoesNotThrow(() -> MergeMetadataUtility.mergeDescription(req, extrinsic));
		assertTrue(descriptionSlots(extrinsic).isEmpty());
	}

	@Test
	@DisplayName("descriptions valorizzato: slot description sovrascritto")
	void valuedDescriptionsOverwritesSlot() {
		ExtrinsicObjectType extrinsic = extrinsicWithDescription("OLD^old^1.2.3");
		PublicationMetadataReqDTO req = new PublicationMetadataReqDTO();
		req.setDescriptions(List.of("NEW^new^4.5.6"));

		MergeMetadataUtility.mergeDescription(req, extrinsic);

		List<SlotType1> slots = descriptionSlots(extrinsic);
		assertEquals(1, slots.size());
		assertEquals(List.of("NEW^new^4.5.6"), slots.get(0).getValueList().getValue());
	}
}
