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
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.RESPONSE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.WII;

import java.io.StringWriter;
import java.util.Date;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.handler.MessageContext;

import com.sun.xml.ws.api.handler.MessageHandler;
import com.sun.xml.ws.api.handler.MessageHandlerContext;
import com.sun.xml.ws.api.message.Message;

import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import it.finanze.sanita.fse2.ms.iniclient.service.IAuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import lombok.extern.slf4j.Slf4j;

/**
 * Logs (and audits) the inbound SOAP Fault returned by INI.
 * 
 * Unlike {@link SOAPLoggingHandler}, it works on a copy of the JAX-WS {@link Message} and serializes it
 * directly to XML, without converting it to SAAJ: the SAAJ conversion fails on some SOAP 1.2 Fault
 * (SAAJ0416) and leaves the original message unreadable, hiding the real INI fault.
 */
@Slf4j
public class SOAPFaultLoggingHandler implements MessageHandler<MessageHandlerContext> {

	private final IAuditIniSrv auditIniSrv;
	private final IConfigSRV configSRV;

	public SOAPFaultLoggingHandler(IAuditIniSrv inAuditIniSrv, IConfigSRV inConfigSrv) {
		this.auditIniSrv = inAuditIniSrv;
		this.configSRV = inConfigSrv;
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(MessageHandlerContext mhc) {
		return true;
	}

	@Override
	public boolean handleFault(MessageHandlerContext mhc) {
		if (configSRV.isAuditIniEnable()) {
			try {
				String msg = serialize(mhc.getMessage().copy());
				log.info("Inbound fault message:\n{}", msg);
				String workflowInstanceId = (String) mhc.get(WII);
				EventType eventType = (EventType) mhc.get(EVENT_TYPE);
				Date eventDate = (Date) mhc.get(EVENT_DATE);
				auditIniSrv.save(workflowInstanceId, eventType, eventDate, RESPONSE, msg);
			} catch (Exception e) {
				log.warn("Unable to log/audit SOAP fault, original fault will be propagated", e);
			}
		}
		return true;
	}

	@Override
	public void close(MessageContext messageContext) {
		// nothing to release
	}

	private static String serialize(Message message) throws Exception {
		StringWriter out = new StringWriter();
		XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(out);
		message.writeTo(writer);
		writer.flush();
		writer.close();
		return out.toString();
	}
}
