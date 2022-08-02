package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.ResponseUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
	private IIniClient iniClient;

	@Override
	public IniResponseDTO publishByWorkflowInstanceId(final String workflowInstanceId) {
		IniResponseDTO out = new IniResponseDTO();
		try { 
			StringBuilder errorMsg = new StringBuilder();
			IniEdsInvocationETY iniInvocationETY = iniInvocationRepo.findByWorkflowInstanceId(workflowInstanceId);
			if (iniInvocationETY != null) {
				DocumentTreeDTO documentTreeDTO = extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

				if (documentTreeDTO.checkIntegrity()) {
					RegistryResponseType res = iniClient.sendPublicationData(documentTreeDTO.getDocumentEntry(), documentTreeDTO.getSubmissionSetEntry(), documentTreeDTO.getTokenEntry());
					out.setEsito(true);
					if (ResponseUtility.isErrorResponse(res)) {
						out.setEsito(false);
						for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
							errorMsg.append(" SEVERITY : ").append(error.getSeverity()).append(" ERROR_CODE : ").append(error.getErrorCode());
						}
						out.setErrorMessage(errorMsg.toString());
					}  
				}
			} else {
				out.setEsito(false);
				out.setErrorMessage(Constants.IniClientConstants.RECORD_NOT_FOUND);
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
			if (checkDeleteRequestIntegrity(deleteRequestDTO)) {
				RegistryResponseType res = iniClient.sendDeleteData(
						deleteRequestDTO.getIdentificativoDelete(),
						jwtPayloadDTO
				);
				out.setEsito(true);
				if (ResponseUtility.isErrorResponse(res)) {
					out.setEsito(false);
					for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
						errorMsg.append(" SEVERITY : ").append(error.getSeverity()).append(" ERROR_CODE : ").append(error.getErrorCode());
					}
					out.setErrorMessage(errorMsg.toString());
				}
			} else {
				out.setEsito(false);
				out.setErrorMessage(Constants.IniClientConstants.RECORD_NOT_FOUND);
			}

		} catch(Exception ex) {
			log.error("Error while running find and send to ini by document id: {}" , deleteRequestDTO.getIdentificativoDelete());
			out.setErrorMessage(ExceptionUtils.getRootCauseMessage(ex));
			out.setEsito(false);
		}
		return out;
	}

	private JWTPayloadDTO buildJwtPayloadFromDeleteRequest(DeleteRequestDTO deleteRequestDTO) {
		log.info("Build payload information");
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
	public IniResponseDTO updateByWorkflowInstanceId(String identificativoDocUpdate) {
		IniResponseDTO out = new IniResponseDTO();
		try {
			StringBuilder errorMsg = new StringBuilder();
			IniEdsInvocationETY iniInvocationETY = iniInvocationRepo.findByWorkflowInstanceId(identificativoDocUpdate);
			if (iniInvocationETY != null) {
				DocumentTreeDTO documentTreeDTO = extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

				if (documentTreeDTO.checkIntegrity()) {
					RegistryResponseType res = iniClient.sendUpdateData(documentTreeDTO.getDocumentEntry(), documentTreeDTO.getSubmissionSetEntry(), documentTreeDTO.getTokenEntry());
					out.setEsito(true);
					if (ResponseUtility.isErrorResponse(res)) {
						out.setEsito(false);
						for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
							errorMsg.append(" SEVERITY : ").append(error.getSeverity()).append(" ERROR_CODE : ").append(error.getErrorCode());
						}
						out.setErrorMessage(errorMsg.toString());
					}
				}
			} else {
				out.setEsito(false);
				out.setErrorMessage(Constants.IniClientConstants.RECORD_NOT_FOUND);
			}

		} catch(Exception ex) {
			log.error("Error while running find and send to ini by document id: {}" , identificativoDocUpdate);
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
				DocumentTreeDTO documentTreeDTO = extractDocumentsFromMetadata(iniInvocationETY.getMetadata());

				if (documentTreeDTO.checkIntegrity()) {
					RegistryResponseType res = iniClient.sendReplaceData(requestDTO, documentTreeDTO.getDocumentEntry(), documentTreeDTO.getSubmissionSetEntry(), documentTreeDTO.getTokenEntry());
					out.setEsito(true);
					if (ResponseUtility.isErrorResponse(res)) {
						out.setEsito(false);
						for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
							errorMsg.append(" SEVERITY : ").append(error.getSeverity()).append(" ERROR_CODE : ").append(error.getErrorCode());
						}
						out.setErrorMessage(errorMsg.toString());
					}
				}
			} else {
				out.setEsito(false);
				out.setErrorMessage(Constants.IniClientConstants.RECORD_NOT_FOUND);
			}

		} catch(Exception ex) {
			log.error("Error while running find and send to ini by document id: {} and workflowInstanceId: {}" , requestDTO.getIdentificativoDocUpdate(), requestDTO.getWorkflowInstanceId());
			out.setErrorMessage(ExceptionUtils.getRootCauseMessage(ex));
			out.setEsito(false);
		}
		return out;
	}

	/**
	 * Extract metadata from entity
	 * @param metadata
	 * @return
	 */
	private DocumentTreeDTO extractDocumentsFromMetadata(List<Document> metadata) {
		DocumentTreeDTO documentTreeDTO = new DocumentTreeDTO();
		for (Document meta : metadata) {
			if (meta.get("documentEntry") != null) {
				documentTreeDTO.setDocumentEntry((Document) meta.get("documentEntry"));
			}

			if (meta.get("tokenEntry") != null) {
				documentTreeDTO.setTokenEntry((Document) meta.get("tokenEntry"));
			}

			if (meta.get("submissionSetEntry") != null) {
				documentTreeDTO.setSubmissionSetEntry((Document) meta.get("submissionSetEntry"));
			}
		}

		return documentTreeDTO;
	}

	private boolean checkDeleteRequestIntegrity(DeleteRequestDTO deleteRequestDTO) {
		return deleteRequestDTO.getIdentificativoDelete() != null && !StringUtility.isNullOrEmpty(deleteRequestDTO.getIdentificativoDelete());
	}
}
