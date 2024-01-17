package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

@AllArgsConstructor
@Data
public class AuthorSlotDTO {
	
	private SlotType1 authorRoleSlot;
	
	private SlotType1 authorInstitutionSlot;
	
	private SlotType1 authorPersonSlot;

}
