package it.finanze.sanita.fse2.ms.iniclient.enums;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import lombok.Getter;

public enum ActionEnumType {
    CREATE(Constants.IniClientConstants.CREATE_ACTION, Constants.IniClientConstants.TREATMENT_PURPOSE_OF_USE,Constants.IniClientConstants.REGISTER_DOCUMENT_SETB_ACTION),
    READ_REFERENCE(Constants.IniClientConstants.READ_ACTION, Constants.IniClientConstants.TREATMENT_PURPOSE_OF_USE,Constants.IniClientConstants.REGISTRY_STORED_QUERY_ACTION),
    READ_METADATA(Constants.IniClientConstants.READ_ACTION, Constants.IniClientConstants.TREATMENT_PURPOSE_OF_USE,Constants.IniClientConstants.REGISTRY_STORED_QUERY_ACTION),
    UPDATE(Constants.IniClientConstants.UPDATE_ACTION, Constants.IniClientConstants.TREATMENT_PURPOSE_OF_USE,Constants.IniClientConstants.REGISTER_DOCUMENT_SETB_ACTION),
    DELETE(Constants.IniClientConstants.DELETE_ACTION, Constants.IniClientConstants.SYSADMIN_PURPOSE_OF_USE,Constants.IniClientConstants.DELETE_DOCUMENT_ACTION),
    REPLACE(Constants.IniClientConstants.CREATE_ACTION, Constants.IniClientConstants.TREATMENT_PURPOSE_OF_USE,Constants.IniClientConstants.REGISTER_DOCUMENT_SETB_ACTION);
    
	@Getter
    private final String actionId;
	@Getter
	private final String purposeOfUse;
	@Getter
	private final String headerAction;
	

	ActionEnumType(String inActionId, String inPurposeOfUse, String inHeaderAction) {
		actionId = inActionId;
		purposeOfUse = inPurposeOfUse;
		headerAction = inHeaderAction;
	}
}
