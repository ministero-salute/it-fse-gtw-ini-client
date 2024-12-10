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

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

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
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.enums.SearchTypeEnum;
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

	private SSLContext sslContext;

	private XDSDeletetWS deletePort;

	private DocumentRegistryPortType documentRegistryPort;
	
	private DocumentRegistryPortType recuperoRiferimentoPort;

	private UpdateDocumentRegistryPortType updateDocumentRegistryPort;
	
	@PostConstruct
	void initialize() {
		try {
			samlHeaderBuilderUtility.initialize();
			if(Boolean.TRUE.equals(iniCFG.isEnableSSL())) {
				sslContext = securitySRV.createSslCustomContext();
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
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
		
		return documentRegistryPort.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
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
		return updateDocumentRegistryPort.documentRegistryUpdateDocumentSet(submitObjectsRequest);
//		return sendUpdateData(submitObjectsRequest,jwtTokenDTO,workflowInstanceId,startingDate,ActionEnumType.UPDATE_V2);
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
		return documentRegistryPort.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
//		return sendUpdateData(submitObjectsRequest,jwtTokenDTO,workflowInstanceId,startingDate,ActionEnumType.UPDATE);
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

		return documentRegistryPort.documentRegistryRegisterDocumentSetB(submitObjectsRequest);

	}

	@Override
	public AdhocQueryResponse getReferenceUUID(String idDoc, String tipoRicerca,JWTTokenDTO tokenDTO) {
		log.debug("Call to INI get reference");

		List<Header> headers = samlHeaderBuilderUtility.buildHeader(tokenDTO, ActionEnumType.READ_REFERENCE);
		WSBindingProvider bp = (WSBindingProvider)documentRegistryPort;
		bp.setOutboundHeaders(headers);

		AdhocQueryRequest adhocQueryRequest = ReadBodyBuilderUtility.buildAdHocQueryRequest(idDoc, tipoRicerca);
		return documentRegistryPort.documentRegistryRegistryStoredQuery(adhocQueryRequest);
	}

	@Override
	public AdhocQueryResponse getReferenceMetadata(String uuid, String tipoRicerca, JWTTokenDTO jwtToken,
			String workflowInstanceId) {
		Date startingDate = new Date();
		return getReferenceMetadata(uuid, tipoRicerca, jwtToken, ActionEnumType.READ_METADATA,workflowInstanceId,startingDate);
	}

	@Override
	public AdhocQueryResponse getReferenceMetadata(String uuid, String tipoRicerca, JWTTokenDTO jwtToken, ActionEnumType actionEnumType, String workflowInstanceId,Date startingDate) {
		log.debug("Call to INI get reference metadata");

		JWTTokenDTO reconfiguredToken = RequestUtility.configureReadTokenPerAction(jwtToken, actionEnumType);
		List<Header> headers = samlHeaderBuilderUtility.buildHeader(reconfiguredToken, actionEnumType);
 
		AdhocQueryRequest adhocQueryRequest = ReadBodyBuilderUtility.buildAdHocQueryRequest(uuid,tipoRicerca);
		
		AdhocQueryResponse response = null;
		WSBindingProvider bp = null;
		Object eventType = SearchTypeEnum.OBJECT_REF.getSearchKey().equals(tipoRicerca) ? INI_RIFERIMENTO_SOAP : INI_GET_METADATI_SOAP; 
		if(ActionEnumType.READ_METADATA.equals(actionEnumType)) {
			bp = (WSBindingProvider)documentRegistryPort;
			bp.getRequestContext().put(WII, workflowInstanceId);
			bp.getRequestContext().put(EVENT_TYPE, eventType);
			bp.getRequestContext().put(EVENT_DATE, startingDate);
			bp.setOutboundHeaders(headers);
			response = documentRegistryPort.documentRegistryRegistryStoredQuery(adhocQueryRequest);	
		} else {
			bp = (WSBindingProvider)recuperoRiferimentoPort;
			bp.getRequestContext().put(WII, workflowInstanceId);
			bp.getRequestContext().put(EVENT_TYPE, eventType);
			bp.getRequestContext().put(EVENT_DATE, startingDate);
			bp.setOutboundHeaders(headers);
			response = recuperoRiferimentoPort.documentRegistryRegistryStoredQuery(adhocQueryRequest);
		}
		 
		StringBuilder sb = new StringBuilder();
		if (response.getRegistryErrorList() != null && !CollectionUtils.isEmpty(response.getRegistryErrorList().getRegistryError())) {
			for(RegistryError error : response.getRegistryErrorList().getRegistryError()) {
					sb.append(error.getCodeContext());
			}
			throw new BusinessException(sb.toString());
		}
		return response;
	}
 
 
}
