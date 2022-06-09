package it.finanze.sanita.fse2.ms.iniclient.utility;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

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
import org.opensaml.saml2.core.impl.AssertionMarshaller;
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
import org.opensaml.ws.wsaddressing.To;
import org.opensaml.ws.wsaddressing.impl.ActionBuilder;
import org.opensaml.ws.wsaddressing.impl.MessageIDBuilder;
import org.opensaml.ws.wsaddressing.impl.MessageIDMarshaller;
import org.opensaml.ws.wsaddressing.impl.ToBuilder;
import org.opensaml.ws.wsaddressing.impl.ToMarshaller;
import org.opensaml.ws.wssecurity.Security;
import org.opensaml.ws.wssecurity.impl.SecurityBuilder;
import org.opensaml.ws.wssecurity.impl.SecurityMarshaller;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.security.credential.Credential;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jmx.export.metadata.InvalidMetadataException;
import org.w3c.dom.Element;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;

import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SamlRequestBuilderUtility {
	
	static {
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			log.error("Error in static block while run default bootstrap : " , e);
			
		}
	}

	/*public static void main(String[] args) throws MarshallingException, ConfigurationException {
		DefaultBootstrap.bootstrap();

		Assertion assertion = SamlRequestBuilderUtility.generateSamlHeader(null);
		AssertionMarshaller marshaller = new AssertionMarshaller();
		Element element = marshaller.marshall(assertion);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLHelper.writeNode(element, baos);
		String saml = new String(baos.toByteArray());
		System.out.println(saml);
	}*/

	public static List<Header> buildHeader(final Document token) {
		List<Header> out = new ArrayList<>();
		try {
			ActionBuilder actionBuilder = new ActionBuilder();
			Action action = actionBuilder.buildObject();
			action.setValue("urn:ihe:iti:2007:RegisterDocumentSet-b");
			org.opensaml.ws.wsaddressing.impl.ActionMarshaller actionMarshaller = new org.opensaml.ws.wsaddressing.impl.ActionMarshaller();
			Element actionElement = actionMarshaller.marshall(action);
			ByteArrayOutputStream baosAction = new ByteArrayOutputStream();
			XMLHelper.writeNode(actionElement, baosAction);
			out.add(Headers.create(actionElement));
			
			ToBuilder toBuilder = new ToBuilder();
			To to = toBuilder.buildObject();
			to.setValue("https://fse-ini-test.liguriadigitale.it/services/fse_ini_DocumentRegistryProxy");
			ToMarshaller marshallerTo = new ToMarshaller();
			Element elTo = marshallerTo.marshall(to);
			ByteArrayOutputStream baosTo = new ByteArrayOutputStream();
			XMLHelper.writeNode(elTo, baosTo);
			out.add(Headers.create(elTo));
			
			MessageIDBuilder messageIdBuilder = new MessageIDBuilder();
			MessageID messageId = messageIdBuilder.buildObject();
			messageId.setValue(Utilities.generateUUID());
			MessageIDMarshaller idMarshaller = new MessageIDMarshaller();
			Element elId = idMarshaller.marshall(messageId);
			ByteArrayOutputStream baosId = new ByteArrayOutputStream();
			XMLHelper.writeNode(elId, baosId);
			out.add(Headers.create(elId));
			
			SecurityBuilder securityBuilder = new SecurityBuilder();
			Security sec = securityBuilder.buildObject(); 
			Assertion assertion1 = generateSamlHeader(token);
			Assertion assertion2 = new AssertionBuilder().buildObject();
			sec.getUnknownXMLObjects().add(assertion1);
			sec.getUnknownXMLObjects().add(assertion2);
			SecurityMarshaller securityMarsh = new SecurityMarshaller();
			Element elementSec = securityMarsh.marshall(sec);
			ByteArrayOutputStream baosSec = new ByteArrayOutputStream();
			XMLHelper.writeNode(elementSec, baosSec);
			out.add(Headers.create(elementSec));
		} catch(Exception ex) {
			log.error("Error while running build headers:" , ex);
			throw new BusinessException(ex);
		}

		return out;
	}
	
	
    private static Assertion generateSamlHeader(final Document jwtToken) {
        AssertionBuilder assertionBuilder = new AssertionBuilder();
        Assertion assertion = assertionBuilder.buildObject();
        assertion.setID(Utilities.generateUUID());
        assertion.setIssueInstant(new DateTime());
        assertion.setVersion(SAMLVersion.VERSION_20);
        assertion.setIssuer(buildIssuer());
        assertion.setSubject(buildSubject());
        assertion.setConditions(buildConditions());
        assertion.getAuthnStatements().add(buildAuthnStatement());
        assertion.getAttributeStatements().add(buildAttributeStatement(jwtToken));

        Signature sign = buildSignature();
        assertion.setSignature(sign);
        
        try {
        	Configuration.getMarshallerFactory().getMarshaller(assertion).marshall(assertion);
        	Signer.signObject(sign);
        } catch(Exception ex) {
        	
        }
        
        return assertion;
    }

    private static Issuer buildIssuer() {
        IssuerBuilder issuerBuilder = new IssuerBuilder();
        Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue("000");
        return issuer;
    }

