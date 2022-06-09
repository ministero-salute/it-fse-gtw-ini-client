package it.finanze.sanita.fse2.ms.iniclient.service;

import java.io.Serializable;

import it.finanze.sanita.fse2.ms.iniclient.controller.impl.IniPublicationDTO;

public interface IIniInvocationSRV extends Serializable {

	IniPublicationDTO findAndSendToIniByWorkflowInstanceId(String workflowInstanceId);
}
