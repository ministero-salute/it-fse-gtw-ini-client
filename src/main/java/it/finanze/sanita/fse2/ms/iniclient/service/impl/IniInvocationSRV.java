package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.INIErrorEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.ResponseUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Override
	public IniResponseDTO publishByWorkflowInstanceId(final String workflowInstanceId) {
		IniResponseDTO out = new IniResponseDTO();
		try { 
			StringBuilder errorMsg = new StringBuilder();
			IniEdsInvocationETY iniInvocationETY = iniInvocationRepo.findByWorkflowInstanceId(workflowInstanceId);
			if (iniInvocationETY != null) {
				DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

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
		return out;
	}

	@Override
	public IniResponseDTO deleteByDocumentId(DeleteRequestDTO deleteRequestDTO) {
		IniResponseDTO out = new IniResponseDTO();
		try {
			StringBuilder errorMsg = new StringBuilder();
			JWTPayloadDTO jwtPayloadDTO = this.buildJwtPayloadFromDeleteRequest(deleteRequestDTO);
			if (RequestUtility.checkDeleteRequestIntegrity(deleteRequestDTO)) {
				RegistryResponseType res = iniClient.sendDeleteData(
						deleteRequestDTO.getIdDoc(),
						jwtPayloadDTO
				);
				out.setEsito(true);
				if (ResponseUtility.isErrorResponse(res)) {
					out.setEsito(false);
					for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
						errorMsg.append(Constants.IniClientConstants.SEVERITY_HEAD_ERROR_MESSAGE).append(error.getSeverity()).append(Constants.IniClientConstants.CODE_HEAD_ERROR_MESSAGE).append(error.getErrorCode());
					}
					out.setErrorMessage(errorMsg.toString());
				}
			} else {
				out.setEsito(false);
				out.setErrorMessage(INIErrorEnum.BAD_REQUEST.toString());
			}

		} catch (NoRecordFoundException ne){
			out.setEsito(false);
			out.setErrorMessage(INIErrorEnum.RECORD_NOT_FOUND.toString());
		} catch(Exception ex) {
			log.error("Error while running find and send to ini by document id: {}" , deleteRequestDTO.getIdDoc());
			out.setErrorMessage(ExceptionUtils.getRootCauseMessage(ex));
			out.setEsito(false);
		}
		return out;
	}

	private JWTPayloadDTO buildJwtPayloadFromDeleteRequest(DeleteRequestDTO deleteRequestDTO) {
		log.debug("Build payload information");
		return JWTPayloadDTO.builder()
				.attachment_hash(null)
				.aud(null)
				.exp(0)
				.iat(0)
				.jti(null)
				.action_id(deleteRequestDTO.getAction_id())
				.patient_consent(deleteRequestDTO.getPatient_consent())
				.iss(deleteRequestDTO.getIss())
				.locality(deleteRequestDTO.getLocality())
				.person_id(deleteRequestDTO.getPerson_id())
				.purpose_of_use(deleteRequestDTO.getPurpose_of_use())
				.resource_hl7_type(deleteRequestDTO.getResource_hl7_type())
				.sub(deleteRequestDTO.getSub())
				.subject_organization(deleteRequestDTO.getSubject_organization())
				.subject_organization_id(deleteRequestDTO.getSubject_organization_id())
				.subject_role(deleteRequestDTO.getSubject_role())
				.build();
	}

	@Override
	public IniResponseDTO updateByRequestBody(UpdateRequestDTO updateRequestDTO) {
		IniResponseDTO out = new IniResponseDTO();
		try {
			StringBuilder errorMsg = new StringBuilder();
			RegistryResponseType res = iniClient.sendUpdateData(updateRequestDTO);
			out.setEsito(true);
			if (ResponseUtility.isErrorResponse(res)) {
				out.setEsito(false);
				for (RegistryError error : res.getRegistryErrorList().getRegistryError()) {
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
		return out;
	}

	@Override
	public IniResponseDTO replaceByWorkflowInstanceId(ReplaceRequestDTO requestDTO) {
		IniResponseDTO out = new IniResponseDTO();
		try {
			StringBuilder errorMsg = new StringBuilder();
			IniEdsInvocationETY iniInvocationETY = iniInvocationRepo.findByWorkflowInstanceId(requestDTO.getWorkflowInstanceId());
			if (iniInvocationETY != null) {
				DocumentTreeDTO documentTreeDTO = RequestUtility.extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

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
		return out;
	}

	@Override
	public AdhocQueryResponse getMetadati(String oid, JWTTokenDTO tokenDTO) {
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
	 
}
