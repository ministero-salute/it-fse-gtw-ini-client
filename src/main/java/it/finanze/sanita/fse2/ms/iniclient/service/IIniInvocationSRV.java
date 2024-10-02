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
package it.finanze.sanita.fse2.ms.iniclient.service;

import java.util.Date;

import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;

public interface IIniInvocationSRV {

	IniResponseDTO publishOrReplaceOnIni(String workflowInstanceId, ProcessorOperationEnum operation, IniEdsInvocationETY ety);
	
	IniResponseDTO deleteByDocumentId(DeleteRequestDTO deleteRequestDTO);

	IniResponseDTO updateByRequestBody(SubmitObjectsRequest submitObjectRequest, UpdateRequestDTO updateRequestDTO);
	
    AdhocQueryResponse getMetadata(String oid, JWTTokenDTO tokenDTO);

    GetReferenceResponseDTO getReference(String oid, JWTTokenDTO tokenDTO,String workflowInstanceId);

	GetMergedMetadatiDTO getMergedMetadati(String oidToUpdate,MergedMetadatiRequestDTO updateRequestDTO);
	
	IniEdsInvocationETY findByWII(String workflowInstanceId, final ProcessorOperationEnum operation, Date startingDate);
	
	
}
