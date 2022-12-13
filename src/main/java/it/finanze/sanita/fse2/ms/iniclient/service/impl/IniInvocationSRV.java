/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.webjars.NotFoundException;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentTreeDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMergedMetadatiDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.INIErrorEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.UpdateBodyBuilderUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

@Service
@Slf4j
@ConditionalOnProperty(name="ini.client.mock-enable", havingValue="false")
public class IniInvocationSRV implements IIniInvocationSRV {

	private static final String WARNING = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Warning";
	
	@Autowired
	private IniInvocationRepo iniInvocationRepo;

	@Autowired
	private IIniClient iniClient;

	@Autowired
	private LoggerHelper logger;
	
	@Autowired
	private IniCFG iniCFG;

	
	@Override
	public IniResponseDTO publishOrReplaceOnIni(final String workflowInstanceId, ProcessorOperationEnum operation) {
		final Date startingDate = new Date();

		IniEdsInvocationETY iniInvocationETY = iniInvocationRepo.findByWorkflowInstanceId(workflowInstanceId);
		if(iniInvocationETY==null) {
			logger.error(INIErrorEnum.RECORD_NOT_FOUND.toString(), ProcessorOperationEnum.PUBLISH.getOperation(), 
					ResultLogEnum.KO, startingDate, ProcessorOperationEnum.PUBLISH.getErrorType(), 
					Constants.IniClientConstants.JWT_MISSING_ISSUER_PLACEHOLDER, 
					Constants.IniClientConstants.MISSING_DOC_TYPE_PLACEHOLDER, 
					Constants.IniClientConstants.JWT_MISSING_ISSUER_PLACEHOLDER, 
					Constants.IniClientConstants.JWT_MISSING_SUBJECT, 
					Constants.IniClientConstants.JWT_MISSING_LOCALITY);
			throw new NotFoundException(INIErrorEnum.RECORD_NOT_FOUND.toString());
		}

		IniResponseDTO out = null;
		if(ProcessorOperationEnum.PUBLISH.equals(operation)) {
			out = publishByWorkflowInstanceId(iniInvocationETY,startingDate);
		} else if(ProcessorOperationEnum.REPLACE.equals(operation)) {
			out = replaceByWorkflowInstanceId(iniInvocationETY,startingDate);
		}
		return out;
	}

	private IniResponseDTO publishByWorkflowInstanceId(final IniEdsInvocationETY iniInvocationETY, final Date startingDate) {
		DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

		String issuer = CommonUtility.extractIssuer(documentTreeDTO);
		String documentType = CommonUtility.extractDocumentType(documentTreeDTO);
		String subjectRole = CommonUtility.extractSubjectRole(documentTreeDTO);
		String fiscalCode = CommonUtility.extractFiscalCodeFromDocumentTree(documentTreeDTO);
		String locality =  CommonUtility.extractLocality(documentTreeDTO);
		IniResponseDTO out = new IniResponseDTO();
		try { 
			RegistryResponseType res = iniClient.sendPublicationData(documentTreeDTO.getDocumentEntry(), documentTreeDTO.getSubmissionSetEntry(), documentTreeDTO.getTokenEntry());
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
				logger.error(message, ProcessorOperationEnum.PUBLISH.getOperation(), ResultLogEnum.KO, startingDate, ProcessorOperationEnum.PUBLISH.getErrorType(), issuer, documentType, subjectRole, fiscalCode, locality);
			} else {
				logger.info(message, ProcessorOperationEnum.PUBLISH.getOperation(), ResultLogEnum.OK, startingDate, issuer, documentType, subjectRole,fiscalCode,locality);
			}
		} catch(Exception ex) {
			logger.error("Errore riscontrato durante l'esecuzione dell'operazione su INI:" + out.getErrorMessage(), 
					ProcessorOperationEnum.PUBLISH.getOperation(), ResultLogEnum.KO, startingDate, ProcessorOperationEnum.PUBLISH.getErrorType(), issuer, documentType, subjectRole,
					fiscalCode, locality);
			throw new BusinessException(ex);
		}

