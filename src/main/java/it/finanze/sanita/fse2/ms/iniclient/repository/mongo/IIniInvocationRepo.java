/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.repository.mongo;

import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;

public interface IIniInvocationRepo {

	IniEdsInvocationETY findByWorkflowInstanceId(String workflowInstanceId);
	
}
