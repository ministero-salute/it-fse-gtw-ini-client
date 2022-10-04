package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.INIErrorEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.ResponseUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class IniInvocationSRV implements IIniInvocationSRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -8674806764400269288L;

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

				if (documentTreeDTO.checkIntegrity()) {
					RegistryResponseType res = iniClient.sendPublicationData(documentTreeDTO.getDocumentEntry(), documentTreeDTO.getSubmissionSetEntry(), documentTreeDTO.getTokenEntry());
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
			log.error("Error while running find and send to ini by workflow instance id: {}" , workflowInstanceId);
			out.setErrorMessage(ExceptionUtils.getRootCauseMessage(ex));
			out.setEsito(false);
		}
		this.logResult(out.getEsito(), ProcessorOperationEnum.PUBLISH, startingDate, CommonUtility.extractIssuer(documentTreeDTO), CommonUtility.extractDocumentType(documentTreeDTO));
		return out;
	}

	@Override
	public IniResponseDTO deleteByDocumentId(DeleteRequestDTO deleteRequestDTO) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		JWTPayloadDTO jwtPayloadDTO = CommonUtility.buildJwtPayloadFromDeleteRequest(deleteRequestDTO);

		try {
			StringBuilder errorMsg = new StringBuilder();
			if (RequestUtility.checkDeleteRequestIntegrity(deleteRequestDTO)) {
				RegistryResponseType res = iniClient.sendDeleteData(
						deleteRequestDTO.getIdDoc(),
						jwtPayloadDTO
				);
				out.setEsito(true);
				if (ResponseUtility.isErrorResponse(res)) {
					out.setEsito(false);
					for (RegistryError error : res.getRegistryErrorList().getRegistryError()) {
						errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
					}
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
		this.logResult(out.getEsito(), ProcessorOperationEnum.DELETE, startingDate, deleteRequestDTO.getIss(), this.extractDocumentTypeFromMetadata(null, deleteRequestDTO.getIdDoc(), jwtPayloadDTO));
		return out;
	}

	@Override
	public IniResponseDTO updateByRequestBody(UpdateRequestDTO updateRequestDTO) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		UpdateResponseDTO updateResponseDTO = null;
		try {
			StringBuilder errorMsg = new StringBuilder();
			updateResponseDTO = iniClient.sendUpdateData(updateRequestDTO);
			out.setEsito(true);
			if (ResponseUtility.isErrorResponse(updateResponseDTO.getRegistryResponse())) {
				out.setEsito(false);
				for (RegistryError error : updateResponseDTO.getRegistryResponse().getRegistryErrorList().getRegistryError()) {
					errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
				}
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
		AdhocQueryResponse metadata = updateResponseDTO != null && updateResponseDTO.getOldMetadata() != null ? updateResponseDTO.getOldMetadata() : null;
		this.logResult(out.getEsito(), ProcessorOperationEnum.UPDATE, startingDate, updateRequestDTO.getToken().getIss(), this.extractDocumentTypeFromMetadata(metadata, null, null));
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
		this.logResult(out.getEsito(), ProcessorOperationEnum.REPLACE, startingDate, CommonUtility.extractIssuer(documentTreeDTO), CommonUtility.extractDocumentType(documentTreeDTO));
		return out;
	}

	@Override
	public AdhocQueryResponse getMetadata(String oid, JWTTokenDTO tokenDTO) {
		AdhocQueryResponse out = null;
		try {
			String uuid = iniClient.getReferenceUUID(oid, tokenDTO);
			out = iniClient.getReferenceMetadata(uuid, tokenDTO);
		} catch(Exception ex) {
			log.error("Error while execute getMetadati : " , ex);
			throw new BusinessException(ex);
		}
		return out;
	}


	private void logResult(boolean isSuccess, ProcessorOperationEnum operationType, Date startingDate, String issuer, String documentType) {
		if (isSuccess) {
			logger.info("Operazione eseguita su INI", operationType.getOperation(), ResultLogEnum.OK, startingDate, issuer, documentType);
		} else {
			logger.error("Errore riscontrato durante l'esecuzione dell'operazione su INI", operationType.getOperation(), ResultLogEnum.KO, startingDate, operationType.getErrorType(), issuer, documentType);
		}
	}

	/**
	 * Compute type code name basing on passed metadata object
	 * If metadata does not exist, an attempt will be performed to get them from INI
	 * @param queryResponse
	 * @param idDoc
	 * @param jwtPayloadDTO
	 * @return
	 */
	public String extractDocumentTypeFromMetadata(AdhocQueryResponse queryResponse, String idDoc, JWTPayloadDTO jwtPayloadDTO) {
		log.debug("Extract document type from metadata");
		boolean isIniCallable = idDoc != null && jwtPayloadDTO != null;
		if (CommonUtility.checkMetadata(queryResponse)) {
			return CommonUtility.extractDocumentTypeFromQueryResponse(queryResponse);
		} else if (isIniCallable) {
			JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
			jwtTokenDTO.setPayload(jwtPayloadDTO);
			try {
				queryResponse = this.getMetadata(idDoc, jwtTokenDTO);
				return CommonUtility.extractDocumentTypeFromQueryResponse(queryResponse);
			} catch (Exception e) {
				return Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER;
			}
		} else {
			return Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER;
		}
	}
}
