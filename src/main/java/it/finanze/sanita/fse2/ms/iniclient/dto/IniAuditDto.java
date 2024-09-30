package it.finanze.sanita.fse2.ms.iniclient.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class IniAuditDto {

	private String workflow_instance_id;
	private String eventType;
	private Date eventDate;
	private String soapRequest;
	private String soapResponse;
}
