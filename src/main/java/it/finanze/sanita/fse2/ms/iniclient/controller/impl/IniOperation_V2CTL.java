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

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.controller.IIniOperation_V2CTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationMockedSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;

/**
 * INI Publication controller.
 */
@Slf4j
@RestController
public class IniOperation_V2CTL extends AbstractCTL implements IIniOperation_V2CTL {

	@Autowired
	private IIniInvocationSRV iniInvocationSRV;

	@Autowired
	private IIniInvocationMockedSRV iniMockInvocationSRV;

	@Autowired
	private IIssuerSRV issuserSRV;

	@Autowired
	private IniCFG iniCFG;
  

	@Override
	public IniTraceResponseDTO update(final UpdateRequestDTO requestBody, HttpServletRequest request) {
		log.debug("Metadata received: {}, calling ini update client...", JsonUtility.objectToJson(requestBody));
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();

		log.info(Constants.Logs.START_UPDATE_LOG, Constants.Logs.UPDATE, Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID());

		IniResponseDTO res = null;
		SubmitObjectsRequest req = JAXB.unmarshal(new StringReader(requestBody.getMarshallData()), SubmitObjectsRequest.class);
		if (!iniCFG.isMockEnable()) {
			res = iniInvocationSRV.updateByRequestBody(req, requestBody);
		} else {
			if (!issuserSRV.isMocked(requestBody.getToken().getIss())) {
				res = iniInvocationSRV.updateByRequestBody(req, requestBody);
			} else {
				res = iniMockInvocationSRV.updateByRequestBody(req, requestBody);
			}
		}

		log.info(Constants.Logs.END_UPDATE_LOG, Constants.Logs.UPDATE, Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID());

		return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getMessage());
	}
 
}
