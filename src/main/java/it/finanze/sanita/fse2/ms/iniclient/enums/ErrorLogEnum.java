/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.enums;

import lombok.Getter;

public enum ErrorLogEnum implements ILogEnum {

	KO_PUBLISH("KO-PUBLISH", "Errore nella pubblicazione del CDA su INI"),
	KO_REPLACE("KO-REPLACE", "Errore nella replace del CDA su INI"),
	KO_UPDATE("KO-UPDATE", "Errore nell'update del CDA su INI"),
	KO_DELETE("KO-DELETE", "Errore nella delete del CDA su INI");

	@Getter
	private String code;
	
	@Getter
	private String description;

	ErrorLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}

}
