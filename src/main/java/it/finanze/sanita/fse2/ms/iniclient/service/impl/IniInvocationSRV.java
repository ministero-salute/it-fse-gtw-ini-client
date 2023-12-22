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
package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXB;

import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.webjars.NotFoundException;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.SearchTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.IdDocumentNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.UpdateBodyBuilderUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.webjars.NotFoundException;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.util.Date;

@Service
@Slf4j
@ConditionalOnProperty(name="ini.client.mock-enable", havingValue="false")
public class IniInvocationSRV implements IIniInvocationSRV {

	private static final String WARNING = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Warning";
	
	@Autowired
	private IniInvocationRepo iniInvocationRepo;

	@Autowired
	private IConfigSRV configSRV;

	@Autowired
	private IIniClient iniClient;

	@Autowired
	private LoggerHelper logger;
	
	@Autowired
	private SamlHeaderBuilderUtility samlHeaderBuilderUtility;

	 
	@Override
	public IniResponseDTO publishOrReplaceOnIni(final String workflowInstanceId, final ProcessorOperationEnum operation) {
		final Date startingDate = new Date();

		IniEdsInvocationETY iniInvocationETY = iniInvocationRepo.findByWorkflowInstanceId(workflowInstanceId);
		if(iniInvocationETY==null) {
			String message = String.format("Record con wii %s per l'operazione di %s non trovato", workflowInstanceId, operation);
			logger.error(Constants.AppConstants.LOG_TYPE_CONTROL, workflowInstanceId,message, operation.getOperation(), startingDate, operation.getErrorType(), 
					null,null, new JWTPayloadDTO(),null,null);
			throw new NotFoundException(message);
		}

		IniResponseDTO out = null;
		if(ProcessorOperationEnum.PUBLISH.equals(operation)) {
			out = publishByWorkflowInstanceId(iniInvocationETY,startingDate);
		} else if(ProcessorOperationEnum.REPLACE.equals(operation)) {
			out = replaceByWorkflowInstanceId(iniInvocationETY,startingDate);
		}

		if(out != null && out.getEsito() != null && out.getEsito() && configSRV.isRemoveMetadataEnable()) {
			iniInvocationRepo.removeMetadataByWorkflowInstanceId(workflowInstanceId);
		}

		return out;
	}

	private IniResponseDTO publishByWorkflowInstanceId(final IniEdsInvocationETY iniInvocationETY, final Date startingDate) {
		DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

		String documentType = CommonUtility.extractDocumentType(documentTreeDTO);
		String fiscalCode = CommonUtility.extractFiscalCodeFromDocumentTree(documentTreeDTO);
		
		JWTTokenDTO jwtTokenDTO = samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry());
		JWTPayloadDTO tokenPayloadDTO = jwtTokenDTO.getPayload();
		
