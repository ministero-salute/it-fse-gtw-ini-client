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
package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import java.util.Date;

import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIniInvocationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMergedMetadatiDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;

@Service
@ConditionalOnProperty(name="ini.client.mock-enable", havingValue="true")
public class IniInvocationMockedSRV implements IIniInvocationSRV  {

	@Autowired
	private LoggerHelper logger;

	@Autowired
	private ConfigSRV config;

	@Autowired
	private IIniInvocationRepo repository;

	@Override
	public IniResponseDTO publishOrReplaceOnIni(String workflowInstanceId,ProcessorOperationEnum operation) {
		final Date startingDate = new Date();
		IniResponseDTO out = new IniResponseDTO();
		out.setErrorMessage("Regime di mock abilitato");
		logger.info(Constants.AppConstants.LOG_TYPE_CONTROL, workflowInstanceId,"Regime di mock abilitato", operation.getOperation(), 
				startingDate, 
				"Mocked Doc Type Ini", 
				"Mocked fiscal code Ini",  
				new JWTPayloadDTO());
		if(config.isRemoveMetadataEnable()) {
			repository.removeMetadataByWorkflowInstanceId(workflowInstanceId);
		}
		return out;
	}

	@Override
	public IniResponseDTO deleteByDocumentId(DeleteRequestDTO deleteRequestDTO) {
		final Date startingDate = new Date();
		logger.info(Constants.AppConstants.LOG_TYPE_CONTROL, deleteRequestDTO.getWorkflow_instance_id() ,"Regime di mock abilitato", ProcessorOperationEnum.DELETE.getOperation(), 
				startingDate, 
				"Mocked Doc Type Ini", 
				"Mocked fiscal code Ini",  
				new JWTPayloadDTO());
		return new IniResponseDTO();
	}

	@Override
	public IniResponseDTO updateByRequestBody(SubmitObjectsRequest submitObjectRequest, UpdateRequestDTO updateRequestDTO) {
		final Date startingDate = new Date();
		logger.info(Constants.AppConstants.LOG_TYPE_CONTROL, updateRequestDTO.getWorkflow_instance_id(),"Regime di mock abilitato", ProcessorOperationEnum.UPDATE.getOperation(), 
				startingDate, 
				"Mocked Doc Type Ini", 
				"Mocked fiscal code Ini",  
				new JWTPayloadDTO());
		return new IniResponseDTO();
	}


	@Override
	public AdhocQueryResponse getMetadata(String oid, JWTTokenDTO tokenDTO) {
		AdhocQueryResponse out = new AdhocQueryResponse();
		out.setRequestId("Attenzione chiamata in regime di mock");
		return out;
	}

	@Override
	public GetReferenceResponseDTO getReference(String oid, JWTTokenDTO tokenDTO) {
		GetReferenceResponseDTO out = new GetReferenceResponseDTO();
		out.setUuid("UUID_MOCKATO");
		out.setDocumentType("DOCUMENT_TYPE MOCKATO");
		return out;
	}

	@Override
	public GetMergedMetadatiDTO getMergedMetadati(String oidToUpdate, MergedMetadatiRequestDTO updateRequestDTO) {
		return new GetMergedMetadatiDTO();
	}

}
