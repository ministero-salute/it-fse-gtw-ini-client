package it.finanze.sanita.fse2.ms.iniclient.service;

import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ReplaceRequestDTO;

import java.io.Serializable;

public interface IIniInvocationSRV extends Serializable {

	IniResponseDTO publishByWorkflowInstanceId(String workflowInstanceId);

	IniResponseDTO deleteByDocumentId(DeleteRequestDTO deleteRequestDTO);

	IniResponseDTO updateByWorkflowInstanceId(String identificativoDocUpdate);

    IniResponseDTO replaceByWorkflowInstanceId(ReplaceRequestDTO requestDTO);
}
