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
package it.finanze.sanita.fse2.ms.iniclient.client.impl;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.EVENT_DATE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.EVENT_TYPE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.WII;
import static it.finanze.sanita.fse2.ms.iniclient.enums.EventType.INI_CREATE_SOAP;
import static it.finanze.sanita.fse2.ms.iniclient.enums.EventType.INI_DELETE_SOAP;
import static it.finanze.sanita.fse2.ms.iniclient.enums.EventType.INI_GET_METADATI_SOAP;
import static it.finanze.sanita.fse2.ms.iniclient.enums.EventType.INI_REPLACE_SOAP;
import static it.finanze.sanita.fse2.ms.iniclient.enums.EventType.INI_RIFERIMENTO_SOAP;
import static it.finanze.sanita.fse2.ms.iniclient.enums.EventType.INI_UPDATE_SOAP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.WSBindingProvider;

import ihe.iti.xds_b._2007.DocumentRegistryPortType;
import ihe.iti.xds_b._2007.DocumentRegistryService;
import ihe.iti.xds_b._2007.UpdateDocumentRegistryPortType;
import ihe.iti.xds_b._2007.UpdateDocumentRegistryService;
import ihe.iti.xds_b._2010.XDSDeletetWS;
import ihe.iti.xds_b._2010.XDSDeletetWSService;
import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.GovwayCfg;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.enums.SearchTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.IniDocumentNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.SoapIniException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.AuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.PublishReplaceBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.delete.DeleteBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.read.ReadBodyBuilderUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.RemoveObjectsRequestType;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

