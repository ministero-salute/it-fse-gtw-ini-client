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
package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.controller.IIniOperationCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMergedMetadatiDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMetadatiReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetReferenceReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMergedMetadatiResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMetadatiCrashResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMetadatiResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationMockedSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

/**
 * INI Publication controller.
 */
@Slf4j
@RestController
public class IniOperationCTL extends AbstractCTL implements IIniOperationCTL {

	@Autowired
	private IIniInvocationSRV iniInvocationSRV;

	@Autowired
	private IIniInvocationMockedSRV iniMockInvocationSRV;

	@Autowired
	private IIssuerSRV issuserSRV;

	@Autowired
	private IniCFG iniCFG;

	@Override
	public IniTraceResponseDTO create(final String workflowInstanceId, HttpServletRequest request) {
		log.debug("Workflow instance id received:" + workflowInstanceId + ", calling ini invocation client...");
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();

		log.info(Constants.Logs.START_LOG, Constants.Logs.CREATE, Constants.Logs.TRACE_ID_LOG,
				traceInfoDTO.getTraceID(), Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId);

		IniResponseDTO res = null;
		if (!iniCFG.isMockEnable()) {
			res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.PUBLISH);
		} else {
			IniEdsInvocationETY iniETY = iniInvocationSRV.findByWII(workflowInstanceId, ProcessorOperationEnum.PUBLISH,
					new Date());
			if (!issuserSRV.isMocked(iniETY.getIssuer())) {
				res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.PUBLISH);
			} else {
				res = iniMockInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.PUBLISH);
			}
		}

		log.info(Constants.Logs.END_LOG, Constants.Logs.CREATE, Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
				Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId);

		return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getMessage());
	}

	@Override
	public IniTraceResponseDTO delete(final DeleteRequestDTO requestBody, HttpServletRequest request) {
		log.debug("document id received: " + requestBody.getIdDoc() + ", calling ini delete client...");
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();

		log.info(Constants.Logs.START_LOG, Constants.Logs.DELETE, Constants.Logs.TRACE_ID_LOG,
				traceInfoDTO.getTraceID(), "idDoc", requestBody.getIdDoc());

		IniResponseDTO res = null;
		if (!iniCFG.isMockEnable()) {
			res = iniInvocationSRV.deleteByDocumentId(requestBody);
		} else {
			if (!issuserSRV.isMocked(requestBody.getIss())) {
				res = iniInvocationSRV.deleteByDocumentId(requestBody);
			} else {
				res = iniMockInvocationSRV.deleteByDocumentId(requestBody);
			}
		}

		log.info(Constants.Logs.END_LOG, Constants.Logs.DELETE, Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
				"idDoc", requestBody.getIdDoc());

		return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getMessage());
	}

	@Override
	public IniTraceResponseDTO update(final UpdateRequestDTO requestBody, HttpServletRequest request) {
		log.debug("Metadata received: {}, calling ini update client...", JsonUtility.objectToJson(requestBody));
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();

		log.info(Constants.Logs.START_UPDATE_LOG, Constants.Logs.UPDATE, Constants.Logs.TRACE_ID_LOG,
				traceInfoDTO.getTraceID());

		IniResponseDTO res = null;
		SubmitObjectsRequest req = JAXB.unmarshal(new StringReader(requestBody.getMarshallData()),
				SubmitObjectsRequest.class);
		if (!iniCFG.isMockEnable()) {
			res = iniInvocationSRV.updateByRequestBody(req, requestBody);
		} else {
			if (!issuserSRV.isMocked(requestBody.getToken().getIss())) {
				res = iniInvocationSRV.updateByRequestBody(req, requestBody);
			} else {
				res = iniMockInvocationSRV.updateByRequestBody(req, requestBody);
			}
		}

		log.info(Constants.Logs.END_UPDATE_LOG, Constants.Logs.UPDATE, Constants.Logs.TRACE_ID_LOG,
				traceInfoDTO.getTraceID());

		return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getMessage());
	}

	@Override
	public IniTraceResponseDTO replace(final String workflowInstanceId, HttpServletRequest request) {
		log.debug("Workflow instance id received replace:" + workflowInstanceId + ", calling ini invocation client...");
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();

		log.info(Constants.Logs.START_LOG, Constants.Logs.REPLACE,
				Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
				Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId);

		IniResponseDTO res = null;
		if (!iniCFG.isMockEnable()) {
			res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.REPLACE);
		} else {
			IniEdsInvocationETY iniETY = iniInvocationSRV.findByWII(workflowInstanceId, ProcessorOperationEnum.REPLACE,
					new Date());
			if (!issuserSRV.isMocked(iniETY.getIssuer())) {
				res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.REPLACE);
			} else {
				res = iniMockInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.REPLACE);
			}
		}

		log.info(Constants.Logs.END_LOG, Constants.Logs.REPLACE,
				Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
				Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId);

		return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getMessage());
	}

	@Override
	public ResponseEntity<GetMetadatiResponseDTO> getMetadati(String idDoc, GetMetadatiReqDTO req,
			HttpServletRequest request) {
		log.warn(
				"Get metadati - Attenzione il token usato è configurabile dalle properties. Non usare in ambiente di produzione");
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(req));

		GetMetadatiResponseDTO out = new GetMetadatiResponseDTO();
		LogTraceInfoDTO traceInfo = getLogTraceInfo();
		out.setTraceID(traceInfo.getTraceID());
		out.setSpanID(traceInfo.getSpanID());

		if (!iniCFG.isMockEnable()) {
			out.setResponse(iniInvocationSRV.getMetadata(idDoc, token));
		} else {
			if (!issuserSRV.isMocked(req.getIss())) {
				out.setResponse(iniInvocationSRV.getMetadata(idDoc, token));
			} else {
				out.setResponse(iniMockInvocationSRV.getMetadata(idDoc, token));
			}

		}

		return new ResponseEntity<>(out, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<GetReferenceResponseDTO> getReference(String idDoc, GetReferenceReqDTO req,
			HttpServletRequest request) {
		// DELETE
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(req));
		GetReferenceResponseDTO out = null;
		if (!iniCFG.isMockEnable()) {
			out = iniInvocationSRV.getReference(idDoc, token);
		} else {
			if (!issuserSRV.isMocked(req.getIss())) {
				out = iniInvocationSRV.getReference(idDoc, token);
			} else {
				out = iniMockInvocationSRV.getReference(idDoc, token);
			}

		}
		return new ResponseEntity<>(out, HttpStatus.OK);
	}

	@Override
	public GetMergedMetadatiResponseDTO getMergedMetadati(final MergedMetadatiRequestDTO requestBody,
			HttpServletRequest request) {
		log.debug("Call merged metadati");
		GetMergedMetadatiDTO mergedMetadati = null;
		if (!iniCFG.isMockEnable()) {
			mergedMetadati = iniInvocationSRV.getMergedMetadati(requestBody.getIdDoc(), requestBody);
		} else {
			if (!issuserSRV.isMocked(requestBody.getToken().getIss())) {
				mergedMetadati = iniInvocationSRV.getMergedMetadati(requestBody.getIdDoc(), requestBody);
			} else {
				mergedMetadati = iniMockInvocationSRV.getMergedMetadati(requestBody.getIdDoc(), requestBody);
			}
		}

		return new GetMergedMetadatiResponseDTO(getLogTraceInfo(), mergedMetadati.getErrorMessage(),
				mergedMetadati.getMarshallResponse(),
				mergedMetadati.getDocumentType(), mergedMetadati.getAuthorInstitution(),
				mergedMetadati.getAdministrativeRequest());
	}

	@Override
	public ResponseEntity<GetMetadatiCrashResponseDTO> getMetadatiPostCrash(String idDoc, GetMetadatiReqDTO jwtPayload,
			HttpServletRequest request) {
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(jwtPayload));

		AdhocQueryResponse res = iniInvocationSRV.getMetadata(idDoc, token);
		GetMetadatiCrashResponseDTO out = buildFromAdhocQueryRes(res);
		return new ResponseEntity<>(out, HttpStatus.OK);
	}

	private GetMetadatiCrashResponseDTO buildFromAdhocQueryRes(AdhocQueryResponse response) {
		GetMetadatiCrashResponseDTO out = new GetMetadatiCrashResponseDTO();
		 
		List<JAXBElement<? extends IdentifiableType>> identifiableList = new ArrayList<>(
				response.getRegistryObjectList().getIdentifiable());
		Optional<JAXBElement<? extends IdentifiableType>> optExtrinsicObject = identifiableList.stream()
				.filter(e -> e.getValue() instanceof ExtrinsicObjectType)
				.findFirst();
		if (optExtrinsicObject.isPresent()) {
			ExtrinsicObjectType extrinsicObject = (ExtrinsicObjectType) optExtrinsicObject.get().getValue();
			for(SlotType1 slot : extrinsicObject.getSlot()){
				if("repositoryUniqueId".equals(slot.getName())){
					out.setIdentificativoRep(slot.getValueList().getValue().get(0));
				}

				if("urn:ita:2022:administrativeRequest".equals(slot.getName())){
					out.setAdministrativeRequest(slot.getValueList().getValue());
				}
			}
		}
 
		return out;
	}
}
