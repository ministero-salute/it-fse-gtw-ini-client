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
package it.finanze.sanita.fse2.ms.iniclient.logging;

import it.finanze.sanita.fse2.ms.iniclient.client.IConfigClient;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.LogDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ILogEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.AppConstants.LOG_TYPE_CONTROL;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.AppConstants.LOG_TYPE_KPI;

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
	
	@Autowired
	private IConfigSRV configSRV;
	
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
	public void trace(String log_type, String workflowInstanceId, String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation, String issuer, 
			String documentType, String subjectRole, String subjectFiscalCode, String locality) {
		
		if((LOG_TYPE_CONTROL.equals(log_type) && configSRV.isControlLogPersistenceEnable()) ||
				(LOG_TYPE_KPI.equals(log_type) && configSRV.isKpiLogPersistenceEnable())) {
			LogDTO logDTO = LogDTO.builder().
					op_locality(locality).
					message(message).
					operation(operation.getCode()).
					op_document_type(documentType).
					op_result(result.getCode()).
					op_role(subjectRole).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					log_type(log_type).
					workflow_instance_id(workflowInstanceId).
					build();

			if(!configSRV.isCfOnIssuerNotAllowed()) {
				logDTO.setOp_issuer(issuer);
			}
			
			if(!configSRV.isSubjectNotAllowed()) {
				logDTO.setOp_fiscal_code(subjectFiscalCode);
			}
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.trace(logMessage);

			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.trace(logMessage);
			}
		}
		
	} 

	
	public void debug(String log_type, String workflowInstanceId,String message,  ILogEnum operation, ResultLogEnum result, Date startDateOperation, 
			String issuer, String documentType, String subjectRole, String subjectFiscalCode, String locality) {
		
		if((LOG_TYPE_CONTROL.equals(log_type) && configSRV.isControlLogPersistenceEnable()) ||
				(LOG_TYPE_KPI.equals(log_type) && configSRV.isKpiLogPersistenceEnable())) {
			LogDTO logDTO = LogDTO.builder().
					op_locality(locality).
					message(message).
					operation(operation.getCode()).
					op_document_type(documentType).
					op_result(result.getCode()).
					op_role(subjectRole).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					build();
			
			if(!configSRV.isCfOnIssuerNotAllowed()) {
				logDTO.setOp_issuer(issuer);
			}
			
			if(!configSRV.isSubjectNotAllowed()) {
				logDTO.setOp_fiscal_code(subjectFiscalCode);
			}
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.debug(logMessage);

			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.debug(logMessage);
			}	
		}
		
	} 
	
	
	public void info(String log_type, String workflowInstanceId,String message, ILogEnum operation, Date startDateOperation, String documentType, String subjectFiscalCode, JWTPayloadDTO payloadDTO,
			String administrativeRequest, String authorInstitution) {
		
		if((LOG_TYPE_CONTROL.equals(log_type) && configSRV.isControlLogPersistenceEnable()) ||
				(LOG_TYPE_KPI.equals(log_type) && configSRV.isKpiLogPersistenceEnable())) {
			LogDTO logDTO = LogDTO.builder().
					op_locality(payloadDTO.getLocality()).
					message(message).
					operation(operation.getCode()).
					op_document_type(documentType).
					op_result(ResultLogEnum.OK.getCode()).
					op_role(payloadDTO.getSubject_role()).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					op_application_id(payloadDTO.getSubject_application_id()).
					op_application_vendor(payloadDTO.getSubject_application_vendor()).
					op_application_version(payloadDTO.getSubject_application_version()).
					administrative_request(administrativeRequest).
					author_institution(authorInstitution).
					log_type(log_type).
					workflow_instance_id(workflowInstanceId).
					build();
			
			if(!configSRV.isCfOnIssuerNotAllowed()) {
				logDTO.setOp_issuer(payloadDTO.getIss());
			}
			
			if(!configSRV.isSubjectNotAllowed()) {
				logDTO.setOp_fiscal_code(subjectFiscalCode);
			}
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.info(logMessage);
			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.info(logMessage);
			}
		}
		
	} 

	public void warn(String log_type, String workflowInstanceId,String message, ILogEnum operation, ResultLogEnum result, Date startDateOperation, String issuer, 
			String documentType, String subjectRole, String subjectFiscalCode, String locality,
			String applicationId, String applicationVendor, String applicationVersion) {
		
		if((LOG_TYPE_CONTROL.equals(log_type) && configSRV.isControlLogPersistenceEnable()) ||
				(LOG_TYPE_KPI.equals(log_type) && configSRV.isKpiLogPersistenceEnable())) {
			LogDTO logDTO = LogDTO.builder().
					op_locality(locality).
					message(message).
					operation(operation.getCode()).
					op_document_type(documentType).
					op_role(subjectRole).
					op_result(result.getCode()).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					op_application_id(applicationId).
					op_application_vendor(applicationVendor).
					op_application_version(applicationVersion).
					log_type(log_type).
					workflow_instance_id(workflowInstanceId).
					build();
			
			if(!configSRV.isCfOnIssuerNotAllowed()) {
				logDTO.setOp_issuer(issuer);
			}
			
			if(!configSRV.isSubjectNotAllowed()) {
				logDTO.setOp_fiscal_code(subjectFiscalCode);
			}
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.warn(logMessage);
	 
			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.warn(logMessage);
			}
		}
		
	} 

	public void error(String log_type, String workflowInstanceId,String message, ILogEnum operation, Date startDateOperation, ILogEnum error, 
			String documentType, String subjectFiscalCode, JWTPayloadDTO jwtPayloadDTO, String administrativeRequest, String authorInstitution) {
		if((LOG_TYPE_CONTROL.equals(log_type) && configSRV.isControlLogPersistenceEnable()) ||
				(LOG_TYPE_KPI.equals(log_type) && configSRV.isKpiLogPersistenceEnable())) {
			LogDTO logDTO = LogDTO.builder().
					op_locality(jwtPayloadDTO.getLocality()).
					message(message).
					operation(operation.getCode()).
					op_document_type(documentType).
					op_role(jwtPayloadDTO.getSubject_role()).
					op_result(ResultLogEnum.KO.getCode()).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					op_error(error.getCode()).
					op_error_description(error.getDescription()).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					op_application_id(jwtPayloadDTO.getSubject_application_id()).
					op_application_vendor(jwtPayloadDTO.getSubject_application_vendor()).
					op_application_version(jwtPayloadDTO.getSubject_application_version()).
					log_type(log_type).
					workflow_instance_id(workflowInstanceId).
					administrative_request(administrativeRequest).
					author_institution(authorInstitution).
					build();

			if(!configSRV.isCfOnIssuerNotAllowed()) {
				logDTO.setOp_issuer(jwtPayloadDTO.getIss());
			}
			
			if(!configSRV.isSubjectNotAllowed()) {
				logDTO.setOp_fiscal_code(subjectFiscalCode);
			}
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.error(logMessage);

			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.error(logMessage);
			}
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
