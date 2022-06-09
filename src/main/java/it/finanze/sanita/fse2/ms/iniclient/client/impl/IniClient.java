package it.finanze.sanita.fse2.ms.iniclient.client.impl;


import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestBuilderUtilityOld;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.WSBindingProvider;

import ihe.iti.xds_b._2007.DocumentRegistryPortType;
import ihe.iti.xds_b._2007.DocumentRegistryService;
import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.SamlBodyBuilderUtilityNew;
import it.finanze.sanita.fse2.ms.iniclient.utility.SamlRequestBuilderUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

/**
 * Production implemention of Ini Client.
 *
 * @author vincenzoingenito
 */
@Slf4j
@Component
@Profile(Constants.Profile.DEV)
public class IniClient implements IIniClient {


	@Override
	public RegistryResponseType sendData(final Document documentEntry, final Document submissionSetEntry, final Document jwtToken) {
		log.info("Production implementation ini client");
		try {
			QName qname = new QName("urn:ihe:iti:xds-b:2007", "DocumentRegistry_Service");
			final URL wsdlLocationUS = new URL("http://servizi.fascicolosanitario.gov.it/XDSDocumentRegistryRegister/RegisterDocumentSetb?wsdl");
			DocumentRegistryService service = new DocumentRegistryService(wsdlLocationUS, qname);
			DocumentRegistryPortType port = service.getDocumentRegistryPortSoap12();
 
			List<Header> headers = SamlRequestBuilderUtility.buildHeader(jwtToken);
			WSBindingProvider bp = (WSBindingProvider)port;
			bp.setOutboundHeaders(headers); 
			
			Binding bindingProvider = ((BindingProvider) port).getBinding();
			List<Handler> handlerChain = bindingProvider.getHandlerChain();
			handlerChain.add(new SOAPLoggingHandler());
			bindingProvider.setHandlerChain(handlerChain);

			DocumentEntryDTO documentEntryDTO = SamlBodyBuilderUtilityNew.extractDocumentEntry(documentEntry);
			SubmissionSetEntryDTO submissionSetEntryDTO = SamlBodyBuilderUtilityNew.extractSubmissionSetEntry(submissionSetEntry);

			// ENABLE the new Utility when all got parameterized
			// SubmitObjectsRequest submitObjectsRequest = SamlBodyBuilderUtilityNew.buildSubmitObjectRequest(qname, documentEntryDTO, submissionSetEntryDTO);
			SubmitObjectsRequest submitObjectsRequest = RequestBuilderUtilityOld.buildSubmitObjectRequest(qname);
			return port.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
		} catch (Exception ex) {
			log.error("Error while send data to ini: {}" , ex.getMessage());
			throw new BusinessException("Error while send data to ini:" + ex.getMessage());
		}
	}
 
	
}
