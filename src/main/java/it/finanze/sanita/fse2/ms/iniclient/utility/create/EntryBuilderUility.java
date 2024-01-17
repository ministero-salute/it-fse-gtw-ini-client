package it.finanze.sanita.fse2.ms.iniclient.utility.create;

import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;

import it.finanze.sanita.fse2.ms.iniclient.dto.AuthorSlotDTO;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

public class EntryBuilderUility {

	
	protected static AuthorSlotDTO buildAuthorSlot(final String authorRole, final String authorInstitution, final String authorPerson) {
		SlotType1 authorRoleSlot = buildSlotObject("authorRole", authorRole);
		SlotType1 authorInstitutionSlot = buildSlotObject("authorInstitution", authorInstitution);
		SlotType1 authorPersonSlot = buildSlotObject("authorPerson", authorPerson);
		return new AuthorSlotDTO(authorRoleSlot, authorInstitutionSlot, authorPersonSlot);
		
	}
}
