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
package it.finanze.sanita.fse2.ms.iniclient.utility.common;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.singleton.SigningCredentialSingleton;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.ws.security.util.Base64;
import org.bson.Document;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.*;
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
import org.opensaml.xml.signature.*;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List; 

@Slf4j
@Component
public class SamlHeaderBuilderUtility {

	@Autowired
	private IniCFG iniCFG;

	public void initialize() {
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
		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issuer = issuerBuilder.buildObject();
		issuer.setValue(issuerValue);
		return issuer;
	}

	/**
	 * Build signature object from jwt and certificate / pk
	 * @return
	 */
	private Signature buildSignature() {
		SignatureBuilder builder = new SignatureBuilder();
		Signature signature = builder.buildObject();
		BasicX509Credential signingCredential =  SigningCredentialSingleton.getInstance(iniCFG);
		signature.setSigningCredential(signingCredential);
		signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
		signature.setKeyInfo(buildKeyInfo(signingCredential));
		return signature;
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
		AuthnStatementBuilder authnStatementBuilder = new AuthnStatementBuilder();
		AuthnStatement authnStatement = authnStatementBuilder.buildObject();
		authnStatement.setAuthnInstant(new DateTime());
		authnStatement.setAuthnContext(buildAuthnContext());
		return authnStatement;
	}

	/**
	 * Build authentication context
	 * @return
	 */
	private AuthnContext buildAuthnContext() {
		AuthnContextBuilder authnContextBuilder = new AuthnContextBuilder();
		AuthnContext authnContext = authnContextBuilder.buildObject();
		authnContext.setAuthnContextClassRef(buildAuthnContextClassRef());
		return authnContext;
	}

	/**
	 * Build auth context class reference
	 * @return
	 */
	private AuthnContextClassRef buildAuthnContextClassRef() {
		AuthnContextClassRefBuilder authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
		AuthnContextClassRef authnContextClassRef = authnContextClassRefBuilder.buildObject();
		authnContextClassRef.setAuthnContextClassRef(Constants.IniClientConstants.HEADER_AUTH_CONTEXT);
		return authnContextClassRef;
	}

	/**
	 * Build attribute statement
	 * @param tokenDTO
	 * @return
	 */
	private AttributeStatement buildAttributeStatement(final JWTTokenDTO tokenDTO, ActionEnumType actionType) {
		AttributeStatementBuilder attStaBuilder = new AttributeStatementBuilder();
		AttributeStatement attrStatement = attStaBuilder.buildObject();
		attrStatement.getAttributes().addAll(buildAttributes(tokenDTO, actionType));
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
				
			if (payloadTokenJwt.getPatient_consent() != null) {
				out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:resource:patient:consent", payloadTokenJwt.getPatient_consent().toString()));
			}

			out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:resource:hl7:type", payloadTokenJwt.getResource_hl7_type()));
			out.add(buildAttribute("urn:oasis:names:tc:xacml:2.0:subject:role", payloadTokenJwt.getSubject_role()));

			//Controllo che il campo locality inizia con un numero di modo che posso assumere che sia un oid
			if (!StringUtility.isNullOrEmpty(payloadTokenJwt.getLocality()) && !Character.isDigit(payloadTokenJwt.getLocality().charAt(0))) {
				String locality = payloadTokenJwt.getLocality();
				int firstIndextAmp = locality.indexOf('&');
				String localityWithoutNomeStruttura = locality.substring(firstIndextAmp+1, locality.length());
				out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:environment:locality", localityWithoutNomeStruttura));
			} else {
				out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:environment:locality", payloadTokenJwt.getLocality()));
			}

			out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse", payloadTokenJwt.getPurpose_of_use()));
			out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:subject:organization-id", payloadTokenJwt.getSubject_organization_id()));
			out.add(buildAttribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id", payloadTokenJwt.getSub().split("\\^")[0] + Constants.IniClientConstants.GENERIC_SUBJECT_SSN_OID));
			out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:subject:organization", payloadTokenJwt.getSubject_organization()));
			out.add(buildAttribute("urn:oasis:names:tc:xacml:1.0:resource:resource-id", payloadTokenJwt.getPerson_id()));
			out.add(buildAttribute("urn:oasis:names:tc:xacml:1.0:action:action-id", payloadTokenJwt.getAction_id()));
			out.add(buildAttribute("SubjectApplicationId", payloadTokenJwt.getSubject_application_id(),Constants.IniClientConstants.HEADER_NAME_FORMAT)); 
			out.add(buildAttribute("SubjectApplicationVendor", payloadTokenJwt.getSubject_application_vendor(),Constants.IniClientConstants.HEADER_NAME_FORMAT));
			out.add(buildAttribute("SubjectApplicationVersion", payloadTokenJwt.getSubject_application_version(),Constants.IniClientConstants.HEADER_NAME_FORMAT));
			out.add(buildAttribute("SubjectAuthenticator", Constants.IniClientConstants.SUBJECT_AUTHENTICATOR,Constants.IniClientConstants.HEADER_NAME_FORMAT));

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
		return buildAttribute(name, value, Constants.IniClientConstants.HEADER_ATTRNAME_URI);
	}
	
	/**
	 * Build attribute object from given input
	 * @param name
	 * @param value
	 * @return
	 */
	private Attribute buildAttribute(String name, String value, String nameFormat) {
		XSStringBuilder stringBuilder = new XSStringBuilder();
		AttributeBuilder attributeBuild = new AttributeBuilder();
		Attribute attribute = attributeBuild.buildObject();
		attribute.setName(name);
		attribute.setNameFormat(nameFormat);
		XSString stringValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
		stringValue.setValue(value);
		attribute.getAttributeValues().add(stringValue);
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