package it.finanze.sanita.fse2.ms.iniclient.service;

import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditDto;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;

import java.util.Date;

public interface IAuditIniSrv {

	IniAuditDto findByWii(String wii);
	
	void save(String wii, EventType eventType, Date eventDate, String reqOrRes, String soapMessage);

}
