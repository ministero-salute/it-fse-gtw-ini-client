package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import it.finanze.sanita.fse2.ms.iniclient.controller.IIniOperationCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ReplaceRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import lombok.extern.slf4j.Slf4j;
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
        log.info("document id received:" + requestBody.getIdentificativoDocUpdate() +", calling ini delete client...");
        IniResponseDTO res = iniInvocationSRV.deleteByDocumentId(requestBody);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO update(final String workflowInstanceId, HttpServletRequest request) {
        log.info("Workflow instance id received:" + workflowInstanceId +", calling ini update client...");
        IniResponseDTO res = iniInvocationSRV.updateByWorkflowInstanceId(workflowInstanceId);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }

    @Override
    public IniTraceResponseDTO replace(final ReplaceRequestDTO requestDTO, HttpServletRequest request) {
        log.info("identificativoDocUpdate received: " + requestDTO.getIdentificativoDocUpdate() + " - workflowInstanceId: " + requestDTO.getWorkflowInstanceId() + ", calling ini replace client...");
        IniResponseDTO res = iniInvocationSRV.replaceByWorkflowInstanceId(requestDTO);
        return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }
}
