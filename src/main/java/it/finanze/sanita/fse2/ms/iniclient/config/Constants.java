package it.finanze.sanita.fse2.ms.iniclient.config;

/**
 * 
 * @author vincenzoingenito
 *
 * Constants application.
 */
public final class Constants {

	/**
	 *	Path scan.
	 */
	public static final class ComponentScan {

		/**
		 * Base path.
		 */
		public static final String BASE = "it.finanze.sanita.fse2.ms.iniclient";

		/**
		 * Controller path.
		 */
		public static final String CONTROLLER = "it.finanze.sanita.fse2.ms.iniclient.controller";

		/**
		 * Service path.
		 */
		public static final String SERVICE = "it.finanze.sanita.fse2.ms.iniclient.service";

		/**
		 * Configuration path.
		 */
		public static final String CONFIG = "it.finanze.sanita.fse2.ms.iniclient.config";
		
		/**
		 * Configuration mongo path.
		 */
		public static final String CONFIG_MONGO = "it.finanze.sanita.fse2.ms.iniclient.config.mongo";
		
		/**
		 * Configuration mongo repository path.
		 */
		public static final String REPOSITORY_MONGO = "it.finanze.sanita.fse2.ms.iniclient.repository";

		public static final class Collections {

			public static final String INI_EDS_INVOCATION = "ini_eds_invocation";

			private Collections() {

			}
		}
		
		private ComponentScan() {
			//This method is intentionally left blank.
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
		 * Constructor.
		 */
		private Profile() {
			//This method is intentionally left blank.
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

        private IniClientConstants(){}
	}
  
	/**
	 *	Constants.
	 */
	private Constants() {

	}

}
