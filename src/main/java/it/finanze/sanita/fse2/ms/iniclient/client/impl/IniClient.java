package it.finanze.sanita.fse2.ms.iniclient.client.impl;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.WSBindingProvider;
import ihe.iti.xds_b._2007.DocumentRegistryPortType;
import ihe.iti.xds_b._2007.DocumentRegistryService;
import ihe.iti.xds_b._2010.XDSDeletetWS;
import ihe.iti.xds_b._2010.XDSDeletetWSService;
import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.ResponseUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.PublishReplaceBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.delete.DeleteBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.read.ReadBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.UpdateBodyBuilderUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.RemoveObjectsRequestType;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
	
	private SSLContext sslContext;
	
	private DocumentRegistryService documentRegistryService;
	
	private XDSDeletetWSService deletetWSService;
	
	@PostConstruct
	void postConstruct() {
		try {
			sslContext = securitySRV.createSslCustomContext();
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			
			documentRegistryService = new DocumentRegistryService();
			if (!StringUtility.isNullOrEmpty(iniCFG.getWsdlPublishLocation())) {
				documentRegistryService = new DocumentRegistryService(new URL(iniCFG.getWsdlPublishLocation()));
			}  
			
			deletetWSService = new XDSDeletetWSService();
			if (!StringUtility.isNullOrEmpty(iniCFG.getWsdlDeleteLocation())) {
				deletetWSService = new XDSDeletetWSService(new URL(iniCFG.getWsdlDeleteLocation()));
			}
		} catch(Exception ex) {
			log.error("Error while initialiting INI context : " , ex);
			throw new BusinessException(ex);
		}
	}

	 
	@Override
	@SuppressWarnings("rawtypes")
	public RegistryResponseType sendPublicationData(final Document documentEntry, final Document submissionSetEntry, final Document jwtToken) {
		log.debug("Call to INI publication");
		RegistryResponseType out = null;
		try { 
			
			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO jwtTokenDTO = samlHeaderBuilderUtility.extractTokenEntry(jwtToken);
			JWTTokenDTO reconfiguredToken = RequestUtility.configureTokenPerAction(jwtTokenDTO, ActionEnumType.CREATE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.CREATE);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				initHeaders(bp, headers, (BindingProvider) port);

				DocumentEntryDTO documentEntryDTO = CommonUtility.extractDocumentEntry(documentEntry);
				SubmissionSetEntryDTO submissionSetEntryDTO = CommonUtility.extractSubmissionSetEntry(submissionSetEntry);
				SubmitObjectsRequest submitObjectsRequest = PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(documentEntryDTO, submissionSetEntryDTO, jwtTokenDTO.getPayload(), null);

				out = port.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
			}
		} catch (Exception ex) {
			log.error(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
			throw new BusinessException(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
		}
		return out;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public RegistryResponseType sendDeleteData(String idDoc, JWTPayloadDTO jwtPayloadDTO) {
		log.debug("Call to INI delete");
		try { 

			XDSDeletetWS port = deletetWSService.getXDSDeletetWSSPort();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
			jwtTokenDTO.setPayload(jwtPayloadDTO);

			// Get reference from INI UUID
			String uuid = this.getReferenceUUID(idDoc, jwtTokenDTO);

			// Reconfigure token and build request
			JWTTokenDTO reconfiguredToken = RequestUtility.configureTokenPerAction(jwtTokenDTO, ActionEnumType.DELETE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.DELETE);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				initHeaders(bp, headers, (BindingProvider) port);

				RemoveObjectsRequestType removeObjectsRequest = DeleteBodyBuilderUtility.buildRemoveObjectsRequest(uuid);
				Holder<RegistryResponseType> holder = new Holder<>();

				port.documentRegistryDeleteDocumentSet(removeObjectsRequest, holder);
				return holder.value;
			}
		} catch (NoRecordFoundException ne) {
			throw ne;
		} catch (Exception ex) {
			log.error(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
			throw new BusinessException(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public UpdateResponseDTO sendUpdateData(UpdateRequestDTO updateRequestDTO) {
		log.debug("Call to INI update");
		RegistryResponseType out = null;
		UpdateResponseDTO updateResponseDTO = new UpdateResponseDTO();
		AdhocQueryResponse queryResponse = null;
		try {
			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
			jwtTokenDTO.setPayload(updateRequestDTO.getToken());
			JWTTokenDTO reconfiguredToken = RequestUtility.configureTokenPerAction(jwtTokenDTO, ActionEnumType.UPDATE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.UPDATE);

			// Get reference from INI UUID
			String uuid = this.getReferenceUUID(updateRequestDTO.getIdDoc(), jwtTokenDTO);
			queryResponse = this.getReferenceMetadata(uuid, jwtTokenDTO);
			RegistryObjectListType metadata = queryResponse.getRegistryObjectList();

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				initHeaders(bp, headers, (BindingProvider) port);

				SubmitObjectsRequest submitObjectsRequest = UpdateBodyBuilderUtility.buildSubmitObjectRequest(updateRequestDTO,metadata,
						uuid,reconfiguredToken);
				out = port.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
			}
		} catch (NoRecordFoundException ne) {
			throw ne;
		} catch (Exception ex) {
			log.error(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
			throw new BusinessException(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
		}

		updateResponseDTO.setRegistryResponse(out);
		updateResponseDTO.setOldMetadata(queryResponse);

		return updateResponseDTO;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public RegistryResponseType sendReplaceData(ReplaceRequestDTO requestDTO, Document documentEntry, Document submissionSetEntry, Document jwtToken) {
		log.debug("Call to INI replace");
		RegistryResponseType out = null;
		try { 
			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			// Reconfigure token and build request
			JWTTokenDTO jwtTokenDTO = samlHeaderBuilderUtility.extractTokenEntry(jwtToken);

			// Get reference from INI UUID
			String uuid = this.getReferenceUUID(requestDTO.getIdDoc(), jwtTokenDTO);

			JWTTokenDTO reconfiguredToken = RequestUtility.configureTokenPerAction(jwtTokenDTO, ActionEnumType.REPLACE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.REPLACE);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				initHeaders(bp, headers, (BindingProvider) port);

				DocumentEntryDTO documentEntryDTO = CommonUtility.extractDocumentEntry(documentEntry);
				SubmissionSetEntryDTO submissionSetEntryDTO = CommonUtility.extractSubmissionSetEntry(submissionSetEntry);
				SubmitObjectsRequest submitObjectsRequest = PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(documentEntryDTO, submissionSetEntryDTO, jwtTokenDTO.getPayload(), uuid);

				out = port.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
			}
		} catch (Exception ex) {
			log.error(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
			throw new BusinessException(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
		}
		return out;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public String getReferenceUUID(String idDoc, JWTTokenDTO jwtToken) {
		log.debug("Call to INI get reference");
		try { 
			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO reconfiguredToken = RequestUtility.configureTokenPerAction(jwtToken, ActionEnumType.READ_REFERENCE);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.READ_REFERENCE);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				initHeaders(bp, headers, (BindingProvider) port);

				AdhocQueryRequest adhocQueryRequest = ReadBodyBuilderUtility.buildAdHocQueryRequest(idDoc, ActionEnumType.READ_REFERENCE);
				AdhocQueryResponse response = port.documentRegistryRegistryStoredQuery(adhocQueryRequest);
				if (ResponseUtility.isErrorResponse(response) || ResponseUtility.doesRecordGetResponseExist(response)) {
					throw new NoRecordFoundException("Record non trovato su INI");
				}
				String uuid = response.getRegistryObjectList().getIdentifiable().get(0).getValue().getId();
				log.debug("Found uuid: {}", uuid);
				return uuid;
			}
		} catch(NoRecordFoundException ne) {
			throw ne;
		} catch (Exception ex) {
			log.error(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
			throw new BusinessException(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public AdhocQueryResponse getReferenceMetadata(String idDoc, JWTTokenDTO jwtToken) {
		log.debug("Call to INI get reference metadata");
		try { 
			DocumentRegistryPortType port = documentRegistryService.getDocumentRegistryPortSoap12();
			((BindingProvider) port).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());

			JWTTokenDTO reconfiguredToken = RequestUtility.configureTokenPerAction(jwtToken, ActionEnumType.READ_METADATA);
			List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, ActionEnumType.READ_METADATA);

			try (WSBindingProvider bp = (WSBindingProvider)port) {
				initHeaders(bp, headers, (BindingProvider) port);

				AdhocQueryRequest adhocQueryRequest = ReadBodyBuilderUtility.buildAdHocQueryRequest(idDoc, ActionEnumType.READ_METADATA);
				AdhocQueryResponse response = port.documentRegistryRegistryStoredQuery(adhocQueryRequest);
				if (ResponseUtility.isErrorResponse(response) || ResponseUtility.doesRecordGetResponseExist(response)) {
					throw new NoRecordFoundException("Record non trovato su INI");
				}
				return response;
			}
		} catch (Exception ex) {
			log.error(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
			throw new BusinessException(Constants.IniClientConstants.DEFAULT_HEAD_ERROR_MESSAGE + ex.getMessage());
		}
	}

	private void initHeaders(WSBindingProvider bp, List<Header> headers, BindingProvider port) {
		bp.setOutboundHeaders(headers);

		if (Boolean.TRUE.equals(iniCFG.isEnableLog())) {
			Binding bindingProvider = port.getBinding();
			List<Handler> handlerChain = bindingProvider.getHandlerChain();
			handlerChain.add(new SOAPLoggingHandler());
			bindingProvider.setHandlerChain(handlerChain);
		}
	}
}
