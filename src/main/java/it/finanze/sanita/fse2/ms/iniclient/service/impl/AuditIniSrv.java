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

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.REQUEST;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.RESPONSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditDto;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditsDto;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IAuditIniRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IAuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.DateUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;

@Service
public class AuditIniSrv implements IAuditIniSrv {

	@Autowired
	private IAuditIniRepo auditRepo;
	
	@Autowired
	private IConfigSRV configSRV;

	@Override
	public IniAuditsDto findByWii(String wii) {
		List<AuditIniETY> etys = auditRepo.findByWii(wii);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		List<IniAuditDto> list = new ArrayList<>();
		for(AuditIniETY ety : etys) {
			IniAuditDto audit = new IniAuditDto();
			audit.setWorkflowInstanceId(ety.getWorkflowInstanceId());
			audit.setEventType(ety.getEventType().name());
			audit.setEventDate(sdf.format(ety.getEventDate()));

			String soapRequest = StringUtility.isNullOrEmpty(ety.getSoapRequest()) ? "" : "SOAP_REQUEST:"+ety.getSoapRequest();
			String soapResponse = StringUtility.isNullOrEmpty(ety.getSoapResponse()) ? "" : " SOAP_RESPONSE:"+ety.getSoapResponse();
			audit.setMessage(soapRequest + soapResponse);
			audit.setExpiringDate(sdf.format(ety.getExpiringDate()));
			list.add(audit);
		}
		return new IniAuditsDto(list);
	}

	@Override
	public void save(String wii, EventType eventType, Date eventDate, String reqOrRes, String soapMessage) {
		if (REQUEST.equalsIgnoreCase(reqOrRes)) {
			Date expiringDate = DateUtility.addDay(new Date(), configSRV.getExpirationDate());
			AuditIniETY audit = new AuditIniETY(wii, eventType, eventDate, soapMessage, null,expiringDate);
			auditRepo.insert(audit);
		} else if (RESPONSE.equalsIgnoreCase(reqOrRes)) {
			auditRepo.updateResponseByWiiAndEventType(wii, eventType.name(), soapMessage);
		}
	}

}
