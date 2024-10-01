package it.finanze.sanita.fse2.ms.iniclient.service;

import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditDto;

public interface IAuditIniSrv {

	IniAuditDto findByWii(String wii);
	
	void save(String request);

}
