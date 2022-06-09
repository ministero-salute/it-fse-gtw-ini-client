package it.finanze.sanita.fse2.ms.iniclient.utility;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.ResponseBuilder;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml2.ecp.Request;
import org.opensaml.saml2.ecp.impl.RequestBuilder;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

public final class SamlUtility {

	public static Element samlGenerator() {
		AssertionBuilder assertionBuilder = new AssertionBuilder();
		Assertion ass = assertionBuilder.buildObject();
		ass.setID("_801bbd9c1bac7d62df2ead334139a459");
		ass.setIssueInstant(new DateTime());
		ass.setVersion(SAMLVersion.VERSION_20);
		return ass.getDOM();
	}
	
	public static void main(String[] args) throws MarshallingException {
		Request req = createRequest();
		req.setDOM(samlGenerator());
		
		ResponseMarshaller marshaller = new ResponseMarshaller();
		Element element = marshaller.marshall(req);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLHelper.writeNode(element, baos);
		String xml = new String(baos.toByteArray());
		System.out.println(xml);
	}
	
	private static Request createRequest() {
		RequestBuilder reqBuilder =new RequestBuilder();
		Request req = reqBuilder.buildObject();
		return req;
	}
	
	private Response createResponse(final DateTime issueDate, Issuer issuer, Status status, Assertion assertion) {
		ResponseBuilder responseBuilder = new ResponseBuilder();
		Response response = responseBuilder.buildObject();
		response.setID(UUID.randomUUID().toString());
		response.setIssueInstant(issueDate);
		response.setVersion(SAMLVersion.VERSION_20);
		response.setIssuer(issuer);
		response.setStatus(status);
		response.getAssertions().add(assertion);
		return response;
	}
}