//    private static Signature buildSignature() {
////        SignatureBuilder signatureBuilder = new SignatureBuilder();
////        Signature signature = signatureBuilder.buildObject();
////        signature.setKeyInfo(buildKeyInfo());
////        signature.setSignatureAlgorithm("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
////        signature.setCanonicalizationAlgorithm(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
////        
//        
//        
//        BasicX509Credential signingCredential = new BasicX509Credential();
////        signingCredential.setPrivateKey(privateKey);
//        // Build up the signature
//        SignatureBuilder signatureBuilder = new SignatureBuilder();
//        Signature signature = signatureBuilder.buildObject();
//        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
//        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
//        signature.setSigningCredential(signingCredential);
////        assertion.setSignature(signature);
//        
//        
//        return signature;
//    }
    
	private static Signature buildSignature() {
		Signature signature = null;
		try {
			SignatureBuilder builder = new SignatureBuilder();
			signature = builder.buildObject();
			BasicX509Credential signingCredential = getSigningCredential();
			signature.setSigningCredential(signingCredential);
			signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
			signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
			signature.setKeyInfo(buildKeyInfo(signingCredential));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return signature;
		
	}
	
	public static BasicX509Credential getSigningCredential() {
		BasicX509Credential credential = null;
		try {
			// create public key (cert) portion of credential
			// InputStream inStreamCrt = new FileInputStream("C:\\Users\\066008758\\eclipse-workspace-new\\gtw-ini-client\\src\\main\\resources\\saml.crt");
			Resource resourceSamlCrt = new ClassPathResource("saml.crt");
			InputStream inStreamCrt = resourceSamlCrt.getInputStream();
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			java.security.cert.X509Certificate publicKey = (java.security.cert.X509Certificate)cf.generateCertificate(inStreamCrt);
			inStreamCrt.close();
			String pbK = publicKey.getSigAlgName();

			// create private key
			Resource resourcePkcs8 = new ClassPathResource("saml.pkcs8");
			// RandomAccessFile raf = new RandomAccessFile("C:\\Users\\066008758\\eclipse-workspace-new\\gtw-ini-client\\src\\main\\resources\\saml.pkcs8", "r");
			RandomAccessFile raf = new RandomAccessFile(resourcePkcs8.getURI().getPath(), "r");
			byte[] buf = new byte[(int)raf.length()];
			raf.readFully(buf);
			raf.close();
			
			PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(buf);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate(kspec);

			// create credential and initialize
			credential = new BasicX509Credential();
			credential.setEntityCertificate(publicKey);
			credential.setPrivateKey(privateKey);
		} catch (Exception e) {
			System.out.println("Stop");
			// TODO: handle exception
		}
		
		
		return credential;
	}

    private static KeyInfo buildKeyInfo(BasicX509Credential signingCredential) throws CertificateEncodingException {
    	KeyInfoBuilder keyInfoBuilder = new KeyInfoBuilder();
    	KeyInfo keyInfo = keyInfoBuilder.buildObject();

    	X509DataBuilder builderData = new X509DataBuilder();
    	X509Data data = builderData.buildObject();
    	
    	X509CertificateBuilder build = new X509CertificateBuilder();
    	X509Certificate cert  = build.buildObject();
		String certValue = Base64.encode(signingCredential.getEntityCertificate().getEncoded());
		cert.setValue(certValue);
		data.getX509Certificates().add(cert);
    	
    	keyInfo.getX509Datas().add(data);
    	return keyInfo;
    }
    
    private static Subject buildSubject() {
        SubjectBuilder subjectBuilder = new SubjectBuilder();
        Subject subject = subjectBuilder.buildObject();
        subject.setNameID(buildNameID());
        return subject;
    }

    private static NameID buildNameID() {
        NameIDBuilder nameIdBuilder = new NameIDBuilder();
        NameID nameId = nameIdBuilder.buildObject();
        nameId.setValue("SISTEMATS^^^&amp;2.16.840.1.113883.2.9.4.3.2&amp;ISO");
        return nameId;
    }

    private static Conditions buildConditions() {
        ConditionsBuilder builder = new ConditionsBuilder();
        Conditions conditions = builder.buildObject();
        conditions.setNotBefore(new DateTime());
        conditions.setNotOnOrAfter(new DateTime().plusHours(1));
        return conditions;
    }

    private static AuthnStatement buildAuthnStatement() {
        AuthnStatementBuilder authnStatementBuilder = new AuthnStatementBuilder();
        AuthnStatement authnStatement = authnStatementBuilder.buildObject();
        authnStatement.setAuthnInstant(new DateTime());
        authnStatement.setAuthnContext(buildAuthnContext());
        return authnStatement;
    }

    private static AuthnContext buildAuthnContext() {
        AuthnContextBuilder authnContextBuilder = new AuthnContextBuilder();
        AuthnContext authnContext = authnContextBuilder.buildObject();
        authnContext.setAuthnContextClassRef(buildAuthnContextClassRef());
        return authnContext;
    }

    private static AuthnContextClassRef buildAuthnContextClassRef() {
        AuthnContextClassRefBuilder authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
        AuthnContextClassRef authnContextClassRef = authnContextClassRefBuilder.buildObject();
        authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:X509");
        return authnContextClassRef;
    }

    private static AttributeStatement buildAttributeStatement(final Document jwtToken) {
        AttributeStatementBuilder attStaBuilder = new AttributeStatementBuilder();
        AttributeStatement attrStatement = attStaBuilder.buildObject();
        attrStatement.getAttributes().addAll(buildAttributes(jwtToken));
        return attrStatement;
    }

    private static List<Attribute> buildAttributes(final Document jwtToken) {
        List<Attribute> out = new ArrayList<>();
        out.add(buildAttribute("urn:oasis:names:tc:xacml:2.0:subject:role", "INI"));
        out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:environment:locality", jwtToken.getString("locality")));
        out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse", jwtToken.getString("purpose_of_use")));
        out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:resource:hl7:type",
                "(&apos;57832-8^^2.16.840.1.113883.6.1&apos;)"));
        out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:subject:organization-id", jwtToken.getString("subject_organization_id")));
        out.add(buildAttribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id",
                "SISTEMATS^^^&amp;2.16.840.1.113883.2.9.4.3.2&amp;ISO"));
        out.add(buildAttribute("urn:oasis:names:tc:xspa:1.0:subject:organization", "test"));
        out.add(buildAttribute("urn:oasis:names:tc:xacml:1.0:resource:resource-id",
                "XXXRND78C60Z222F^^^&amp;2.16.840.1.113883.2.9.4.3.2&amp;ISO"));
        out.add(buildAttribute("urn:oasis:names:tc:xacml:1.0:action:action-id", jwtToken.getString("action_id")));
        return out;
    }

    private static Attribute buildAttribute(String name, String value) {
        XSStringBuilder stringBuilder = new XSStringBuilder();
        AttributeBuilder attributeBuild = new AttributeBuilder();
        Attribute attribute = attributeBuild.buildObject();
        attribute.setName(name);
        attribute.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
        XSString stringValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        stringValue.setValue(value);
        attribute.getAttributeValues().add(stringValue);
        return attribute;
    }

    @SuppressWarnings({ "unused", "unchecked" })
    private static <T> T buildSAMLObject(XMLObjectBuilderFactory builderFactory, final Class<T> objectClass, QName qName) {
        return (T) builderFactory.getBuilder(qName).buildObject(qName);
    }

  
}
