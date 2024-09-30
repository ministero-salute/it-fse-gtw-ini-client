package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditDto;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IAuditIniRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IAuditIniSrv;

@Service
public class AuditIniSrv implements IAuditIniSrv {

	@Autowired
	private IAuditIniRepo auditRepo;

	@Override
	public IniAuditDto findByWii(String wii) {
		IniAuditDto out = null;
		AuditIniETY ety = auditRepo.findByWii(wii);
		if(ety!=null) {
			out = new IniAuditDto(wii, ety.getEventType().name(), ety.getEventDate(), ety.getSoapRequest(), ety.getSoapResponse());
		}
		return out;
	}
}
