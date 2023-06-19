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
