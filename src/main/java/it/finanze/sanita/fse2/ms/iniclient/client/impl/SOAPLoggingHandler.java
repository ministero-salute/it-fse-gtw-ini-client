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

import java.io.ByteArrayOutputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.IAuditIniSrv;
import lombok.extern.slf4j.Slf4j;

/*
 * This simple SOAPHandler will output the contents of incoming
 * and outgoing messages.
 */
@Slf4j
public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String REQUEST = "request";
	public static final String RESPONSE = "response";

	private IAuditIniSrv auditIniSrv;

	public SOAPLoggingHandler(IAuditIniSrv inAuditIniSrv/*, String workflowInstanceId, EventType eventType, Date eventDate*/){
		if(auditIniSrv==null) {
			auditIniSrv = inAuditIniSrv; 
		}
	}

	public Set<QName> getHeaders() { 
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		//TODO - Recuperare da qui il booleano che ti dice se abilitato o meno l'audit
		//Il booleano settarlo per ogni chiamata di iniClient
		logToSystemOut(smc); 
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		logToSystemOut(smc);
		return true;
	}

	public void close(MessageContext messageContext) {
		//TODO - Capire se qui va bene
		messageContext.remove("WII");
	}

	/*
	 * Check the MESSAGE_OUTBOUND_PROPERTY in the context
	 * to see if this is an outgoing or incoming message.
	 * Write a brief message to the print stream and
	 * output the message. The writeTo() method can throw
	 * SOAPException or IOException
	 */
	private void logToSystemOut(SOAPMessageContext smc) {
		Boolean outboundProperty = (Boolean)
				smc.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);

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
			//TODO: Analysis - when we arrive through handleFault method what happens?
			//TODO - Passare altri parametri
			String workflowInstanceId = (String)smc.get("WII");
			auditIniSrv.save(workflowInstanceId, null, null, reqOrRes, msg);
		} catch (Exception e) {
			log.error("Exception in handler: " + e);
			throw new BusinessException(e);
		}
	}
}