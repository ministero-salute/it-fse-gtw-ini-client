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
package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.controller.IIniOperationCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import java.io.StringReader;

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
				mergedMetadati.getDocumentType(), mergedMetadati.getAuthorInstitution());
	}
}
