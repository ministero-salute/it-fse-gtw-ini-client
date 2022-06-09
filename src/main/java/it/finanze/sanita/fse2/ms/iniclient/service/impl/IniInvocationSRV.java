package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.controller.impl.IniPublicationDTO;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

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
	public IniPublicationDTO findAndSendToIniByWorkflowInstanceId(final String workflowInstanceId) {
		IniPublicationDTO out = new IniPublicationDTO();
		out.setEsito(false);
		try {
			
			String errorMsg = "";
			IniEdsInvocationETY iniInvocationETY = iniInvocationRepo.findByWorkflowInstanceId(workflowInstanceId);
			if(iniInvocationETY!=null) {
				Document documentEntryDocument = null;
				Document jwtTokenDocument = null;
				Document submissionSetEntryDocument = null;

				for(Document metadata : iniInvocationETY.getMetadata()) {
					if (metadata.get("documentEntry")!=null) {
						documentEntryDocument = (Document)metadata.get("documentEntry");
						continue;
					}

					if (metadata.get("tokenEntry")!=null) {
						jwtTokenDocument = (Document)metadata.get("tokenEntry");
						continue;
					}

					if (metadata.get("submissionSetEntry")!=null) {
						submissionSetEntryDocument = (Document)metadata.get("submissionSetEntry");
						continue;
					}
				}

				if(jwtTokenDocument != null && documentEntryDocument != null && submissionSetEntryDocument != null) {
					RegistryResponseType res = iniClient.sendData(documentEntryDocument, submissionSetEntryDocument, jwtTokenDocument);
					if(res.getRegistryErrorList()!=null && res.getRegistryErrorList().getRegistryError()!=null && 
							!res.getRegistryErrorList().getRegistryError().isEmpty()) {
						out.setEsito(false);
						for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
							errorMsg = errorMsg + " SEVERITY : " + error.getSeverity() + " ERROR_CODE : " + error.getErrorCode();
						}
						out.setErrorMessage(errorMsg);
					} else {
						out.setEsito(true);
					}
				}
			} else {
				out.setErrorMessage("Nessun record trovato");
			}

		} catch(Exception ex) {
			log.error("Error while running find and send to ini by workflow instance id: {}" , workflowInstanceId);
			out.setErrorMessage(ex.getMessage());
			out.setEsito(false);
		}
		return out;
	}


}
