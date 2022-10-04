package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import it.finanze.sanita.fse2.ms.iniclient.controller.IIniOperationCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
        log.info("Workflow instance id received:" + workflowInstanceId +", calling ini invocation client...");
        IniResponseDTO res = iniInvocationSRV.publishByWorkflowInstanceId(workflowInstanceId);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO delete(final DeleteRequestDTO requestBody, HttpServletRequest request) {
        log.info("document id received: " + requestBody.getIdDoc() + ", calling ini delete client...");
        IniResponseDTO res = iniInvocationSRV.deleteByDocumentId(requestBody);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO update(final UpdateRequestDTO requestBody, HttpServletRequest request) {
        log.info("Metadata received: {}, calling ini update client...", JsonUtility.objectToJson(requestBody));
        IniResponseDTO res = iniInvocationSRV.updateByRequestBody(requestBody);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO replace(final ReplaceRequestDTO requestDTO, HttpServletRequest request) {
        log.info("idDoc received: " + requestDTO.getIdDoc() + " - workflowInstanceId: " + requestDTO.getWorkflowInstanceId() + ", calling ini replace client...");
        IniResponseDTO res = iniInvocationSRV.replaceByWorkflowInstanceId(requestDTO);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

	@Override
	public AdhocQueryResponse getMetadati(String oid,GetMetadatiReqDTO req, HttpServletRequest request) {
		log.warn("Get metadati - Attenzione il token usato è configurabile dalle properties. Non usare in ambiente di produzione");
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(req));
		return iniInvocationSRV.getMetadata(oid, token);
	}
}
