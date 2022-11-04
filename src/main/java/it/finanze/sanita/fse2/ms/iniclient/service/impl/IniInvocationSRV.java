/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import java.util.Date;

import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentTreeDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ReplaceRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.INIErrorEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.ResponseUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

@Service
@Slf4j
public class IniInvocationSRV implements IIniInvocationSRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -8674806764400269288L;

	private static final String WARNING = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Warning";
	
	@Autowired
	private IniInvocationRepo iniInvocationRepo;

	@Autowired
	private transient IIniClient iniClient;

	@Autowired
	private transient LoggerHelper logger;

	@Override
	public IniResponseDTO publishByWorkflowInstanceId(final String workflowInstanceId) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		DocumentTreeDTO documentTreeDTO = null;
		try { 
			StringBuilder errorMsg = new StringBuilder();
			IniEdsInvocationETY iniInvocationETY = iniInvocationRepo.findByWorkflowInstanceId(workflowInstanceId);
			if (iniInvocationETY != null) {
				documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

				RegistryResponseType res = iniClient.sendPublicationData(documentTreeDTO.getDocumentEntry(), documentTreeDTO.getSubmissionSetEntry(), documentTreeDTO.getTokenEntry());
				
				if (res.getRegistryErrorList() != null && !CollectionUtils.isEmpty(res.getRegistryErrorList().getRegistryError())) {
					for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
						if (!WARNING.equals(error.getSeverity())) {
							errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
						}
					}
					
					if(!StringUtility.isNullOrEmpty(errorMsg.toString())) {
						out.setErrorMessage(errorMsg.toString());
					}
				} else {
					out.setEsito(true);
				}
				
			} else {
				out.setEsito(false);
				out.setErrorMessage(INIErrorEnum.RECORD_NOT_FOUND.toString());
			}

		} catch(Exception ex) {
			log.error("Error while running find and send to ini by workflow instance id: {}" , workflowInstanceId);
			out.setErrorMessage(ExceptionUtils.getRootCauseMessage(ex));
			out.setEsito(false);
		}
		logResult(out.getEsito(), ProcessorOperationEnum.PUBLISH, startingDate, CommonUtility.extractIssuer(documentTreeDTO), CommonUtility.extractDocumentType(documentTreeDTO), CommonUtility.extractSubjectRole(documentTreeDTO), out.getErrorMessage());
		return out;
	}

	@Override
	public IniResponseDTO deleteByDocumentId(DeleteRequestDTO deleteRequestDTO) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		JWTPayloadDTO jwtPayloadDTO = CommonUtility.buildJwtPayloadFromDeleteRequest(deleteRequestDTO);
		AdhocQueryResponse currentMetadata = null;
		try {
			StringBuilder errorMsg = new StringBuilder();
			if (RequestUtility.checkDeleteRequestIntegrity(deleteRequestDTO)) {
				JWTPayloadDTO readJwtPayload = JsonUtility.clone(jwtPayloadDTO, JWTPayloadDTO.class);
				JWTTokenDTO readJwtToken = new JWTTokenDTO(readJwtPayload);
				currentMetadata = getMetadata(deleteRequestDTO.getIdDoc(), readJwtToken);
				RegistryResponseType res = iniClient.sendDeleteData(deleteRequestDTO.getIdDoc(),jwtPayloadDTO, deleteRequestDTO.getUuid());
				out.setEsito(true);
				if (res.getRegistryErrorList() != null && !CollectionUtils.isEmpty(res.getRegistryErrorList().getRegistryError())) {
					for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
						if (!WARNING.equals(error.getSeverity())) {
							errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
						}
					}
				}
				
				if(!StringUtility.isNullOrEmpty(errorMsg.toString())) {
					out.setEsito(false);						
					out.setErrorMessage(errorMsg.toString());
				}
			} else {
				out.setEsito(false);
				out.setErrorMessage(INIErrorEnum.BAD_REQUEST.toString());
			}

		} catch (NoRecordFoundException ne) {
			out.setEsito(false);
			out.setErrorMessage(INIErrorEnum.RECORD_NOT_FOUND.toString());
		} catch (Exception ex) {
			log.error("Error while running find and send to ini by document id: {}", deleteRequestDTO.getIdDoc());
			out.setErrorMessage(ExceptionUtils.getRootCauseMessage(ex));
			out.setEsito(false);
		}
		logResult(out.getEsito(), ProcessorOperationEnum.DELETE, startingDate, jwtPayloadDTO.getIss(), CommonUtility.extractDocumentTypeFromQueryResponse(currentMetadata), jwtPayloadDTO.getSubject_role(),out.getErrorMessage());
		return out;
	}

	@Override
	public IniResponseDTO updateByRequestBody(final UpdateRequestDTO updateRequestDTO) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		AdhocQueryResponse currentMetadata = null;
		try {
			StringBuilder errorMsg = new StringBuilder();

			// Get reference from INI UUID
			JWTPayloadDTO readJwtPayload = JsonUtility.clone(updateRequestDTO.getToken(), JWTPayloadDTO.class);
			JWTTokenDTO readJwtToken = new JWTTokenDTO(readJwtPayload);
			String uuid = iniClient.getReferenceUUID(updateRequestDTO.getIdDoc(), readJwtToken);
			currentMetadata = iniClient.getReferenceMetadata(uuid, readJwtToken);

			RegistryResponseType registryResponse = iniClient.sendUpdateData(updateRequestDTO, currentMetadata, uuid);
			out.setEsito(true);
			
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
		} catch (NoRecordFoundException ne){
			out.setEsito(false);
			out.setErrorMessage(INIErrorEnum.RECORD_NOT_FOUND.toString());
		} catch(Exception ex) {
			log.error("Error while running find and send to ini by document id: {}" , updateRequestDTO.getIdDoc());
			out.setErrorMessage(ExceptionUtils.getRootCauseMessage(ex));
			out.setEsito(false);
		}
		logResult(out.getEsito(), ProcessorOperationEnum.UPDATE, startingDate, updateRequestDTO.getToken().getIss(), CommonUtility.extractDocumentTypeFromQueryResponse(currentMetadata), updateRequestDTO.getToken().getSubject_role(),out.getErrorMessage());
		return out;
	}

	@Override
	public IniResponseDTO replaceByWorkflowInstanceId(ReplaceRequestDTO requestDTO) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		DocumentTreeDTO documentTreeDTO = null;
		try {
			StringBuilder errorMsg = new StringBuilder();
			IniEdsInvocationETY iniInvocationETY = iniInvocationRepo.findByWorkflowInstanceId(requestDTO.getWorkflowInstanceId());
			if (iniInvocationETY != null) {
				documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

				if (documentTreeDTO.checkIntegrity()) {
					RegistryResponseType res = iniClient.sendReplaceData(requestDTO, documentTreeDTO.getDocumentEntry(), documentTreeDTO.getSubmissionSetEntry(), documentTreeDTO.getTokenEntry());
					out.setEsito(true);
					if (ResponseUtility.isErrorResponse(res)) {
						out.setEsito(false);
						for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
							errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
						}
						out.setErrorMessage(errorMsg.toString());
					}
				}
			} else {
				out.setEsito(false);
				out.setErrorMessage(INIErrorEnum.RECORD_NOT_FOUND.toString());
			}

		} catch(Exception ex) {
			log.error("Error while running find and send to ini by document id: {} and workflowInstanceId: {}" , requestDTO.getIdDoc(), requestDTO.getWorkflowInstanceId());
			out.setErrorMessage(ExceptionUtils.getRootCauseMessage(ex));
			out.setEsito(false);
		}
		logResult(out.getEsito(), ProcessorOperationEnum.REPLACE, startingDate, CommonUtility.extractIssuer(documentTreeDTO), CommonUtility.extractDocumentType(documentTreeDTO), CommonUtility.extractSubjectRole(documentTreeDTO),out.getErrorMessage());
		return out;
	}

	@Override
	public AdhocQueryResponse getMetadata(String oid, JWTTokenDTO tokenDTO) {
		AdhocQueryResponse out = null;
		try {
			String uuid = iniClient.getReferenceUUID(oid, tokenDTO);
			out = iniClient.getReferenceMetadata(uuid, tokenDTO);
		} catch (NoRecordFoundException ex) {
			throw ex;
		} catch(Exception ex) {
			log.error("Error while execute getMetadati : " , ex);
			throw ex;
		}
		return out;
	}

	@Override
	public String getReference(String oid, JWTTokenDTO tokenDTO) {
		return iniClient.getReferenceUUID(oid, tokenDTO);
	}


	private void logResult(boolean isSuccess, ProcessorOperationEnum operationType, Date startingDate, String issuer, String documentType, String subjectRole,
			String errorMessage) {
		if (isSuccess) {
			logger.info("Operazione eseguita su INI", operationType.getOperation(), ResultLogEnum.OK, startingDate, issuer, documentType, subjectRole);
		} else {
			logger.error("Errore riscontrato durante l'esecuzione dell'operazione su INI:" + errorMessage, operationType.getOperation(), ResultLogEnum.KO, startingDate, operationType.getErrorType(), issuer, documentType, subjectRole);
		}
	}
}
