package it.finanze.sanita.fse2.ms.iniclient.client.impl;


import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.WSBindingProvider;
import ihe.iti.xds_b._2007.*;
import ihe.iti.xds_b._2010.XDSDeletetWS;
import ihe.iti.xds_b._2010.XDSDeletetWSService;
import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.ResponseUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.ReplaceBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.delete.DeleteBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.PublishBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.read.ReadBodyBuilderUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.RemoveObjectsRequestType;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import java.net.URL;
import java.util.List;

/**
 * Production implemention of Ini Client.
 *
 * @author vincenzoingenito
 */
@Slf4j
@Component
public class IniClient implements IIniClient {

	@Autowired
	private IniCFG iniCFG;

	@Autowired
	private SamlHeaderBuilderUtility samlHeaderBuilderUtility;

	@Autowired
	private ISecuritySRV securitySRV;

	@Override
	public RegistryResponseType sendPublicationData(final Document documentEntry, final Document submissionSetEntry, final Document jwtToken) {
		log.info("Call to INI publication");
		RegistryResponseType out = null;
		try {
			SSLContext sslContext = securitySRV.createSslCustomContext();
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			
			DocumentRegistryService documentRegistryService = new DocumentRegistryService();
			if (!StringUtility.isNullOrEmpty(iniCFG.getWsdlPublishLocation())) {
				documentRegistryService = new DocumentRegistryService(new URL(iniCFG.getWsdlPublishLocation()));
			}  
			
			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO jwtTokenDTO = samlHeaderBuilderUtility.extractTokenEntry(jwtToken);
			JWTTokenDTO reconfiguredToken = this.configureTokenPerAction(jwtTokenDTO, ActionEnumType.CREATE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.CREATE);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				bp.setOutboundHeaders(headers);

				if (Boolean.TRUE.equals(iniCFG.isEnableLog())) {
					Binding bindingProvider = ((BindingProvider) port).getBinding();
					List<Handler> handlerChain = bindingProvider.getHandlerChain();
					handlerChain.add(new SOAPLoggingHandler());
					bindingProvider.setHandlerChain(handlerChain);
				}

				DocumentEntryDTO documentEntryDTO = CommonUtility.extractDocumentEntry(documentEntry);
				SubmissionSetEntryDTO submissionSetEntryDTO = CommonUtility.extractSubmissionSetEntry(submissionSetEntry);
				SubmitObjectsRequest submitObjectsRequest = PublishBodyBuilderUtility.buildSubmitObjectRequest(documentEntryDTO, submissionSetEntryDTO, jwtTokenDTO.getPayload());

				out = port.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
			}
		} catch (Exception ex) {
			log.error("Error while send data to ini: " + ex.getMessage());
			throw new BusinessException("Error while send data to ini:" + ex.getMessage());
		}
		return out;
	}

	@Override
	public RegistryResponseType sendDeleteData(String identificativoDocUpdate, JWTPayloadDTO jwtPayloadDTO) {
		log.info("Call to INI delete");
		try {
			SSLContext sslContext = securitySRV.createSslCustomContext();
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			XDSDeletetWSService deletetWSService = new XDSDeletetWSService();
			if (!StringUtility.isNullOrEmpty(iniCFG.getWsdlDeleteLocation())) {
				deletetWSService = new XDSDeletetWSService(new URL(iniCFG.getWsdlDeleteLocation()));
			}

			XDSDeletetWS port = deletetWSService.getXDSDeletetWSSPort();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
			jwtTokenDTO.setPayload(jwtPayloadDTO);

			// Get reference from INI UUID
			String uuid = this.getReferenceUUID(identificativoDocUpdate, jwtTokenDTO);

			// Reconfigure token and build request
			JWTTokenDTO reconfiguredToken = this.configureTokenPerAction(jwtTokenDTO, ActionEnumType.DELETE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.DELETE);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				bp.setOutboundHeaders(headers);

				if (Boolean.TRUE.equals(iniCFG.isEnableLog())) {
					Binding bindingProvider = ((BindingProvider) port).getBinding();
					List<Handler> handlerChain = bindingProvider.getHandlerChain();
					handlerChain.add(new SOAPLoggingHandler());
					bindingProvider.setHandlerChain(handlerChain);
				}

				RemoveObjectsRequestType removeObjectsRequest = DeleteBodyBuilderUtility.buildRemoveObjectsRequest(uuid);
				Holder<RegistryResponseType> holder = new Holder<>();

				port.documentRegistryDeleteDocumentSet(removeObjectsRequest, holder);
				return holder.value;
			}
		} catch (Exception ex) {
			log.error("Error while send data to ini: " + ex.getMessage());
			throw new BusinessException("Error while send data to ini:" + ex.getMessage());
		}
	}

	@Override
	public RegistryResponseType sendUpdateData(Document documentEntry, Document submissionSetEntry, Document jwtToken) {
		log.info("Call to INI update");
		RegistryResponseType out = null;
		try {
			SSLContext sslContext = securitySRV.createSslCustomContext();
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			DocumentRegistryService documentRegistryService = new DocumentRegistryService();
			if (!StringUtility.isNullOrEmpty(iniCFG.getWsdlPublishLocation())) {
				documentRegistryService = new DocumentRegistryService(new URL(iniCFG.getWsdlPublishLocation()));
			}

			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO jwtTokenDTO = samlHeaderBuilderUtility.extractTokenEntry(jwtToken);
			JWTTokenDTO reconfiguredToken = this.configureTokenPerAction(jwtTokenDTO, ActionEnumType.UPDATE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.UPDATE);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				bp.setOutboundHeaders(headers);

				if (Boolean.TRUE.equals(iniCFG.isEnableLog())) {
					Binding bindingProvider = ((BindingProvider) port).getBinding();
					List<Handler> handlerChain = bindingProvider.getHandlerChain();
					handlerChain.add(new SOAPLoggingHandler());
					bindingProvider.setHandlerChain(handlerChain);
				}

				DocumentEntryDTO documentEntryDTO = CommonUtility.extractDocumentEntry(documentEntry);
				SubmissionSetEntryDTO submissionSetEntryDTO = CommonUtility.extractSubmissionSetEntry(submissionSetEntry);
				SubmitObjectsRequest submitObjectsRequest = PublishBodyBuilderUtility.buildSubmitObjectRequest(documentEntryDTO, submissionSetEntryDTO, jwtTokenDTO.getPayload());

				//out = port.documentRegistryUpdateDocumentSet(submitObjectsRequest);
			}
		} catch (Exception ex) {
			log.error("Error while send data to ini: " + ex.getMessage());
			throw new BusinessException("Error while send data to ini:" + ex.getMessage());
		}
		return out;
	}

	@Override
	public RegistryResponseType sendReplaceData(ReplaceRequestDTO requestDTO, Document documentEntry, Document submissionSetEntry, Document jwtToken) {
		log.info("Call to INI replace");
		RegistryResponseType out = null;
		try {
			SSLContext sslContext = securitySRV.createSslCustomContext();
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			DocumentRegistryService documentRegistryService = new DocumentRegistryService();
			if (!StringUtility.isNullOrEmpty(iniCFG.getWsdlPublishLocation())) {
				documentRegistryService = new DocumentRegistryService(new URL(iniCFG.getWsdlPublishLocation()));
			}

			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			// Reconfigure token and build request
			JWTTokenDTO jwtTokenDTO = samlHeaderBuilderUtility.extractTokenEntry(jwtToken);

			// Get reference from INI UUID
			String uuid = this.getReferenceUUID(requestDTO.getIdentificativoDocUpdate(), jwtTokenDTO);

			JWTTokenDTO reconfiguredToken = this.configureTokenPerAction(jwtTokenDTO, ActionEnumType.REPLACE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.REPLACE);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				bp.setOutboundHeaders(headers);

				if (Boolean.TRUE.equals(iniCFG.isEnableLog())) {
					Binding bindingProvider = ((BindingProvider) port).getBinding();
					List<Handler> handlerChain = bindingProvider.getHandlerChain();
					handlerChain.add(new SOAPLoggingHandler());
					bindingProvider.setHandlerChain(handlerChain);
				}

				DocumentEntryDTO documentEntryDTO = CommonUtility.extractDocumentEntry(documentEntry);
				SubmissionSetEntryDTO submissionSetEntryDTO = CommonUtility.extractSubmissionSetEntry(submissionSetEntry);
				SubmitObjectsRequest submitObjectsRequest = ReplaceBodyBuilderUtility.buildSubmitObjectRequest(uuid, documentEntryDTO, submissionSetEntryDTO, jwtTokenDTO.getPayload());

				out = port.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
			}
		} catch (Exception ex) {
			log.error("Error while send data to ini: " + ex.getMessage());
			throw new BusinessException("Error while send data to ini:" + ex.getMessage());
		}
		return out;
	}

	@Override
	public String getReferenceUUID(String identificativoDocUpdate, JWTTokenDTO jwtToken) {
		log.info("Call to INI get reference");
		try {
			SSLContext sslContext = securitySRV.createSslCustomContext();
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			DocumentRegistryService documentRegistryService = new DocumentRegistryService();
			if (!StringUtility.isNullOrEmpty(iniCFG.getWsdlGetLocation())) {
				documentRegistryService = new DocumentRegistryService(new URL(iniCFG.getWsdlGetLocation()));
			}

			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO reconfiguredToken = this.configureTokenPerAction(jwtToken, ActionEnumType.READ_REFERENCE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.READ_REFERENCE);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				bp.setOutboundHeaders(headers);

				if (Boolean.TRUE.equals(iniCFG.isEnableLog())) {
					Binding bindingProvider = ((BindingProvider) port).getBinding();
					List<Handler> handlerChain = bindingProvider.getHandlerChain();
					handlerChain.add(new SOAPLoggingHandler());
					bindingProvider.setHandlerChain(handlerChain);
				}

				AdhocQueryRequest adhocQueryRequest = ReadBodyBuilderUtility.buildAdHocQueryRequest(identificativoDocUpdate, ActionEnumType.READ_REFERENCE);
				AdhocQueryResponse response = port.documentRegistryRegistryStoredQuery(adhocQueryRequest);
				if (ResponseUtility.isErrorResponse(response) || ResponseUtility.doesRecordGetResponseExist(response)) {
					throw new NoRecordFoundException("Record non trovato su INI");
				}
				String uuid = response.getRegistryObjectList().getIdentifiable().get(0).getValue().getId();
				log.info("Found uuid: {}", uuid);
				return uuid;
			}
		} catch (Exception ex) {
			log.error("Error while send data to ini: " + ex.getMessage());
			throw new BusinessException("Error while send data to ini:" + ex.getMessage());
		}
	}

	@Override
	public AdhocQueryResponse getReferenceMetadata(String identificativoDocUpdate, JWTTokenDTO jwtToken) {
		log.info("Call to INI get reference metadata");
		try {
			SSLContext sslContext = securitySRV.createSslCustomContext();
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			DocumentRegistryService documentRegistryService = new DocumentRegistryService();
			if (!StringUtility.isNullOrEmpty(iniCFG.getWsdlGetLocation())) {
				documentRegistryService = new DocumentRegistryService(new URL(iniCFG.getWsdlGetLocation()));
			}

			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO reconfiguredToken = this.configureTokenPerAction(jwtToken, ActionEnumType.READ_METADATA);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.READ_METADATA);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				bp.setOutboundHeaders(headers);

				if (Boolean.TRUE.equals(iniCFG.isEnableLog())) {
					Binding bindingProvider = ((BindingProvider) port).getBinding();
					List<Handler> handlerChain = bindingProvider.getHandlerChain();
					handlerChain.add(new SOAPLoggingHandler());
					bindingProvider.setHandlerChain(handlerChain);
				}

				AdhocQueryRequest adhocQueryRequest = ReadBodyBuilderUtility.buildAdHocQueryRequest(identificativoDocUpdate, ActionEnumType.READ_METADATA);
				AdhocQueryResponse response = port.documentRegistryRegistryStoredQuery(adhocQueryRequest);
				if (ResponseUtility.isErrorResponse(response) || ResponseUtility.doesRecordGetResponseExist(response)) {
					throw new NoRecordFoundException("Record non trovato su INI");
				}
				return response;
			}
		} catch (Exception ex) {
			log.error("Error while send data to ini: " + ex.getMessage());
			throw new BusinessException("Error while send data to ini:" + ex.getMessage());
		}
	}

	private JWTTokenDTO configureTokenPerAction(JWTTokenDTO jwtTokenDTO, ActionEnumType actionType) {
		log.info("Reconfiguring token per action");
		JWTPayloadDTO jwtPayloadDTO = jwtTokenDTO.getPayload();
		switch (actionType) {
			case CREATE:
			case REPLACE:
				jwtPayloadDTO.setAction_id("CREATE");
				jwtPayloadDTO.setPurpose_of_use("TREATMENT");
				break;
			case READ_REFERENCE:
			case READ_METADATA:
				jwtPayloadDTO.setAction_id("READ");
				jwtPayloadDTO.setPurpose_of_use("TREATMENT");
				break;
			case UPDATE:
				jwtPayloadDTO.setAction_id("UPDATE");
				jwtPayloadDTO.setPurpose_of_use("TREATMENT");
				break;
			case DELETE:
				jwtPayloadDTO.setAction_id("DELETE");
				jwtPayloadDTO.setPurpose_of_use("SYSADMIN");
				break;
			default:
				throw new BusinessException("Action not configured");
		}
		jwtTokenDTO.setPayload(jwtPayloadDTO);
		return jwtTokenDTO;
	}
}
