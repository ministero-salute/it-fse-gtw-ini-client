/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.client.IConfigClient;
import it.finanze.sanita.fse2.ms.iniclient.dto.LogDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ILogEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

/** 
 * 
 */ 
@Service
@Slf4j
public class LoggerHelper {
    
	Logger kafkaLog = LoggerFactory.getLogger("kafka-logger"); 
	
	@Value("${log.kafka-log.enable}")
	private boolean kafkaLogEnable;

	@Autowired
	private IConfigClient configClient;
	
	private String gatewayName;
	
	@Value("${spring.application.name}")
	private String msName;
	
	/* 
	 * Specify here the format for the dates 
	 */
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS"); 
	
	/* 
	 * Implements structured logs, at all logging levels
	 */
	public void trace(String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation, String issuer, 
		String documentType, String subjectRole, String subjectFiscalCode, String locality) {
		
		final String gatewayName = getGatewayName();
		
		LogDTO logDTO = LogDTO.builder().
				op_issuer(issuer).
				op_locality(locality).
				message(message).
				operation(operation.getCode()).
				op_document_type(documentType).
				op_result(result.getCode()).
				op_role(subjectRole).
				op_fiscal_code(subjectFiscalCode).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				gateway_name(gatewayName).
				microservice_name(msName).
				build();

		final String logMessage = StringUtility.toJSON(logDTO);
		log.trace(logMessage);

		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.trace(logMessage);
		}
	} 
	
	public void debug(String message,  ILogEnum operation, ResultLogEnum result, Date startDateOperation, 
			String issuer, String documentType, String subjectRole, String subjectFiscalCode, String locality) {
		
		final String gatewayName = getGatewayName();
		LogDTO logDTO = LogDTO.builder().
				op_issuer(issuer).
				op_locality(locality).
				message(message).
				operation(operation.getCode()).
				op_document_type(documentType).
				op_result(result.getCode()).
				op_role(subjectRole).
				op_fiscal_code(subjectFiscalCode).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				gateway_name(gatewayName).
				microservice_name(msName).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.debug(logMessage);

		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.debug(logMessage);
		}
	} 
	 
	public void info(String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation, String issuer, 
		String documentType, String subjectRole, String subjectFiscalCode, String locality,
		String applicationId, String applicationVendor, String applicationVersion) {
		
		final String gatewayName = getGatewayName();
		
		LogDTO logDTO = LogDTO.builder().
				op_issuer(issuer).
				op_locality(locality).
				message(message).
				operation(operation.getCode()).
				op_document_type(documentType).
				op_result(result.getCode()).
				op_role(subjectRole).
				op_fiscal_code(subjectFiscalCode).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				gateway_name(gatewayName).
				microservice_name(msName).
				op_application_id(applicationId).
				op_application_vendor(applicationVendor).
				op_application_version(applicationVersion).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.info(logMessage);
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.info(logMessage);
		}
	} 
	
	public void warn(String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation, String issuer, 
		String documentType, String subjectRole, String subjectFiscalCode, String locality,
		String applicationId, String applicationVendor, String applicationVersion) {
		
		final String gatewayName = getGatewayName();
		
		LogDTO logDTO = LogDTO.builder().
				op_issuer(issuer).
				op_locality(locality).
				message(message).
				operation(operation.getCode()).
				op_document_type(documentType).
				op_role(subjectRole).
				op_fiscal_code(subjectFiscalCode).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				gateway_name(gatewayName).
				microservice_name(msName).
				op_application_id(applicationId).
				op_application_vendor(applicationVendor).
				op_application_version(applicationVersion).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.warn(logMessage);
 
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.warn(logMessage);
		}
 
	} 
	
	public void error(String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation, ILogEnum error, 
		String issuer, String documentType, String subjectRole, String subjectFiscalCode, String locality,
		String applicationId, String applicationVendor, String applicationVersion) {
		
		LogDTO logDTO = LogDTO.builder().
				op_issuer(issuer).
				op_locality(locality).
				message(message).
				operation(operation.getCode()).
				op_document_type(documentType).
				op_role(subjectRole).
				op_fiscal_code(subjectFiscalCode).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				op_error(error.getCode()).
				op_error_description(error.getDescription()).
				gateway_name(getGatewayName()).
				microservice_name(msName).
				op_application_id(applicationId).
				op_application_vendor(applicationVendor).
				op_application_version(applicationVersion).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.error(logMessage);

		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.error(logMessage);
		}
		
	}

	/**
	 * Returns the gateway name.
	 * 
	 * @return The GatewayName of the ecosystem.
	 */
	private String getGatewayName() {
		if (gatewayName == null) {
			gatewayName = configClient.getGatewayName();
		}
		return gatewayName;
	}
    
}
