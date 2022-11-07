/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.service;

import java.io.Serializable;

import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMergedMetadatiDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;

public interface IIniInvocationSRV extends Serializable {

	IniResponseDTO publishByWorkflowInstanceId(String workflowInstanceId);

	IniResponseDTO deleteByDocumentId(DeleteRequestDTO deleteRequestDTO);

	IniResponseDTO updateByRequestBody(SubmitObjectsRequest submitObjectRequest, UpdateRequestDTO updateRequestDTO);
	
	IniResponseDTO replaceByWorkflowInstanceId(String workflowInstanceId);
    
    AdhocQueryResponse getMetadata(String oid, JWTTokenDTO tokenDTO);

	String getReference(String oid, JWTTokenDTO tokenDTO);
	
	GetMergedMetadatiDTO getMergedMetadati(String oidToUpdate,MergedMetadatiRequestDTO updateRequestDTO);
}
