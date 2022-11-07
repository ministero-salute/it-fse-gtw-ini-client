/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.iniclient.controller.IIniOperationCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMergedMetadatiDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMetadatiReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetReferenceReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMergedMetadatiResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMetadatiResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;

/**
 *
 *	INI Publication controller.
 */
@Slf4j
@RestController
public class IniOperationCTL extends AbstractCTL implements IIniOperationCTL {

    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 8414558048109050743L;
	
	@Autowired
	private IIniInvocationSRV iniInvocationSRV;
    
    
    @Override
    public IniTraceResponseDTO create(final String workflowInstanceId, HttpServletRequest request) {
        log.debug("Workflow instance id received:" + workflowInstanceId +", calling ini invocation client...");
        IniResponseDTO res = iniInvocationSRV.publishByWorkflowInstanceId(workflowInstanceId);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO delete(final DeleteRequestDTO requestBody, HttpServletRequest request) {
        log.debug("document id received: " + requestBody.getIdDoc() + ", calling ini delete client...");
        IniResponseDTO res = iniInvocationSRV.deleteByDocumentId(requestBody);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }
 
    
    @Override
    public IniTraceResponseDTO update(final UpdateRequestDTO requestBody, HttpServletRequest request) {
        log.debug("Metadata received: {}, calling ini update client...", JsonUtility.objectToJson(requestBody));
        SubmitObjectsRequest req =  JAXB.unmarshal(new StringReader(requestBody.getMarshallData()), SubmitObjectsRequest.class);
        IniResponseDTO res = iniInvocationSRV.updateByRequestBody(req, requestBody);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO replace(final String workflowInstanceId, HttpServletRequest request) {
    	log.debug("Workflow instance id received replace:" + workflowInstanceId +", calling ini invocation client...");
        IniResponseDTO res = iniInvocationSRV.replaceByWorkflowInstanceId(workflowInstanceId);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

	@Override
	public ResponseEntity<GetMetadatiResponseDTO> getMetadati(String idDoc,GetMetadatiReqDTO req, HttpServletRequest request) {
		log.warn("Get metadati - Attenzione il token usato è configurabile dalle properties. Non usare in ambiente di produzione");
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(req));
		
		GetMetadatiResponseDTO out = new GetMetadatiResponseDTO();
		LogTraceInfoDTO traceInfo = getLogTraceInfo();
		out.setTraceID(traceInfo.getTraceID());
		out.setSpanID(traceInfo.getSpanID());
		try {
			out.setResponse(iniInvocationSRV.getMetadata(idDoc, token)); 
			return new ResponseEntity<>(out, HttpStatus.OK);
		} catch(NoRecordFoundException ex) {
			out.setErrorMessage(ExceptionUtils.getMessage(ex));
			return new ResponseEntity<>(out, HttpStatus.NOT_FOUND);
		} catch(Exception ex) {
			log.error("Error while perform get metadati :" , ex);
			out.setErrorMessage(ExceptionUtils.getMessage(ex));
			return new ResponseEntity<>(out, HttpStatus.FORBIDDEN);
		}
	}

	@Override
	public ResponseEntity<GetReferenceResponseDTO> getReference(String idDoc, GetReferenceReqDTO req, HttpServletRequest request) {
		log.warn("Get reference - Attenzione il token usato è configurabile dalle properties. Non usare in ambiente di produzione");
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(req));

		GetReferenceResponseDTO out = new GetReferenceResponseDTO();
		LogTraceInfoDTO traceInfo = getLogTraceInfo();
		out.setTraceID(traceInfo.getTraceID());
		out.setSpanID(traceInfo.getSpanID());

		try {
			out.setUuid(iniInvocationSRV.getReference(idDoc, token));
			return new ResponseEntity<>(out, HttpStatus.OK);
		} catch(NoRecordFoundException ex) {
			out.setErrorMessage(ExceptionUtils.getMessage(ex));
			return new ResponseEntity<>(out, HttpStatus.NOT_FOUND);
		} catch(Exception ex) {
			log.error("Error while perform get reference :" , ex);
			out.setErrorMessage(ExceptionUtils.getMessage(ex));
			return new ResponseEntity<>(out, HttpStatus.FORBIDDEN);
		}
	}
	
	@Override
	public GetMergedMetadatiResponseDTO getMergedMetadati(final MergedMetadatiRequestDTO requestBody, HttpServletRequest request) {
		log.debug("Call merged metadati");
		GetMergedMetadatiDTO mergedMetadati = iniInvocationSRV.getMergedMetadati(requestBody.getIdDoc(),requestBody);
		return new GetMergedMetadatiResponseDTO(getLogTraceInfo(), mergedMetadati);
	}
}
