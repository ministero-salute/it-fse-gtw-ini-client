/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.config;

/**
 * Constants application.
 */
public final class Constants {


	public static final class OIDS {

		public static final String OID_MEF = "2.16.840.1.113883.2.9.4.3.2";

		private OIDS() {
			// This method is intentionally left blank.
		}
	}

	public static final class Collections {

		public static final String INI_EDS_INVOCATION = "ini_eds_invocation";

		private Collections() {

		}
	}

	public static final class Profile {
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

		/**
		 * Constructor.
		 */
		private Profile() {
			// This method is intentionally left blank.
		}

	}

	public static final class IniClientConstants {
		public static final String SEVERITY_HEAD_ERROR_MESSAGE = " SEVERITY : ";
		public static final String DEFAULT_HEAD_ERROR_MESSAGE = "Error while send data to ini: ";
		public static final String HEADER_AUTH_CONTEXT = "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";
		public static final String HEADER_ATTRNAME_URI = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";
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

		public static final String JWT_MISSING_ISSUER_PLACEHOLDER = "UNDEFINED_JWT_ISSUER";
		public static final String JWT_MISSING_SUBJECT = "UNDEFINED_SUBJECT";
		public static final String JWT_MISSING_LOCALITY = "UNDEFINED_LOCALITY";

		public static final String JWT_MISSING_ROLE_PLACEHOLDER = "UNDEFINED_JWT_ROLE";
		public static final String ERR_TOKEN_INTEGRITY = "Request token is not allowed to perform the operation";

		private IniClientConstants() {
		}
	}

	public static final class AppConstants {

		public static final String MOCKED_GATEWAY_NAME = "mocked-gateway";
		
		public static final String LOG_TYPE_KPI = "gateway-structured-log";
		
		public static final String LOG_TYPE_CONTROL = "gateway-control-structured-log";
	}

	/**
	 * Constants.
	 */
	private Constants() {

	}

}
