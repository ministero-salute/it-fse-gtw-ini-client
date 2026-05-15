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

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.AppConstants.LOG_TYPE_CONTROL;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.AppConstants.LOG_TYPE_KPI;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;

import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMergedMetadatiDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationMockedSRV;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;

@Slf4j
@Service
public class IniInvocationMockedSRV implements IIniInvocationMockedSRV {

	// Costanti per le mappature OID -> creationTime
	private static final Map<String, String> OID_CREATION_TIME_MAP = new HashMap<>();
	private static final String SLOT_NAME_CREATION_TIME = "creationTime";

	static {
		OID_CREATION_TIME_MAP.put("AD-2.1", "20200531100000+0100");
		OID_CREATION_TIME_MAP.put("AD-2.4", "20230115100000+0100");
		OID_CREATION_TIME_MAP.put("AD-2.4.1", "20230315100000+0100");
		OID_CREATION_TIME_MAP.put("AD-2.5", "20240115100000+0100");
		OID_CREATION_TIME_MAP.put("AD-2.6", "20250315100000+0100");
		OID_CREATION_TIME_MAP.put("AD-2.6.1", "20250715100000+0100");
		OID_CREATION_TIME_MAP.put("AD-2.6.2", "20251215100000+0100");
		OID_CREATION_TIME_MAP.put("AD-2.6.3", "20260415100000+0100");
	}

	@Autowired
	private LoggerHelper logger;

	@Autowired
	private IConfigSRV config;

	@Autowired
	private IIniInvocationRepo repository;

	private final ObjectFactory rimObjectFactory = new ObjectFactory();

	@Override
	public IniResponseDTO publishOrReplaceOnIni(String workflowInstanceId, ProcessorOperationEnum operation) {
		IniResponseDTO out = new IniResponseDTO();
		out.setMessage("Regime di mock abilitato");
		mockLog(workflowInstanceId, operation, new Date());
		if(config.isRemoveMetadataEnable()) repository.removeMetadataByWorkflowInstanceId(workflowInstanceId);
		return out;
	}

	@Override
	public IniResponseDTO deleteByDocumentId(DeleteRequestDTO deleteRequestDTO) {
		final Date startingDate = new Date();
		mockLog(deleteRequestDTO.getWorkflow_instance_id(), ProcessorOperationEnum.DELETE, startingDate);
		return new IniResponseDTO();
	}

	@Override
	public IniResponseDTO updateByRequestBody(SubmitObjectsRequest submitObjectRequest, UpdateRequestDTO updateRequestDTO) {
		final Date startingDate = new Date();
		mockLog(updateRequestDTO.getWorkflow_instance_id(), ProcessorOperationEnum.UPDATE, startingDate);
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
		out.setUuid(Arrays.asList("UUID_MOCKATO"));
		out.setDocumentType("DOCUMENT_TYPE MOCKATO");
		out.setAdministrativeRequest(Arrays.asList("ADM_REQ_MOCKATO"));
		out.setAuthorInstitution("AUTH_REQ_MOCKATO");
		return out;
	}

	@Override
	public GetMergedMetadatiDTO getMergedMetadati(String oidToUpdate, MergedMetadatiRequestDTO updateRequestDTO) {
		GetMergedMetadatiDTO out = new GetMergedMetadatiDTO();

		// Analizza l'OID e costruisce la risposta appropriata
		String creationTime = getCreationTimeForOid(oidToUpdate);
		if (creationTime != null) {
			try (StringWriter sw = new StringWriter()) {
				// Costruisce l'AdhocQueryResponse con ExtrinsicObject contenente il
				// creationTime
				AdhocQueryResponse adhocQueryResponse = new AdhocQueryResponse();
				adhocQueryResponse.setRequestId("Mock response for OID: " + oidToUpdate);

				RegistryObjectListType registryObjectList = createRegistryObjectListWithCreationTime(creationTime);
				adhocQueryResponse.setRegistryObjectList(registryObjectList);

				// Marshalla l'AdhocQueryResponse in una stringa XML
				JAXB.marshal(adhocQueryResponse, sw);
				out.setMarshallResponse(sw.toString());
				out.setDocumentType("MOCKED_DOCUMENT_TYPE");
				out.setAuthorInstitution("MOCKED_AUTHOR_INSTITUTION");
				out.setAdministrativeRequest(Arrays.asList("MOCKED_ADMIN_REQ"));

			} catch (Exception ex) {
				log.error("Error while creating mocked merged metadata for OID: {}", oidToUpdate, ex);
				throw new BusinessException(ex);
			}
		} else {
			// OID non riconosciuto, restituisce DTO vuoto
			log.warn("OID pattern not recognized for mocking: {}", oidToUpdate);
		}

		return out;
	}

	private void mockLog(String workflowInstanceId, ProcessorOperationEnum operation, Date startDateOperation) {
		logger.info(
			LOG_TYPE_CONTROL,
			workflowInstanceId,
			"Regime di mock abilitato",
			operation.getOperation(),
			startDateOperation,
			"Mocked Doc Type Ini",
			"Mocked fiscal code Ini",
				JWTPayloadDTO.getMocked(),
				Arrays.asList("Mocked Admn Req"),
			"Mocked author institution"
		);
		logger.info(
			LOG_TYPE_KPI,
			null,
			"Regime di mock abilitato",
			operation.getOperation(),
			startDateOperation,
			"Mocked Doc Type Ini",
			"Mocked fiscal code Ini",
				JWTPayloadDTO.getMocked(),
			Arrays.asList("Mocked Admn Req"),
			"Mocked author institution"
		);
	}

	private String getCreationTimeForOid(String oid) {
		if (oid == null || oid.isEmpty()) {
			return null;
		}

		// Cerca il pattern OID nella mappa
		for (Map.Entry<String, String> entry : OID_CREATION_TIME_MAP.entrySet()) {
			if (oid.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private RegistryObjectListType createRegistryObjectListWithCreationTime(String creationTime) {
		RegistryObjectListType registryObjectList = rimObjectFactory.createRegistryObjectListType();

		// Crea l'ExtrinsicObject con il creationTime
		ExtrinsicObjectType extrinsicObject = createExtrinsicObjectWithCreationTime(creationTime);

		// Wrappa l'ExtrinsicObject in un JAXBElement e aggiungilo alla lista
		JAXBElement<ExtrinsicObjectType> extrinsicObjectElement = rimObjectFactory
				.createExtrinsicObject(extrinsicObject);
		registryObjectList.getIdentifiable().add(extrinsicObjectElement);

		return registryObjectList;
	}

	private ExtrinsicObjectType createExtrinsicObjectWithCreationTime(String creationTime) {
		ExtrinsicObjectType extrinsicObject = rimObjectFactory.createExtrinsicObjectType();

		// Imposta un ID per l'ExtrinsicObject (richiesto)
		extrinsicObject.setId("urn:uuid:mock-extrinsic-object-" + System.currentTimeMillis());

		// Crea e aggiungi lo Slot con il creationTime
		SlotType1 slot = createSlot(SLOT_NAME_CREATION_TIME, creationTime);
		extrinsicObject.getSlot().add(slot);

		return extrinsicObject;
	}

	private SlotType1 createSlot(String name, String value) {
		SlotType1 slot = new SlotType1();
		slot.setName(name);

		// Crea la ValueList e aggiungi il valore
		ValueListType valueList = rimObjectFactory.createValueListType();
		valueList.getValue().add(value);
		slot.setValueList(valueList);

		return slot;
	}

}
