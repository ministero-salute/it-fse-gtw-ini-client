/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
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
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;

/**
 *	INI Publication controller.
 */
@Slf4j
@RestController
public class IniOperationCTL extends AbstractCTL implements IIniOperationCTL {

	
	@Autowired
	private IIniInvocationSRV iniInvocationSRV;
    
    
    @Override
    public IniTraceResponseDTO create(final String workflowInstanceId, HttpServletRequest request) {
    	log.debug("Workflow instance id received:" + workflowInstanceId +", calling ini invocation client...");
    	final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();
    	
        log.info(Constants.Logs.START_LOG, Constants.Logs.CREATE,
    			Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
        		Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId );
        
        IniResponseDTO res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.PUBLISH);
        
        log.info(Constants.Logs.END_LOG, Constants.Logs.CREATE,
    			Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
        		Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId );
        
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO delete(final DeleteRequestDTO requestBody, HttpServletRequest request) {
    	log.debug("document id received: " + requestBody.getIdDoc() + ", calling ini delete client...");
    	final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();
        
        log.info(Constants.Logs.START_LOG, Constants.Logs.DELETE,
    			Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
    			"idDoc", requestBody.getIdDoc()
    			);
        
        IniResponseDTO res = iniInvocationSRV.deleteByDocumentId(requestBody);
        
        log.info(Constants.Logs.END_LOG, Constants.Logs.DELETE,
    			Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
    			"idDoc", requestBody.getIdDoc()
    			);
        
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }
 
    
    @Override
    public IniTraceResponseDTO update(final UpdateRequestDTO requestBody, HttpServletRequest request) {
        log.debug("Metadata received: {}, calling ini update client...", JsonUtility.objectToJson(requestBody));
        final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();
        
        log.info(Constants.Logs.START_UPDATE_LOG, Constants.Logs.UPDATE,
    			Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID()
    			);
        
        SubmitObjectsRequest req =  JAXB.unmarshal(new StringReader(requestBody.getMarshallData()), SubmitObjectsRequest.class);
        IniResponseDTO res = iniInvocationSRV.updateByRequestBody(req, requestBody);
        
        log.info(Constants.Logs.END_UPDATE_LOG, Constants.Logs.UPDATE,
    			Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID()
    			);
        
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO replace(final String workflowInstanceId, HttpServletRequest request) {
    	log.debug("Workflow instance id received replace:" + workflowInstanceId +", calling ini invocation client...");
    	final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();
    	
    	log.info(Constants.Logs.START_LOG, Constants.Logs.REPLACE,
    			Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
    			Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId
    			);
    	
    	IniResponseDTO res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.REPLACE);
        
    	log.info(Constants.Logs.END_LOG, Constants.Logs.REPLACE,
    			Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
    			Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId
    			);
    	
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
    	out.setResponse(iniInvocationSRV.getMetadata(idDoc, token)); 
    	return new ResponseEntity<>(out, HttpStatus.OK);
    }
 
	
	@Override
	public ResponseEntity<GetReferenceResponseDTO> getReference(String idDoc, GetReferenceReqDTO req, HttpServletRequest request) {
		//DELETE
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(req));
		return new ResponseEntity<>(iniInvocationSRV.getReference(idDoc, token), HttpStatus.OK);
	}
	
	@Override
	public GetMergedMetadatiResponseDTO getMergedMetadati(final MergedMetadatiRequestDTO requestBody, HttpServletRequest request) {
		log.debug("Call merged metadati");
		GetMergedMetadatiDTO mergedMetadati = iniInvocationSRV.getMergedMetadati(requestBody.getIdDoc(),requestBody);
		return new GetMergedMetadatiResponseDTO(getLogTraceInfo(), mergedMetadati.getErrorMessage(), mergedMetadati.getMarshallResponse(),
				mergedMetadati.getDocumentType());
	}
}
