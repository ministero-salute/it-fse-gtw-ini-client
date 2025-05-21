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

    GENERIC("/errors", "Generic", "Generic error, missing more information", "/generic"),
    ID_DOC_MISSING("/errors", "Document not found", "Document with the specified workflowInstanceId not found",
            "/missing-docs"),
    REFERENCE_DATA_MISSING("/errors/ini", "References not found",
            "Query has returned not data when trying to fetch references",
            "/missing-references"),
    MISSING_METADATA("/errors/ini", "Document metadata not found",
            "Metadata of document not found with the specified OID", "/missing-metadata"),
    INVALID_INPUT("/errors/validation", "Invalid input data", "Invalid inputed API parameters", "/api-input"),
    CONFLICT("errors/validation", "Conflicted data", "Loaded data is already present", "/conflict"),
    VALIDATION("errors/validation", "Invalid input data", "Invalid inputed parameters", "/input"),
    ISSUER_MISSING("errors/validation", "Missing issuer", "Issuer not found", "/input");

    private final String type;

    private final String title;

    private final String detail;

    private final String instance;

}
