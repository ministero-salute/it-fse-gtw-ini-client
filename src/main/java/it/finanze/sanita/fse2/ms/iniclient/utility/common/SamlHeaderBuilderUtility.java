/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.utility.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.ws.security.util.Base64;
import org.bson.Document;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml2.core.impl.AttributeStatementBuilder;
import org.opensaml.saml2.core.impl.AuthnContextBuilder;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnStatementBuilder;
import org.opensaml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.SubjectBuilder;
import org.opensaml.ws.wsaddressing.Action;
import org.opensaml.ws.wsaddressing.MessageID;
import org.opensaml.ws.wsaddressing.impl.ActionBuilder;
import org.opensaml.ws.wsaddressing.impl.MessageIDBuilder;
import org.opensaml.ws.wsaddressing.impl.MessageIDMarshaller;
import org.opensaml.ws.wssecurity.Security;
import org.opensaml.ws.wssecurity.impl.SecurityBuilder;
import org.opensaml.ws.wssecurity.impl.SecurityMarshaller;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.FileUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SamlHeaderBuilderUtility {

	@Autowired
	private IniCFG iniCFG;

	public SamlHeaderBuilderUtility(){
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			log.error("Error in block while run default bootstrap : ", e);
		}
	}


	/**
	 * Build headers from jwt token
	 * @param tokenDTO
	 * @return
	 */
	public List<Header> buildHeader(final JWTTokenDTO tokenDTO, ActionEnumType actionType) {
		List<Header> out = new ArrayList<>();
		try {
			Element actionElement = buildAction(actionType);
			ByteArrayOutputStream baosAction = new ByteArrayOutputStream();
			XMLHelper.writeNode(actionElement, baosAction);
			out.add(Headers.create(actionElement));

			Element messageId = buildMessageId();
			ByteArrayOutputStream baosId = new ByteArrayOutputStream();
			XMLHelper.writeNode(messageId, baosId);
			out.add(Headers.create(messageId));

			Element elementSec = buildSecurity(tokenDTO, actionType);
			ByteArrayOutputStream baosSec = new ByteArrayOutputStream();
			XMLHelper.writeNode(elementSec, baosSec);
			out.add(Headers.create(elementSec));
		} catch (Exception ex) {
			log.error("Error while running build headers:" + ex.getMessage());
			throw new BusinessException(ex);
		}

		return out;
	}

	/**
	 * Build security object
	 * @param tokenDTO
	 * @return
	 */
	private Element buildSecurity(final JWTTokenDTO tokenDTO, ActionEnumType actionType) {
		Element output = null;
		try {
			SecurityBuilder securityBuilder = new SecurityBuilder();
			Security sec = securityBuilder.buildObject();
			Assertion assertion1 = generateSamlHeader(tokenDTO, actionType);
			sec.getUnknownXMLObjects().add(assertion1);
			SecurityMarshaller securityMarsh = new SecurityMarshaller();
			output = securityMarsh.marshall(sec);
		} catch (Exception ex) {
			log.error("Error while perform build security : " + ex.getMessage());
			throw new BusinessException("Error while perform build security : " + ex.getMessage());
		}
		return output;
	}

	/**
	 * Build messageId
	 * @return
	 */
	private Element buildMessageId() {
		Element output = null;
		try {
			MessageIDBuilder messageIdBuilder = new MessageIDBuilder();
			MessageID messageId = messageIdBuilder.buildObject();
			messageId.setValue(StringUtility.generateUUID());
			MessageIDMarshaller idMarshaller = new MessageIDMarshaller();
			output = idMarshaller.marshall(messageId);
		} catch (Exception ex) {
			log.error("Error while perform build message id : " + ex.getMessage());
			throw new BusinessException("Error while perform build message id : " + ex.getMessage());
		}
		return output;
	}

	/**
	 * Build action
	 * @return
	 */
	private Element buildAction(ActionEnumType actionType) {
		Element output = null;
		try {
			ActionBuilder actionBuilder = new ActionBuilder();
			Action action = actionBuilder.buildObject();
			action.setValue(actionType.getHeaderAction()); 
			org.opensaml.ws.wsaddressing.impl.ActionMarshaller actionMarshaller = new org.opensaml.ws.wsaddressing.impl.ActionMarshaller();
			output = actionMarshaller.marshall(action);
		} catch (Exception ex) {
			log.error("Error while perform build action : " + ex.getMessage());
			throw new BusinessException("Error while perform build action : " + ex.getMessage());
		}

		return output;
	}

	/**
	 * Generate SAML headers
	 * @param tokenDTO
	 * @return
	 */
	private Assertion generateSamlHeader(final JWTTokenDTO tokenDTO, ActionEnumType actionType) {
		Assertion output = null;
		try {
			AssertionBuilder assertionBuilder = new AssertionBuilder();
			output = assertionBuilder.buildObject();
			output.setID("_" + StringUtility.generateUUID());
			output.setIssueInstant(new DateTime());
			output.setVersion(SAMLVersion.VERSION_20);
			output.setIssuer(buildIssuer(tokenDTO.getPayload().getIss()));
			output.setSubject(buildSubject(tokenDTO.getPayload().getSub().split("\\^")[0]));
			output.setConditions(buildConditions());
			output.getAuthnStatements().add(buildAuthnStatement());
			output.getAttributeStatements().add(buildAttributeStatement(tokenDTO, actionType));

			Signature sign = buildSignature();
			output.setSignature(sign);

			Configuration.getMarshallerFactory().getMarshaller(output).marshall(output);
			Signer.signObject(sign);

		} catch (Exception ex) {
			log.error("Error while perform generate saml header : " + ex.getMessage());
			throw new BusinessException("Error while perform generate saml header : " + ex.getMessage());
		}
		return output;
	}

	/**
	 * Build issuer value
	 * @param issuerValue
	 * @return
	 */
	private Issuer buildIssuer(final String issuerValue) {
		Issuer issuer = null;
		try {
			IssuerBuilder issuerBuilder = new IssuerBuilder();
			issuer = issuerBuilder.buildObject();
			issuer.setValue(issuerValue);
		} catch (Exception ex) {
			log.error("Error while perform build issuer :" + ex.getMessage());
			throw new BusinessException(ex);
		}
		return issuer;
	}

	/**
	 * Build signature object from jwt and certificate / pk
	 * @return
	 */
	private Signature buildSignature() {
		Signature signature = null;
		try {
			SignatureBuilder builder = new SignatureBuilder();
			signature = builder.buildObject();
			BasicX509Credential signingCredential = getSigningCredential();
			signature.setSigningCredential(signingCredential);
			signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
			signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
			signature.setKeyInfo(buildKeyInfo(signingCredential));
		} catch (Exception ex) {
			log.error("Error while perform build signature : " + ex.getMessage());
			throw new BusinessException("Error while perform build signature : " + ex.getMessage());
		}

		return signature;

	}

	public BasicX509Credential getSigningCredential() {
		BasicX509Credential credential = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			try (InputStream authInStreamCrt = FileUtility.getFileFromGenericResource(iniCFG.getKeyStoreLocation())) {
				keyStore.load(authInStreamCrt, iniCFG.getKeyStorePassword().toCharArray());
			}
			Enumeration<String> en = keyStore.aliases();
			String keyAlias = "";
			while (en.hasMoreElements()) {
				keyAlias = en.nextElement();
				if (en.hasMoreElements() && iniCFG.getKeyStoreAlias() != null && !iniCFG.getKeyStoreAlias().isEmpty()) {
					keyAlias = iniCFG.getKeyStoreAlias();
					break;
				}
			}
			Certificate c = keyStore.getCertificate(keyAlias);
			PrivateKey key = (PrivateKey)keyStore.getKey(keyAlias, iniCFG.getKeyStorePassword().toCharArray());

			credential = new BasicX509Credential();
			credential.setEntityCertificate((java.security.cert.X509Certificate) c);
			credential.setPrivateKey(key);
		} catch (Exception ex) {
			log.error("Error while perform get signing credential : " + ex.getMessage());
			throw new BusinessException("Error while perform get signing credential : " + ex.getMessage());
		}
		return credential;
	}

	private KeyInfo buildKeyInfo(BasicX509Credential signingCredential) {
		KeyInfo keyInfo = null;
		try {
			KeyInfoBuilder keyInfoBuilder = new KeyInfoBuilder();
			keyInfo = keyInfoBuilder.buildObject();

			X509DataBuilder builderData = new X509DataBuilder();
			X509Data data = builderData.buildObject();

			// Digital signature cert
			X509CertificateBuilder build = new X509CertificateBuilder();
			X509Certificate cert = build.buildObject();
			String sigCert = Base64.encode(signingCredential.getEntityCertificate().getEncoded());
			cert.setValue(sigCert);
			data.getX509Certificates().add(cert);

			keyInfo.getX509Datas().add(data);
		} catch (Exception ex) {
			log.error("Error while perform build key info : " + ex.getMessage());
			throw new BusinessException("Error while perform build key info : " + ex.getMessage());
		}
		return keyInfo;
	}

	/**
	 * Build subject
	 * @param sub
	 * @return
	 */
	private Subject buildSubject(final String sub) {
		SubjectBuilder subjectBuilder = new SubjectBuilder();
		Subject subject = subjectBuilder.buildObject();
		subject.setNameID(buildNameID(sub));
		return subject;
	}

	/**
	 * Build name id
	 * @param sub
	 * @return
	 */
	private NameID buildNameID(final String sub) {
		NameIDBuilder nameIdBuilder = new NameIDBuilder();
		NameID nameId = nameIdBuilder.buildObject();
		nameId.setValue(sub + Constants.IniClientConstants.GENERIC_SUBJECT_SSN_OID);

		return nameId;
	}

	/**
	 * Build conditions
	 * @return
	 */
	private Conditions buildConditions() {
		ConditionsBuilder builder = new ConditionsBuilder();
		Conditions conditions = builder.buildObject();
		conditions.setNotBefore(new DateTime());
		conditions.setNotOnOrAfter(new DateTime().plusYears(1));
		return conditions;
	}

	/**
	 * Build authentication statement
	 * @return
	 */
	private AuthnStatement buildAuthnStatement() {
		AuthnStatement authnStatement = null;
		try {
			AuthnStatementBuilder authnStatementBuilder = new AuthnStatementBuilder();
			authnStatement = authnStatementBuilder.buildObject();
			authnStatement.setAuthnInstant(new DateTime());
			authnStatement.setAuthnContext(buildAuthnContext());
		} catch (Exception ex) {
			log.error("Error while perform auth statement : " + ex.getMessage());
			throw new BusinessException("Error while perform auth statement : " + ex.getMessage());
		}
		return authnStatement;
	}

	/**
	 * Build authentication context
	 * @return
	 */
	private AuthnContext buildAuthnContext() {
		AuthnContext authnContext = null;
		try {
			AuthnContextBuilder authnContextBuilder = new AuthnContextBuilder();
			authnContext = authnContextBuilder.buildObject();
			authnContext.setAuthnContextClassRef(buildAuthnContextClassRef());
		} catch (Exception ex) {
			log.error("Error while perform authn context :" + ex.getMessage());
			throw new BusinessException("Error while perform authn context :" + ex.getMessage());
		}
		return authnContext;
	}

	/**
	 * Build auth context class reference
	 * @return
	 */
	private AuthnContextClassRef buildAuthnContextClassRef() {
		AuthnContextClassRef authnContextClassRef = null;
		try {
			AuthnContextClassRefBuilder authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
			authnContextClassRef = authnContextClassRefBuilder.buildObject();
			authnContextClassRef.setAuthnContextClassRef(Constants.IniClientConstants.HEADER_AUTH_CONTEXT);
		} catch (Exception ex) {
			log.error("Error while perform authn context class ref : " + ex.getMessage());
			throw new BusinessException("Error while perform authn context class ref : " + ex.getMessage());
		}
		return authnContextClassRef;
	}

	/**
	 * Build attribute statement
	 * @param tokenDTO
	 * @return
	 */
	private AttributeStatement buildAttributeStatement(final JWTTokenDTO tokenDTO, ActionEnumType actionType) {
		AttributeStatement attrStatement = null;
		try {
			AttributeStatementBuilder attStaBuilder = new AttributeStatementBuilder();
			attrStatement = attStaBuilder.buildObject();
			attrStatement.getAttributes().addAll(buildAttributes(tokenDTO, actionType));
		} catch(Exception ex) {
			log.error("Error while perform build attribute statement : "  + ex.getMessage());
			throw new BusinessException("Error while perform build attribute statement : "  + ex.getMessage());
		}
		return attrStatement;
	}

	/**
	 * Build attributes from token values
	 * urn values from documentation
	 * @param tokenDTO
	 * @return
	 */
	private List<Attribute> buildAttributes(final JWTTokenDTO tokenDTO, ActionEnumType actionEnumType) {
		List<Attribute> out = new ArrayList<>();

		try {
			JWTPayloadDTO payloadTokenJwt = tokenDTO.getPayload();

			if(!ActionEnumType.DELETE.equals(actionEnumType)) {
				if (!StringUtility.isNullOrEmpty(payloadTokenJwt.getResource_hl7_type())) {
					out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:resource:hl7:type", payloadTokenJwt.getResource_hl7_type()));
				}
				if (payloadTokenJwt.getPatient_consent() != null) {
					out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:resource:patient:consent", payloadTokenJwt.getPatient_consent().toString()));
				}
			} 
			out.add(buildAttribute("urn:oasis:names:tc:xacml:2.0:subject:role", payloadTokenJwt.getSubject_role()));
			out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:environment:locality", payloadTokenJwt.getLocality()));
			out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse", payloadTokenJwt.getPurpose_of_use()));
			out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:subject:organization-id", payloadTokenJwt.getSubject_organization_id()));
			out.add(buildAttribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id", payloadTokenJwt.getSub().split("\\^")[0] + Constants.IniClientConstants.GENERIC_SUBJECT_SSN_OID));
			out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:subject:organization", payloadTokenJwt.getSubject_organization_id()));
			out.add(buildAttribute("urn:oasis:names:tc:xacml:1.0:resource:resource-id", payloadTokenJwt.getPerson_id()+ Constants.IniClientConstants.GENERIC_SUBJECT_SSN_OID));
			out.add(buildAttribute("urn:oasis:names:tc:xacml:1.0:action:action-id", payloadTokenJwt.getAction_id()));

		} catch(Exception ex) {
			log.error("Error while perform build attributes : "  + ex.getMessage());
			throw new BusinessException("Error while perform build attributes : "  + ex.getMessage());
		}
		return out;
	}

	/**
	 * Build attribute object from given input
	 * @param name
	 * @param value
	 * @return
	 */
	private Attribute buildAttribute(String name, String value) {
		Attribute attribute = null;
		try {
			XSStringBuilder stringBuilder = new XSStringBuilder();
			AttributeBuilder attributeBuild = new AttributeBuilder();
			attribute = attributeBuild.buildObject();
			attribute.setName(name);
			attribute.setNameFormat(Constants.IniClientConstants.HEADER_ATTRNAME_URI);
			XSString stringValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
			stringValue.setValue(value);
			attribute.getAttributeValues().add(stringValue);
		} catch (Exception ex) {
			log.error("Error while perform build attribute : " + ex.getMessage());
			throw new BusinessException("Error while perform build attribute : " + ex.getMessage());
		}
		return attribute;
	}

	/**
	 * Extract token entry from bson
	 * @param jwtToken
	 * @return
	 */
	public JWTTokenDTO extractTokenEntry(Document jwtToken) {
		JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
		JWTPayloadDTO payloadDTO = JsonUtility.clone(jwtToken.get("payload"), JWTPayloadDTO.class);
		jwtTokenDTO.setPayload(payloadDTO);
		return jwtTokenDTO;
	}
}