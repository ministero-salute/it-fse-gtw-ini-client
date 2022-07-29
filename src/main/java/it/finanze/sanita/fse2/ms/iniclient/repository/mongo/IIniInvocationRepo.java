package it.finanze.sanita.fse2.ms.iniclient.repository.mongo;

import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;

import java.io.Serializable;

public interface IIniInvocationRepo extends Serializable {

	IniEdsInvocationETY findByWorkflowInstanceId(String workflowInstanceId);
}
