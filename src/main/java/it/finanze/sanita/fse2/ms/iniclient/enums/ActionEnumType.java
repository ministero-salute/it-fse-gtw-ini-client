package it.finanze.sanita.fse2.ms.iniclient.enums;

import lombok.Getter;

public enum ActionEnumType {
    CREATE("CREATE","TREATMENT","urn:ihe:iti:2007:RegisterDocumentSet-b"),
    READ_REFERENCE("READ","TREATMENT","urn:ihe:iti:2007:RegistryStoredQuery"),
    READ_METADATA("READ","TREATMENT","urn:ihe:iti:2007:RegistryStoredQuery"),
    UPDATE("UPDATE","TREATMENT","urn:ihe:iti:2007:RegisterDocumentSet-b"),
    DELETE("DELETE","SYSADMIN","urn:ihe:iti:xds-b:2010:XDSDeletetWS:DocumentRegistry_DeleteDocumentSetRequest"),
    REPLACE("CREATE","TREATMENT","urn:ihe:iti:2007:RegisterDocumentSet-b");
    
	@Getter
    private String actionId;
	@Getter
	private String purposeOfUse;
	@Getter
	private String headerAction;
	

	private ActionEnumType(String inActionId, String inPurposeOfUse, String inHeaderAction) {
		actionId = inActionId;
		purposeOfUse = inPurposeOfUse;
		headerAction = inHeaderAction;
	}
}
