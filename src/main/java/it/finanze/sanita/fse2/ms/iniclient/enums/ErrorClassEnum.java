/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorClassEnum {

	/**
	 * Generic class error.
	 */
	GENERIC("/errors", "Generic", "Errore generico", "/generic"),
	ID_DOC_MISSING("/errors/fields", "Missing", "Id documento non presente", "/ini"),
	METADATO_MISSING("/errors/fields", "Missing", "Metadato non presente", "/ini"),
	INVALID_INPUT("/errors/input", "Invalid input", "Input non valido", "/ini"),
	CONFLICT("errors/conflict", "Conflict", "Input non valido", "/ini"),
	VALIDATION("errors/validation", "Invalid input", "Input non valido", "/ini"),
	ISSUER_MISSING("errors/fields", "Missing", "Issuer non presente", "/ini");

	/**
	 * Error type.
	 */
	private final String type;

	/**
	 * Error title, user friendly description.
	 */
	private final String title;

	/**
	 * Error detail, developer friendly description.
	 */
	private final String detail;

	/**
	 * Error instance, URI that identifies the specific occurrence of the problem.
	 */
	private final String instance;

}
