package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.iniclient.controller.IINIPublicationCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniPublicationResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *	INI Publication controller.
 */
@Slf4j
@RestController
public class INIPublicationCTL extends AbstractCTL implements IINIPublicationCTL {

    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 8414558048109050743L;
	
	@Autowired
	private IIniInvocationSRV iniInvocationSRV;
    
    
    @Override
    public IniPublicationResponseDTO publicationCreation(String workflowInstanceId, HttpServletRequest request) {
        log.info("Workflow instance id received, calling ini invocation client...");
        IniPublicationDTO res = iniInvocationSRV.findAndSendToIniByWorkflowInstanceId(workflowInstanceId);
        return new IniPublicationResponseDTO(getLogTraceInfo(), res.getEsito(), res.getErrorMessage());
    }
    
}
