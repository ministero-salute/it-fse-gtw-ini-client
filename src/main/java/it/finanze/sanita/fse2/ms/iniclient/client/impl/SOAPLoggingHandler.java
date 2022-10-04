package it.finanze.sanita.fse2.ms.iniclient.client.impl;

import lombok.extern.slf4j.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Set;

/*
 * This simple SOAPHandler will output the contents of incoming
 * and outgoing messages.
 */
@Slf4j
public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {


	public Set<QName> getHeaders() { 
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		try {
			logToSystemOut(smc); 
		} catch (Exception ex) {
			log.error("Error while perform handle message : " + ex.getMessage());
		}
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		logToSystemOut(smc);
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {
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

		String header = "";

		if (Boolean.TRUE.equals(outboundProperty)) {
			header = "Outbound message:";
		} else {
			header = "Inbound message:";
		}

		try {
			SOAPMessage message = smc.getMessage();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();  
			message.writeTo(bout);  
			String msg = bout.toString("UTF-8");  
			log.debug(header + "\n" + msg);
		} catch (Exception e) {
			log.error("Exception in handler: " + e);
		}
	}
}