		return out;
	}
	
	private IniResponseDTO replaceByWorkflowInstanceId(final IniEdsInvocationETY iniInvocationETY, final Date startingDate) {
		IniResponseDTO out = new IniResponseDTO();
		DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

		String issuer = CommonUtility.extractIssuer(documentTreeDTO);
		String documentType = CommonUtility.extractDocumentType(documentTreeDTO);
		String subjectRole = CommonUtility.extractSubjectRole(documentTreeDTO);
		String fiscalCode = CommonUtility.extractFiscalCodeFromDocumentTree(documentTreeDTO);
		String locality =  CommonUtility.extractLocality(documentTreeDTO);

		try {
			RegistryResponseType res = iniClient.sendReplaceData(documentTreeDTO.getDocumentEntry(), documentTreeDTO.getSubmissionSetEntry(), documentTreeDTO.getTokenEntry(), iniInvocationETY.getRiferimentoIni());
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
				logger.error(message, ProcessorOperationEnum.REPLACE.getOperation(), ResultLogEnum.KO, startingDate, ProcessorOperationEnum.REPLACE.getErrorType(), issuer, documentType, subjectRole, fiscalCode, locality);
			} else {
				logger.info(message, ProcessorOperationEnum.REPLACE.getOperation(), ResultLogEnum.OK, startingDate, issuer, documentType, subjectRole,fiscalCode,locality);
			}
		} catch(Exception ex) {
			logger.error("Errore riscontrato durante l'esecuzione dell'operazione su INI:" + out.getErrorMessage(), 
					ProcessorOperationEnum.REPLACE.getOperation(), ResultLogEnum.KO, startingDate, ProcessorOperationEnum.REPLACE.getErrorType(), issuer, documentType, subjectRole,
					fiscalCode, locality);
			throw new BusinessException(ex);
		}
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

		logResult(out.getEsito(), ProcessorOperationEnum.DELETE, startingDate, jwtPayloadDTO.getIss(), CommonUtility.extractDocumentTypeFromQueryResponse(currentMetadata), jwtPayloadDTO.getSubject_role(), out.getErrorMessage(), CommonUtility.extractFiscalCodeFromJwtSub(deleteRequestDTO.getSub()), jwtPayloadDTO.getLocality());
		return out;
	}

	@Override
	public IniResponseDTO updateByRequestBody(SubmitObjectsRequest submitObjectRequest, final UpdateRequestDTO updateRequestDTO) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		AdhocQueryResponse currentMetadata = null;
		try {
			StringBuilder errorMsg = new StringBuilder();

			// Get reference from INI UUID
			JWTTokenDTO token = new JWTTokenDTO(updateRequestDTO.getToken());
			RegistryResponseType registryResponse = iniClient.sendUpdateData(submitObjectRequest,token);
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
			//			log.error("Error while running find and send to ini by document id: {}" , updateRequestDTO.getIdDoc());
			out.setErrorMessage(ExceptionUtils.getRootCauseMessage(ex));
			out.setEsito(false);
		}
		logResult(out.getEsito(), ProcessorOperationEnum.UPDATE, startingDate, updateRequestDTO.getToken().getIss(), CommonUtility.extractDocumentTypeFromQueryResponse(currentMetadata), updateRequestDTO.getToken().getSubject_role(), out.getErrorMessage(), CommonUtility.extractFiscalCodeFromJwtSub(updateRequestDTO.getToken().getSub()), updateRequestDTO.getToken().getLocality());
		return out;
	}


	@Override
	public AdhocQueryResponse getMetadata(String oid, JWTTokenDTO tokenDTO) {
		AdhocQueryResponse out = new AdhocQueryResponse();
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
			String errorMessage, String subjectFiscalCode, String locality) {
		if (isSuccess) {
			if(!iniCFG.isMockEnable()) {
				logger.info("Operazione eseguita su INI", operationType.getOperation(), ResultLogEnum.OK, startingDate, issuer, documentType, subjectRole, subjectFiscalCode, locality);
			} else {
				logger.info("Operazione eseguita su INI in regime di MOCK assicurarsi che sia voluto", operationType.getOperation(), ResultLogEnum.OK, startingDate, issuer, documentType, subjectRole, subjectFiscalCode, locality);
			}
		} else {
			logger.error("Errore riscontrato durante l'esecuzione dell'operazione su INI:" + errorMessage, operationType.getOperation(), ResultLogEnum.KO, startingDate, operationType.getErrorType(), issuer, documentType, subjectRole, subjectFiscalCode, locality);
		}
	}
	
	@Override
	public GetMergedMetadatiDTO getMergedMetadati(final String oidToUpdate,final MergedMetadatiRequestDTO updateRequestDTO) {
		GetMergedMetadatiDTO out = new GetMergedMetadatiDTO();
		JWTTokenDTO token = new JWTTokenDTO(updateRequestDTO.getToken());
		String uuid = "";
		try {
			uuid = iniClient.getReferenceUUID(oidToUpdate, token);
		} catch(Exception ex) {
			out.setErrorMessage("Error while perform getReferenceUuid:" + ExceptionUtils.getMessage(ex));
			log.error("Error while perform get reference uuid", ex);
		}

		AdhocQueryResponse oldMetadata = null;
		if(StringUtility.isNullOrEmpty(out.getErrorMessage())) {
			try {
				oldMetadata = iniClient.getReferenceMetadata(uuid, token);
				if(oldMetadata==null) {
					throw new BusinessException("Nessun metadato trovato");
				}
			} catch(Exception ex) {
				out.setErrorMessage("Error while perform getReferenceMetadata:" + ExceptionUtils.getMessage(ex));
				log.error("Error while perform getReferenceMetadata", ex);
			}

			if(StringUtility.isNullOrEmpty(out.getErrorMessage())) {
				StringWriter sw = new StringWriter();;
				try {
					if(oldMetadata==null) {
						throw new BusinessException("Nessun metadato trovato");
					}
					SubmitObjectsRequest req = UpdateBodyBuilderUtility.buildSubmitObjectRequest(updateRequestDTO,oldMetadata.getRegistryObjectList(), uuid,token);
					JAXB.marshal(req, sw);
					out.setMarshallResponse(sw.toString());
				} catch(Exception ex) {
					out.setErrorMessage("Error while merge metadati:" + ExceptionUtils.getMessage(ex));
					log.error("Error while merge metadati", ex);
				} finally {
					try {
						sw.close();
					} catch (IOException e) {
						log.error("Error while close stream");
					}
				}
			}
		}
		return out;
	}
	
	 
}
