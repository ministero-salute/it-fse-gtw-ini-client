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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorClassEnum {
	
	GENERIC("/errors/internal", "Generic error", "Generic error, missing more information", "/generic"),
	ID_DOC_MISSING("/errors/internal", "Document not found", "Document with the specified workflowInstanceId not found", "/missing-docs"),
	INVALID_INPUT("/errors/validation", "Invalid input data", "Invalid input API parameters", "/api-input"),
	CONFLICT("/errors/validation", "Conflicted data", "Loaded data is already present", "/conflict"),
	VALIDATION("/errors/validation", "Invalid input data", "Invalid input parameters", "/input"),
	ISSUER_MISSING("/errors/validation", "Missing issuer", "Issuer not found", "/issuer-missing"),
	INI_REFERENCE_NOT_FOUND("/errors/external/ini", "INI references not found", "Query returned no data when fetching references from INI", "/missing-references"),
	INI_METADATA_NOT_FOUND("/errors/external/ini", "INI metadata not found", "Metadata not found with the specified OID in INI", "/missing-metadata"),
	INI_COMMUNICATION_ERROR("/errors/external/ini", "INI communication error", "Error occurred during INI service communication", "/communication-error"),
	INI_SOAP_FAULT("/errors/external/ini", "INI SOAP fault", "SOAP fault received from INI service", "/soap-fault"),
	INI_TIMEOUT("/errors/external/ini", "INI timeout", "Timeout while communicating with INI service", "/timeout"),
	INI_UNAVAILABLE("/errors/external/ini", "INI service unavailable", "INI service is temporarily unavailable", "/unavailable");

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
