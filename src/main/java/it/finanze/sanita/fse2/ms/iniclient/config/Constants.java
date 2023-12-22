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
package it.finanze.sanita.fse2.ms.iniclient.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants application.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {


	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class OIDS {

		public static final String OID_MEF = "2.16.840.1.113883.2.9.4.3.2";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Collections {

		public static final String INI_EDS_INVOCATION = "ini_eds_invocation";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Profile {
		/**
		 * Test profile.
		 */
		public static final String TEST = "test";
		public static final String TEST_PREFIX = "test_";

		/**
		 * Dev profile.
		 */
		public static final String DEV = "dev";

		/**
		 * Dev profile.
		 */
		public static final String DOCKER = "docker";


	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class IniClientConstants {
		public static final String SEVERITY_HEAD_ERROR_MESSAGE = " SEVERITY : ";
		public static final String DEFAULT_HEAD_ERROR_MESSAGE = "Error while send data to ini: ";
		public static final String HEADER_AUTH_CONTEXT = "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";
		public static final String HEADER_ATTRNAME_URI = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";
		public static final String HEADER_NAME_FORMAT = "urn:oasis:names:tc:SAML:2.0:attrname-format:basic";
		public static final String GENERIC_SUBJECT_SSN_OID = "^^^&2.16.840.1.113883.2.9.4.3.2&ISO";
		public static final String GENERIC_SSN_OID = "^^^^^^^^&2.16.840.1.113883.2.9.4.3.2&ISO";
		public static final String AUTHOR_IVA_OID = "^^^^^^^^&2.16.840.1.113883.2.9.6.3.2%ISO";
		public static final String AUTHOR_INSTITUTION_OID = "^^^^^&2.16.840.1.113883.2.9.4.1.3&ISO^^^^";
		public static final String VALID_SSN_OID = "^^^&2.16.840.1.113883.2.9.4.3.2&ISO";
		public static final String RECORD_NOT_FOUND = "Nessun record trovato";
		public static final String CODE_HEAD_ERROR_MESSAGE = " ERROR_CODE : ";
		public static final String EXTERNAL_IDENTIFIER_URN = "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier";
		public static final String CLASSIFICATION_OBJECT_URN = "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification";
		public static final String CODING_SCHEME = "codingScheme";
		public static final String URN_UUID = "urn:uuid:";
		public static final String SUBMISSION_SET_DEFAULT_ID = "SubmissionSet01";
		public static final String SOURCE_ID_OID = "2.16.840.1.113883.2.9.2.";
		public static final String TREATMENT_PURPOSE_OF_USE = "TREATMENT";
		public static final String REGISTER_DOCUMENT_SETB_ACTION = "urn:ihe:iti:2007:RegisterDocumentSet-b";
		public static final String REGISTRY_STORED_QUERY_ACTION = "urn:ihe:iti:2007:RegistryStoredQuery";
		public static final String DELETE_DOCUMENT_ACTION = "urn:ihe:iti:xds-b:2010:XDSDeletetWS:DocumentRegistry_DeleteDocumentSetRequest";
		public static final String SYSADMIN_PURPOSE_OF_USE = "SYSADMIN";
		public static final String CREATE_ACTION = "CREATE";
		public static final String READ_ACTION = "READ";
		public static final String UPDATE_ACTION = "UPDATE";
		public static final String DELETE_ACTION = "DELETE";

		public static final String MISSING_DOC_TYPE_PLACEHOLDER = "UNKNOWN_DOCUMENT_TYPE";
		public static final String MISSING_AUTHOR_INSTITUTION_PLACEHOLDER = "UNKNOWN_AUTHOR_INSTITUTION";
		public static final String MISSING_ADMINISTRATIVE_REQUEST_PLACEHOLDER = "UNKNOWN_ADMINISTRATIVE_REQUEST";

		public static final String JWT_MISSING_ISSUER_PLACEHOLDER = "UNDEFINED_JWT_ISSUER";
		public static final String JWT_MISSING_SUBJECT = "UNDEFINED_SUBJECT";
		public static final String JWT_MISSING_LOCALITY = "UNDEFINED_LOCALITY";

		public static final String JWT_MISSING_ROLE_PLACEHOLDER = "UNDEFINED_JWT_ROLE";
		public static final String ERR_TOKEN_INTEGRITY = "Request token is not allowed to perform the operation";
		public static final String SUBJECT_AUTHENTICATOR = "GTW_950";
		public static final String LANGUAGE_CODE = "it-IT";
		public static final String DOCUMENT_SIGNED = "true^Documento firmato";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class AppConstants {
		public static final String MOCKED_GATEWAY_NAME = "mocked-gateway";
		
		public static final String LOG_TYPE_KPI = "kpi-structured-log";
		public static final String LOG_TYPE_CONTROL = "control-structured-log";
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Logs {
		public static final String END_LOG = "[EXIT] {}() with arguments {}={}, {}={}";
		public static final Object DELETE = "delete";
		public static final Object TRACE_ID_LOG = "traceId";
		public static final Object WORKFLOW_INSTANCE_ID = "wif";
		public static final String START_UPDATE_LOG = "[START] {}() with arguments {}={}";
		public static final String END_UPDATE_LOG = "[EXIT] {}() with arguments {}={}";
		public static final Object UPDATE = "update";
        public static final Object CREATE = "create";
		public static final Object REPLACE = "replace";

		public static final String START_LOG = "[START] {}() with arguments {}={}, {}={}";
	}
}
