package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.Data;


@Data
public class IniAuditDto {

	private String workflowInstanceId;
	private String eventType;
	private String eventDate;
	private String message;
	private String expiringDate;
}
