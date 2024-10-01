package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.SOAPLoggingHandler;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditDto;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IAuditIniRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IAuditIniSrv;

import java.util.Date;

@Service
public class AuditIniSrv implements IAuditIniSrv {

	@Autowired
	private IAuditIniRepo auditRepo;

	@Autowired
	private IConfigSRV configSRV;

	@Override
	public IniAuditDto findByWii(String wii) {
		IniAuditDto out = null;
		AuditIniETY ety = auditRepo.findByWii(wii);
		if(ety!=null) {
			out = new IniAuditDto(wii, ety.getEventType().name(), ety.getEventDate(), ety.getSoapRequest(), ety.getSoapResponse());
		}
		return out;
	}

	@Override
	public void save(String wii, EventType eventType, Date eventDate, String reqOrRes, String soapMessage) {
		//TODO: implement logic to take also issuer into account
		boolean issuerFound = true;
		if (configSRV.isAuditIniEnable() && issuerFound) {
			if (SOAPLoggingHandler.REQUEST.equalsIgnoreCase(reqOrRes)) {
				AuditIniETY audit = new AuditIniETY(wii, eventType, eventDate, soapMessage, null);
				auditRepo.insert(audit);
			} else if (SOAPLoggingHandler.RESPONSE.equalsIgnoreCase(reqOrRes)) {
				auditRepo.updateResponseByWii(wii, soapMessage);
			}
		}
	}

}
