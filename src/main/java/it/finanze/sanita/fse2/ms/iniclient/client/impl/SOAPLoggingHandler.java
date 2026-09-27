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
package it.finanze.sanita.fse2.ms.iniclient.client.impl;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.EVENT_DATE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.EVENT_TYPE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.REQUEST;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.RESPONSE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.WII;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.IAuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import lombok.extern.slf4j.Slf4j;

/*
 * This simple SOAPHandler will output the contents of incoming
 * and outgoing messages.
 */
@Slf4j
public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {


	private IAuditIniSrv auditIniSrv;
	private IConfigSRV configSRV;

	public SOAPLoggingHandler(IAuditIniSrv inAuditIniSrv, IConfigSRV inConfigSrv){
		if(auditIniSrv==null) {
			auditIniSrv = inAuditIniSrv; 
		}
		
		if(configSRV==null) {
			configSRV = inConfigSrv;			
		}
	}

	public Set<QName> getHeaders() { 
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		if(configSRV.isAuditIniEnable()){
			logToSystemOut(smc);
		}
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		if(configSRV.isAuditIniEnable()){
			logToSystemOut(smc);	
		}
		return true;
	}

	public void close(MessageContext messageContext) {
		messageContext.remove(WII);
		messageContext.remove(EVENT_TYPE);
		messageContext.remove(EVENT_DATE);
	}

	/*
	 * Check the MESSAGE_OUTBOUND_PROPERTY in the context
	 * to see if this is an outgoing or incoming message.
	 * Write a brief message to the print stream and
	 * output the message. The writeTo() method can throw
	 * SOAPException or IOException
	 */
	private void logToSystemOut(SOAPMessageContext smc) {
		Boolean outboundProperty = (Boolean)smc.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		String reqOrRes = "";
		String header = "";
		if (Boolean.TRUE.equals(outboundProperty)) {
			header = "Outbound message:";
			reqOrRes = REQUEST;
		} else {
			header = "Inbound message:";
			reqOrRes = RESPONSE;
		}

		try {
			SOAPMessage message = smc.getMessage();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();  
			message.writeTo(bout);
			String msg = bout.toString("UTF-8");  
			log.info(header + "\n" + msg);
			String workflowInstanceId = (String)smc.get(WII);
			EventType eventType = (EventType) smc.get(EVENT_TYPE);
			Date eventDate = (Date) smc.get(EVENT_DATE);
			auditIniSrv.save(workflowInstanceId, eventType, eventDate, reqOrRes, msg);
		} catch (Exception e) {
			log.error("Exception in handler: ",e);
			throw new BusinessException(e);
		}
	}
}