		IniResponseDTO out = new IniResponseDTO();
		DocumentEntryDTO documentEntryDTO = CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry());
		SubmissionSetEntryDTO submissionSetEntryDTO = CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry());
		
		try {
			RegistryResponseType res = iniClient.sendPublicationData(documentEntryDTO, submissionSetEntryDTO, jwtTokenDTO);
			if (res.getRegistryErrorList() != null && !CollectionUtils.isEmpty(res.getRegistryErrorList().getRegistryError())) {
				StringBuilder errorMsg = new StringBuilder();
				for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
					if (!WARNING.equals(error.getSeverity())) {
						errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
					}
				}

				if(!StringUtility.isNullOrEmpty(errorMsg.toString())) {
					out.setErrorMessage(errorMsg.toString());
					out.setEsito(false);
				}
			} 

			String message = "Operazione eseguita su INI";
			if(Boolean.FALSE.equals(out.getEsito())) {
				message += ": " + out.getErrorMessage();
				logger.error(Constants.AppConstants.LOG_TYPE_CONTROL, iniInvocationETY.getWorkflowInstanceId(), message, ProcessorOperationEnum.PUBLISH.getOperation(), startingDate, ProcessorOperationEnum.PUBLISH.getErrorType(), documentType, fiscalCode, tokenPayloadDTO, documentEntryDTO.getAdministrativeRequest(), documentEntryDTO.getAuthorInstitution());
			} else {
				logger.info(Constants.AppConstants.LOG_TYPE_CONTROL,iniInvocationETY.getWorkflowInstanceId(), message, ProcessorOperationEnum.PUBLISH.getOperation(), startingDate, documentType, fiscalCode, tokenPayloadDTO, documentEntryDTO.getAdministrativeRequest(), documentEntryDTO.getAuthorInstitution());
				logger.info(Constants.AppConstants.LOG_TYPE_KPI,null, message, ProcessorOperationEnum.PUBLISH.getOperation(), startingDate, documentType, fiscalCode, tokenPayloadDTO,  documentEntryDTO.getAdministrativeRequest(), documentEntryDTO.getAuthorInstitution());
			}
		} catch(Exception ex) {
			logger.error(Constants.AppConstants.LOG_TYPE_CONTROL,iniInvocationETY.getWorkflowInstanceId(), "Errore riscontrato durante l'esecuzione dell'operazione su INI:" + out.getErrorMessage(), ProcessorOperationEnum.PUBLISH.getOperation(), startingDate, ProcessorOperationEnum.PUBLISH.getErrorType(), documentType, fiscalCode, tokenPayloadDTO, documentEntryDTO.getAdministrativeRequest(), documentEntryDTO.getAuthorInstitution());
			throw new BusinessException(ex);
		}

		return out;
	}
	
	private IniResponseDTO replaceByWorkflowInstanceId(final IniEdsInvocationETY iniInvocationETY, final Date startingDate) {
		IniResponseDTO out = new IniResponseDTO();
		DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

		String documentType = CommonUtility.extractDocumentType(documentTreeDTO);
		String fiscalCode = CommonUtility.extractFiscalCodeFromDocumentTree(documentTreeDTO);

		JWTTokenDTO jwtTokenDTO = samlHeaderBuilderUtility.extractTokenEntry(documentTreeDTO.getTokenEntry());
		JWTPayloadDTO tokenPayloadDTO = jwtTokenDTO.getPayload();
		DocumentEntryDTO documentEntryDTO = CommonUtility.extractDocumentEntry(documentTreeDTO.getDocumentEntry());
		SubmissionSetEntryDTO submissionSetEntryDTO = CommonUtility.extractSubmissionSetEntry(documentTreeDTO.getSubmissionSetEntry());
		
		try {
			RegistryResponseType res = iniClient.sendReplaceData(documentEntryDTO, submissionSetEntryDTO, jwtTokenDTO, iniInvocationETY.getRiferimentoIni());
			if (res.getRegistryErrorList() != null && !CollectionUtils.isEmpty(res.getRegistryErrorList().getRegistryError())) {
				StringBuilder errorMsg = new StringBuilder();
				out.setEsito(false);
				for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
					errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
				}
				
				if(!StringUtility.isNullOrEmpty(errorMsg.toString())) {
					out.setErrorMessage(errorMsg.toString());
					out.setEsito(false);
				}
			}

			String message = "Operazione eseguita su INI";
			if(Boolean.FALSE.equals(out.getEsito())) {
				message += ": " + out.getErrorMessage();
				logger.error(Constants.AppConstants.LOG_TYPE_CONTROL, iniInvocationETY.getWorkflowInstanceId(),message, ProcessorOperationEnum.REPLACE.getOperation(), startingDate, 
						ProcessorOperationEnum.REPLACE.getErrorType(), documentType, fiscalCode,tokenPayloadDTO, documentEntryDTO.getAdministrativeRequest(), documentEntryDTO.getAuthorInstitution());
			} else {
				logger.info(Constants.AppConstants.LOG_TYPE_CONTROL, iniInvocationETY.getWorkflowInstanceId(),message, ProcessorOperationEnum.REPLACE.getOperation(), startingDate, documentType, fiscalCode, tokenPayloadDTO, documentEntryDTO.getAdministrativeRequest(), documentEntryDTO.getAuthorInstitution());
				logger.info(Constants.AppConstants.LOG_TYPE_KPI, null,message, ProcessorOperationEnum.REPLACE.getOperation(), startingDate, documentType, fiscalCode, tokenPayloadDTO, documentEntryDTO.getAdministrativeRequest(), documentEntryDTO.getAuthorInstitution());
			}
		} catch(Exception ex) {
			logger.error(Constants.AppConstants.LOG_TYPE_CONTROL,iniInvocationETY.getWorkflowInstanceId(),"Errore riscontrato durante l'esecuzione dell'operazione su INI:" + out.getErrorMessage(), 
					ProcessorOperationEnum.REPLACE.getOperation(), startingDate, ProcessorOperationEnum.REPLACE.getErrorType(), documentType, 
					fiscalCode, tokenPayloadDTO, documentEntryDTO.getAdministrativeRequest(), documentEntryDTO.getAuthorInstitution());
			throw new BusinessException(ex);
		}
		return out;
	}
	 
	
	@Override
	public IniResponseDTO deleteByDocumentId(final DeleteRequestDTO deleteRequestDTO) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		
		String fiscalCode = CommonUtility.extractFiscalCodeFromJwtSub(deleteRequestDTO.getSub());
		JWTPayloadDTO jwtPayloadDTO = CommonUtility.buildJwtPayloadFromDeleteRequest(deleteRequestDTO);
		
		try {
			StringBuilder errorMsg = new StringBuilder();
			RegistryResponseType res = iniClient.sendDeleteData(deleteRequestDTO.getIdDoc(),jwtPayloadDTO, deleteRequestDTO.getUuid());
			if (res.getRegistryErrorList() != null && !CollectionUtils.isEmpty(res.getRegistryErrorList().getRegistryError())) {
				for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
					errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
				}
				
				if(!StringUtility.isNullOrEmpty(errorMsg.toString())) {
					out.setErrorMessage(errorMsg.toString());
					out.setEsito(false);
				}
			}

			if(!StringUtility.isNullOrEmpty(errorMsg.toString())) {
				out.setEsito(false);						
				out.setErrorMessage(errorMsg.toString());
			}
			
			String message = "Operazione eseguita su INI";
			if(Boolean.FALSE.equals(out.getEsito())) {
				message += ": " + out.getErrorMessage();
				logger.error(Constants.AppConstants.LOG_TYPE_CONTROL, deleteRequestDTO.getWorkflow_instance_id(),message, ProcessorOperationEnum.DELETE.getOperation(), startingDate, ProcessorOperationEnum.DELETE.getErrorType(), deleteRequestDTO.getDocumentType(), fiscalCode, jwtPayloadDTO,
						deleteRequestDTO.getAdministrative_request(), deleteRequestDTO.getAuthor_institution());
			} else {
				logger.info(Constants.AppConstants.LOG_TYPE_CONTROL, deleteRequestDTO.getWorkflow_instance_id() ,message, ProcessorOperationEnum.DELETE.getOperation(), startingDate, deleteRequestDTO.getDocumentType(),  fiscalCode, jwtPayloadDTO,
						deleteRequestDTO.getAdministrative_request(), deleteRequestDTO.getAuthor_institution());
				logger.info(Constants.AppConstants.LOG_TYPE_KPI, null,message, ProcessorOperationEnum.DELETE.getOperation(), startingDate, deleteRequestDTO.getDocumentType(),  fiscalCode, jwtPayloadDTO,
						deleteRequestDTO.getAdministrative_request(), deleteRequestDTO.getAuthor_institution());
			}
		} catch (Exception ex) {
			logger.error(Constants.AppConstants.LOG_TYPE_CONTROL,deleteRequestDTO.getWorkflow_instance_id(), "Errore riscontrato durante l'esecuzione dell'operazione su INI:" + out.getErrorMessage(), 
					ProcessorOperationEnum.DELETE.getOperation(), startingDate, ProcessorOperationEnum.DELETE.getErrorType(), deleteRequestDTO.getDocumentType(),
					fiscalCode, jwtPayloadDTO, deleteRequestDTO.getAdministrative_request(), deleteRequestDTO.getAuthor_institution());
			throw new BusinessException(ex);
		}
		return out;
	}

	@Override
	public IniResponseDTO updateByRequestBody(SubmitObjectsRequest submitObjectRequest, final UpdateRequestDTO updateRequestDTO) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		JWTTokenDTO token = new JWTTokenDTO(updateRequestDTO.getToken());
		JWTPayloadDTO payloadToken = token.getPayload();
		
		String fiscalCode = CommonUtility.extractFiscalCodeFromJwtSub(token.getPayload().getSub());
		
		try {
			StringBuilder errorMsg = new StringBuilder();
			RegistryResponseType registryResponse = iniClient.sendUpdateData(submitObjectRequest,token);
			if (registryResponse.getRegistryErrorList() != null && !CollectionUtils.isEmpty(registryResponse.getRegistryErrorList().getRegistryError())) {
				for(RegistryError error : registryResponse.getRegistryErrorList().getRegistryError()) {
					if (!WARNING.equals(error.getSeverity())) {
						errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
					}
				}
			}

			if(!StringUtility.isNullOrEmpty(errorMsg.toString())) {
				out.setEsito(false);						
				out.setErrorMessage(errorMsg.toString());
			}
			
			String message = "Operazione eseguita su INI";
			if(Boolean.FALSE.equals(out.getEsito())) {
				message += ": " + out.getErrorMessage();
				logger.error(Constants.AppConstants.LOG_TYPE_CONTROL,updateRequestDTO.getWorkflow_instance_id() , message, ProcessorOperationEnum.UPDATE.getOperation(), startingDate, ProcessorOperationEnum.UPDATE.getErrorType(), updateRequestDTO.getDocumentType(), fiscalCode, payloadToken,
						updateRequestDTO.getAdministrative_request(), updateRequestDTO.getAuthor_institution());
			} else {
				logger.info(Constants.AppConstants.LOG_TYPE_CONTROL,updateRequestDTO.getWorkflow_instance_id(), message, ProcessorOperationEnum.UPDATE.getOperation(), startingDate, updateRequestDTO.getDocumentType(), fiscalCode,payloadToken,
						updateRequestDTO.getAdministrative_request(), updateRequestDTO.getAuthor_institution());
				logger.info(Constants.AppConstants.LOG_TYPE_KPI,null, message, ProcessorOperationEnum.UPDATE.getOperation(), startingDate, updateRequestDTO.getDocumentType(), fiscalCode,payloadToken,
						updateRequestDTO.getAdministrative_request(), updateRequestDTO.getAuthor_institution());
			}
		} catch(Exception ex) {
			logger.error(Constants.AppConstants.LOG_TYPE_CONTROL,"", "Errore riscontrato durante l'esecuzione dell'operazione su INI:" + out.getErrorMessage(), 
					ProcessorOperationEnum.UPDATE.getOperation(), startingDate, ProcessorOperationEnum.UPDATE.getErrorType(), updateRequestDTO.getDocumentType(), 
					fiscalCode, payloadToken, updateRequestDTO.getAdministrative_request(), updateRequestDTO.getAuthor_institution());
			throw new BusinessException(ex);
		}
		return out;
	}


	@Override
	public AdhocQueryResponse getMetadata(String oid, JWTTokenDTO tokenDTO) {
		return iniClient.getReferenceUUID(oid, SearchTypeEnum.LEAF_CLASS.getSearchKey() ,tokenDTO);
	}

	@Override
	public GetReferenceResponseDTO getReference(final String oid, final JWTTokenDTO tokenDTO) {
		GetReferenceResponseDTO out = new GetReferenceResponseDTO();

		JWTTokenDTO reconfiguredToken = RequestUtility.configureReadTokenPerAction(tokenDTO, ActionEnumType.READ_REFERENCE);

		AdhocQueryResponse response = iniClient.getReferenceMetadata(oid, SearchTypeEnum.LEAF_CLASS.getSearchKey(), reconfiguredToken, ActionEnumType.READ_REF_AND_METADATA);
		StringBuilder sb = buildReferenceResponse(response);

		if(!StringUtility.isNullOrEmpty(sb.toString())){
			out.setErrorMessage(sb.toString());
		} else {
			String documentType = CommonUtility.extractDocumentTypeFromQueryResponse(response);
			String authorInstitution = CommonUtility.extractAuthorInstitutionFromQueryResponse(response);
			String administrativeRequest = CommonUtility.extractAdministrativeRequestFromQueryResponse(response);
			out.setUuid(response.getRegistryObjectList().getIdentifiable().get(0).getValue().getId());
			out.setDocumentType(documentType);
			out.setAuthorInstitution(authorInstitution);
			out.setAdministrativeRequest(administrativeRequest);
		}

		return out;
	}

	private static StringBuilder buildReferenceResponse(AdhocQueryResponse response) {
		StringBuilder sb = new StringBuilder();
		if (response.getRegistryErrorList() != null && !CollectionUtils.isEmpty(response.getRegistryErrorList().getRegistryError())) {
			for(RegistryError error : response.getRegistryErrorList().getRegistryError()) {
				if (error.getCodeContext().equals("No results from the query")) {
					throw new NotFoundException("Non è stato possibile recuperare i riferimenti con i dati forniti in input");
				} else {
					sb.append(error.getCodeContext());
				}
			}
		}
		return sb;
	}


	@Override
	public GetMergedMetadatiDTO getMergedMetadati(final String oidToUpdate,final MergedMetadatiRequestDTO updateRequestDTO) {
		GetMergedMetadatiDTO out = new GetMergedMetadatiDTO();
		JWTTokenDTO token = new JWTTokenDTO(updateRequestDTO.getToken());

		JWTTokenDTO reconfiguredToken = RequestUtility.configureReadTokenPerAction(new JWTTokenDTO(updateRequestDTO.getToken()), ActionEnumType.READ_REFERENCE);
		AdhocQueryResponse oldMetadata = iniClient.getReferenceUUID(oidToUpdate,SearchTypeEnum.LEAF_CLASS.getSearchKey(), reconfiguredToken);
		if(oldMetadata==null) {
			throw new IdDocumentNotFoundException("Nessun metadato trovato");
		}
		out.setAuthorInstitution(CommonUtility.extractAuthorInstitutionFromQueryResponse(oldMetadata));
		out.setDocumentType(CommonUtility.extractDocumentTypeFromQueryResponse(oldMetadata));
		String uuid = oldMetadata.getRegistryObjectList().getIdentifiable().get(0).getValue().getId();
		try (StringWriter sw = new StringWriter()) {
			SubmitObjectsRequest req = UpdateBodyBuilderUtility.buildSubmitObjectRequest(updateRequestDTO,oldMetadata.getRegistryObjectList(), uuid,token);
			JAXB.marshal(req, sw);
			out.setMarshallResponse(sw.toString());
		} catch(Exception ex) {
			out.setErrorMessage("Error while merge metadati:" + ExceptionUtils.getMessage(ex));
			log.error("Error while merge metadati", ex);
		} 
		return out;
	}
	
	 
}