/**
 * Production implemention of Ini Client.
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
	
	@Autowired
	private AuditIniSrv auditIniSrv;

	@Autowired
	private IConfigSRV configSRV;
	
	@Autowired
	private GovwayCfg govwayCfg;

	private SSLContext sslContext;

	private XDSDeletetWS deletePort;

	private DocumentRegistryPortType documentRegistryPort;
	
	private DocumentRegistryPortType recuperoRiferimentoPort;

	private UpdateDocumentRegistryPortType updateDocumentRegistryPort;
	
	private List<URI> uris;
	
	@PostConstruct
	void initialize() {
		try {
			samlHeaderBuilderUtility.initialize();
			if(Boolean.TRUE.equals(iniCFG.isEnableSSL())) {
				sslContext = securitySRV.createSslCustomContext();
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			}
			
			if(!StringUtility.isNullOrEmpty(govwayCfg.getGovwayUrl())) {
				uris = getProxyURIs();
				setupProxy();	
			} 

			DocumentRegistryService documentRegistryService = new DocumentRegistryService();
			documentRegistryPort = documentRegistryService.getDocumentRegistryPortSoap12();
			if (!StringUtility.isNullOrEmpty(iniCFG.getUrlWsdlDocumentRegistryService())) {
				BindingProvider bindingProvider = (BindingProvider) documentRegistryPort;
				bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, iniCFG.getUrlWsdlDocumentRegistryService());
			} 
			
			UpdateDocumentRegistryService updateDocumentRegistryService = new UpdateDocumentRegistryService();
			updateDocumentRegistryPort = updateDocumentRegistryService.getDocumentRegistryUpdateDocumentSetPortSoap12();
			if (!StringUtility.isNullOrEmpty(iniCFG.getUrlWsdlUpdateDocumentRegistryService())) {
				BindingProvider bindingProvider = (BindingProvider) updateDocumentRegistryPort;
				bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, iniCFG.getUrlWsdlUpdateDocumentRegistryService());
			} 

			XDSDeletetWSService deletetWSService = new XDSDeletetWSService();
			deletePort = deletetWSService.getXDSDeletetWSSPort();
			if (!StringUtility.isNullOrEmpty(iniCFG.getUrlWsdlDeletetService())) {
				BindingProvider bindingProvider = (BindingProvider) deletePort;
				bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, iniCFG.getUrlWsdlDeletetService());
			}
			
			DocumentRegistryService recuperoRiferimentoService = new DocumentRegistryService();
			recuperoRiferimentoPort = recuperoRiferimentoService.getDocumentRegistryPortSoap12();
			if (!StringUtility.isNullOrEmpty(iniCFG.getUrlWsdlRecuperoRiferimentoService())) {
				BindingProvider bindingProvider = (BindingProvider) recuperoRiferimentoPort;
				bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, iniCFG.getUrlWsdlRecuperoRiferimentoService());
			} 

			if(Boolean.TRUE.equals(iniCFG.isEnableSSL())) {
				((BindingProvider) documentRegistryPort).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());
				((BindingProvider) deletePort).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());
				((BindingProvider) recuperoRiferimentoPort).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());
				((BindingProvider) updateDocumentRegistryPort).getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());
			}
			
			SOAPLoggingHandler loggingHandler = new SOAPLoggingHandler(auditIniSrv, configSRV);
			List<Handler> handlerChainDocumentRegistry = ((BindingProvider) documentRegistryPort).getBinding().getHandlerChain();
			handlerChainDocumentRegistry.add(loggingHandler);
			((BindingProvider) documentRegistryPort).getBinding().setHandlerChain(handlerChainDocumentRegistry);

			List<Handler> handlerChainDelete = ((BindingProvider) deletePort).getBinding().getHandlerChain();
			handlerChainDelete.add(loggingHandler);
			((BindingProvider) deletePort).getBinding().setHandlerChain(handlerChainDelete);
			
			List<Handler> handlerChainUpdate = ((BindingProvider) updateDocumentRegistryPort).getBinding().getHandlerChain();
			handlerChainUpdate.add(loggingHandler);
			((BindingProvider) updateDocumentRegistryPort).getBinding().setHandlerChain(handlerChainUpdate);

			List<Handler> handlerChainRecuperoRiferimento = ((BindingProvider) recuperoRiferimentoPort).getBinding().getHandlerChain();
			handlerChainRecuperoRiferimento.add(loggingHandler);
			((BindingProvider) recuperoRiferimentoPort).getBinding().setHandlerChain(handlerChainRecuperoRiferimento);
		} catch(Exception ex) {
			log.error("Error while initialiting INI context : " , ex);
			throw new BusinessException(ex);
		}
	}
 
	private void setupProxy() {
	    ProxySelector.setDefault(new ProxySelector() {
	        @Override
	        public List<Proxy> select(URI uri) {
	            if (uris.stream().anyMatch(u -> u.getHost().equals(uri.getHost()))) {
	                return Collections.singletonList(new Proxy(
	                    Proxy.Type.HTTP,
	                    new InetSocketAddress(govwayCfg.getGovwayUrl(), govwayCfg.getGovwayPort())
	                ));
	            }

	            return Collections.singletonList(Proxy.NO_PROXY);
	        }

	        @Override
	        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
	            ioe.printStackTrace();
	        }
	    });
	}
	
	private List<URI> getProxyURIs() {
	    List<String> proxyDomains = Arrays.asList(
	        iniCFG.getUrlWsdlDocumentRegistryService(),
	        iniCFG.getUrlWsdlUpdateDocumentRegistryService(),
	        iniCFG.getUrlWsdlDeletetService(),
	        iniCFG.getUrlWsdlRecuperoRiferimentoService()
	    );

	    return proxyDomains.stream().map(this::toURI).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	private URI toURI(String url) {
	    try {
	        return new URI(url);
	    } catch (URISyntaxException e) {
	        log.error("Invalid URI: {}", url, e);
	        return null;
	    }
	}

	
	@Override
	public RegistryResponseType sendPublicationData(final DocumentEntryDTO documentEntryDTO, final SubmissionSetEntryDTO submissionSetEntryDTO, final JWTTokenDTO jwtTokenDTO,
			String workflowInstanceId,Date startingDate) {
		log.debug("Call to INI publication");
		List<Header> headers = samlHeaderBuilderUtility.buildHeader(jwtTokenDTO, ActionEnumType.CREATE);
		WSBindingProvider bp = (WSBindingProvider)documentRegistryPort;
		bp.setOutboundHeaders(headers);
		
		SubmitObjectsRequest submitObjectsRequest = PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(documentEntryDTO, submissionSetEntryDTO, jwtTokenDTO.getPayload(), null);
		
		bp.getRequestContext().put(WII, workflowInstanceId);
		bp.getRequestContext().put(EVENT_TYPE, INI_CREATE_SOAP);
		bp.getRequestContext().put(EVENT_DATE, startingDate);
		
		if(!StringUtility.isNullOrEmpty(govwayCfg.getGovwayUser()) && !StringUtility.isNullOrEmpty(govwayCfg.getGovwayPass())) {
			Map<String, List<String>> h = getBasicAuthCredentials();
		    bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, h);	
		}
		
		return documentRegistryPort.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
	}

	private Map<String, List<String>> getBasicAuthCredentials() {
	    String authString = govwayCfg.getGovwayUser() + ":" + govwayCfg.getGovwayPass();
	    
	    String encodedAuth = "";
	    if(!StringUtility.isNullOrEmpty(authString)) {
	    	encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());	    	
	    }

	    Map<String, List<String>> h = new HashMap<>();
	    h.put("Authorization", Collections.singletonList("Basic " + encodedAuth));
		return h;
	}
	
 

	@Override
	public RegistryResponseType sendDeleteData(DeleteRequestDTO deleteRequestDto, JWTPayloadDTO jwtPayloadDTO, List<String> uuid, Date startingDate) {
		log.debug("Call to INI delete");

		JWTTokenDTO deleteToken = new JWTTokenDTO(jwtPayloadDTO);
		List<Header> headers = samlHeaderBuilderUtility.buildHeader(deleteToken, ActionEnumType.DELETE);

		WSBindingProvider bp = (WSBindingProvider)deletePort;
		bp.setOutboundHeaders(headers);
		bp.getRequestContext().put(WII, deleteRequestDto.getWorkflow_instance_id());
		bp.getRequestContext().put(EVENT_TYPE, INI_DELETE_SOAP);
		bp.getRequestContext().put(EVENT_DATE, startingDate);
		RemoveObjectsRequestType removeObjectsRequest = DeleteBodyBuilderUtility.buildRemoveObjectsRequest(deleteRequestDto.getUuid());
		Holder<RegistryResponseType> holder = new Holder<>();

		if(!StringUtility.isNullOrEmpty(govwayCfg.getGovwayUser()) && !StringUtility.isNullOrEmpty(govwayCfg.getGovwayPass())) {
			Map<String, List<String>> h = getBasicAuthCredentials();
		    bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, h);	
		}
		
		deletePort.documentRegistryDeleteDocumentSet(removeObjectsRequest, holder);
		return holder.value;
	}
	
	@Override
	public RegistryResponseType sendUpdateV2Data(SubmitObjectsRequest submitObjectsRequest, JWTTokenDTO jwtTokenDTO,String workflowInstanceId,Date startingDate) {
		log.debug("Call to INI update ");
		List<Header> headers = samlHeaderBuilderUtility.buildHeader(jwtTokenDTO, ActionEnumType.UPDATE_V2);
		WSBindingProvider bp = (WSBindingProvider)updateDocumentRegistryPort;
		bp.setOutboundHeaders(headers);

		bp.getRequestContext().put(WII, workflowInstanceId);
		bp.getRequestContext().put(EVENT_TYPE, INI_UPDATE_SOAP);
		bp.getRequestContext().put(EVENT_DATE, startingDate);
		
		if(!StringUtility.isNullOrEmpty(govwayCfg.getGovwayUser()) && !StringUtility.isNullOrEmpty(govwayCfg.getGovwayPass())) {
			Map<String, List<String>> h = getBasicAuthCredentials();
		    bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, h);	
		}
		return updateDocumentRegistryPort.documentRegistryUpdateDocumentSet(submitObjectsRequest);
	}
	

	@Override
	public RegistryResponseType sendUpdateData(SubmitObjectsRequest submitObjectsRequest, JWTTokenDTO jwtTokenDTO,String workflowInstanceId,Date startingDate) {
		log.debug("Call to INI update ");
		List<Header> headers = samlHeaderBuilderUtility.buildHeader(jwtTokenDTO, ActionEnumType.UPDATE);
		WSBindingProvider bp = (WSBindingProvider)documentRegistryPort;
		bp.setOutboundHeaders(headers);

		bp.getRequestContext().put(WII, workflowInstanceId);
		bp.getRequestContext().put(EVENT_TYPE, INI_UPDATE_SOAP);
		bp.getRequestContext().put(EVENT_DATE, startingDate);
		
		if(!StringUtility.isNullOrEmpty(govwayCfg.getGovwayUser()) && !StringUtility.isNullOrEmpty(govwayCfg.getGovwayPass())) {
			Map<String, List<String>> h = getBasicAuthCredentials();
		    bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, h);	
		}
		return documentRegistryPort.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
	}
	
	@Override
	public RegistryResponseType sendReplaceData(final DocumentEntryDTO documentEntryDTO, final SubmissionSetEntryDTO submissionSetEntryDTO,
			final JWTTokenDTO jwtTokenDTO, final String uuid,String workflowInstanceId,Date startingDate) {
		log.debug("Call to INI replace");

		// Reconfigure token and build request
		List<Header> headers = samlHeaderBuilderUtility.buildHeader(jwtTokenDTO, ActionEnumType.REPLACE);
		WSBindingProvider bp = (WSBindingProvider)documentRegistryPort;
		bp.setOutboundHeaders(headers);
		bp.getRequestContext().put(WII, workflowInstanceId);
		bp.getRequestContext().put(EVENT_TYPE, INI_REPLACE_SOAP);
		bp.getRequestContext().put(EVENT_DATE, startingDate);
		SubmitObjectsRequest submitObjectsRequest = PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(documentEntryDTO, submissionSetEntryDTO, jwtTokenDTO.getPayload(), uuid);

		if(!StringUtility.isNullOrEmpty(govwayCfg.getGovwayUser()) && !StringUtility.isNullOrEmpty(govwayCfg.getGovwayPass())) {
			Map<String, List<String>> h = getBasicAuthCredentials();
		    bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, h);	
		}
		
		return documentRegistryPort.documentRegistryRegisterDocumentSetB(submitObjectsRequest);

	}

	@Override
	public AdhocQueryResponse getReferenceUUID(String idDoc, String tipoRicerca,JWTTokenDTO tokenDTO) {
		log.debug("Call to INI get reference");

		List<Header> headers = samlHeaderBuilderUtility.buildHeader(tokenDTO, ActionEnumType.READ_REFERENCE);
		WSBindingProvider bp = (WSBindingProvider)documentRegistryPort;
		bp.setOutboundHeaders(headers);

		JWTPayloadDTO payloadDto = tokenDTO.getPayload();
		AdhocQueryRequest adhocQueryRequest = ReadBodyBuilderUtility.buildAdHocQueryRequest(idDoc, tipoRicerca,payloadDto.isUse_subject_as_author(), payloadDto.getSub());
		
		if(!StringUtility.isNullOrEmpty(govwayCfg.getGovwayUser()) && !StringUtility.isNullOrEmpty(govwayCfg.getGovwayPass())) {
			Map<String, List<String>> h = getBasicAuthCredentials();
		    bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, h);	
		}
		return documentRegistryPort.documentRegistryRegistryStoredQuery(adhocQueryRequest);
	}

	@Override
	public AdhocQueryResponse getReferenceMetadata(String oidToUpdate, String tipoRicerca, JWTTokenDTO jwtToken,
			String workflowInstanceId) {
		Date startingDate = new Date();
		return getReferenceMetadata(oidToUpdate, tipoRicerca, jwtToken, ActionEnumType.READ_METADATA, workflowInstanceId, startingDate);
	}
	
	@Override
	public AdhocQueryResponse getReferenceMetadata(String oidToUpdate, String tipoRicerca, JWTTokenDTO jwtToken, ActionEnumType actionEnumType, String workflowInstanceId, Date startingDate) {
	    
	    log.debug("Call to INI get reference metadata");

	    JWTTokenDTO reconfiguredToken = RequestUtility.configureReadTokenPerAction(jwtToken, actionEnumType);
	    List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, actionEnumType);
	    AdhocQueryRequest adhocQueryRequest = ReadBodyBuilderUtility.buildAdHocQueryRequest(oidToUpdate, tipoRicerca, jwtToken.getPayload().isUse_subject_as_author(), jwtToken.getPayload().getSub());
	    AdhocQueryResponse response = executeRegistryQuery(adhocQueryRequest, headers, actionEnumType, tipoRicerca, workflowInstanceId, startingDate);
	    validateResponse(response);
	    return response;
	}


	private AdhocQueryResponse executeRegistryQuery(AdhocQueryRequest request, List<Header> headers, ActionEnumType actionEnumType, String tipoRicerca,
	        String workflowInstanceId, Date startingDate) {
	    
	    DocumentRegistryPortType port = selectPort(actionEnumType);
	    configurePort(port, headers, tipoRicerca, workflowInstanceId, startingDate);
	    
	    try {
	        return port.documentRegistryRegistryStoredQuery(request);
	    } catch (Exception e) {
	        log.error("Errore durante la chiamata SOAP al registry: {}", e.getMessage(), e);
	        throw new SoapIniException("Errore nella comunicazione con il registry INI", e);
	    }
	}

	private DocumentRegistryPortType selectPort(ActionEnumType actionEnumType) {
	    return ActionEnumType.READ_METADATA.equals(actionEnumType) ? documentRegistryPort : recuperoRiferimentoPort;
	}

	private void configurePort(DocumentRegistryPortType port, List<Header> headers, String tipoRicerca, String workflowInstanceId, Date startingDate) {
	    WSBindingProvider bp = (WSBindingProvider) port;
	    Object eventType = SearchTypeEnum.OBJECT_REF.getSearchKey().equals(tipoRicerca) ? INI_RIFERIMENTO_SOAP : INI_GET_METADATI_SOAP;
	    bp.getRequestContext().put(WII, workflowInstanceId);
	    bp.getRequestContext().put(EVENT_TYPE, eventType);
	    bp.getRequestContext().put(EVENT_DATE, startingDate);
	    bp.setOutboundHeaders(headers);
	    configureBasicAuthIfNeeded(bp);
	}

	private void configureBasicAuthIfNeeded(WSBindingProvider bp) {
	    if (!StringUtility.isNullOrEmpty(govwayCfg.getGovwayUser()) && 
	        !StringUtility.isNullOrEmpty(govwayCfg.getGovwayPass())) {
	        
	        Map<String, List<String>> authHeaders = getBasicAuthCredentials();
	        bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, authHeaders);
	    }
	}
 
	
	private void validateResponse(AdhocQueryResponse response) {
	    if (response.getRegistryErrorList() == null ||
	        CollectionUtils.isEmpty(response.getRegistryErrorList().getRegistryError())) {
	        return;
	    }

	    List<RegistryError> errors = response.getRegistryErrorList().getRegistryError();

	    String errorMessage = errors.stream()
	            .map(error -> String.format("ERROR_CODE: %s ERROR_CONTEXT: %s", 
	                    error.getErrorCode(), 
	                    error.getCodeContext()))
	            .collect(Collectors.joining("; "));

	    log.error("INI ha restituito errori: {}", errorMessage);

	    String firstErrorCode = errors.get(0).getErrorCode();
	    String firstContext = errors.get(0).getCodeContext();

	    if ("QND1".equals(firstErrorCode) || (firstContext != null && firstContext.contains("No results"))) {
	        throw new IniDocumentNotFoundException(firstContext);
	    } else if(!StringUtility.isNullOrEmpty(firstErrorCode)) {
	    	throw new SoapIniException(firstContext);
	    } else {
	    	throw new BusinessException(errorMessage);	
	    }
	    
	}
 
}