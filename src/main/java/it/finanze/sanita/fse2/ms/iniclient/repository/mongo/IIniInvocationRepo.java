package it.finanze.sanita.fse2.ms.iniclient.repository.mongo;

import java.io.Serializable;

import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;

public interface IIniInvocationRepo extends Serializable {

	IniEdsInvocationETY findByWorkflowInstanceId(String workflowInstanceId);
}
