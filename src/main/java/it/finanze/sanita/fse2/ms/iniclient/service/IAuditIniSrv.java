package it.finanze.sanita.fse2.ms.iniclient.service;

import java.util.Date;

import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditsDto;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;

public interface IAuditIniSrv {

	IniAuditsDto findByWii(String wii);
	
	void save(String wii, EventType eventType, Date eventDate, String reqOrRes, String soapMessage);

}
