package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.REQUEST;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.RESPONSE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditDto;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditsDto;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IAuditIniRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IAuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;

@Service
public class AuditIniSrv implements IAuditIniSrv {

	@Autowired
	private IAuditIniRepo auditRepo;

	@Override
	public IniAuditsDto findByWii(String wii) {
		List<AuditIniETY> etys = auditRepo.findByWii(wii);
		
		List<IniAuditDto> list = new ArrayList<>();
		for(AuditIniETY ety : etys) {
			IniAuditDto audit = new IniAuditDto();
			audit.setWorkflowInstanceId(ety.getWorkflowInstanceId());
			audit.setEventType(ety.getEventType().name());
			audit.setEventDate(""+ety.getEventDate());
			Map<String,String> map = new HashMap<>();
			String message = "SOAP_REQUEST:"+ety.getSoapRequest() + " SOAP_RESPONSE:"+ ety.getSoapResponse();
			audit.setMessage(message);
			audit.setExpiringDate(""+ety.getEventDate());
			list.add(audit);
		}
		return new IniAuditsDto(list);
	}

	@Override
	public void save(String wii, EventType eventType, Date eventDate, String reqOrRes, String soapMessage) {
		if (REQUEST.equalsIgnoreCase(reqOrRes)) {
			AuditIniETY audit = new AuditIniETY(wii, eventType, eventDate, soapMessage, null);
			auditRepo.insert(audit);
		} else if (RESPONSE.equalsIgnoreCase(reqOrRes)) {
			auditRepo.updateResponseByWii(wii, soapMessage);
		}
	}

}
