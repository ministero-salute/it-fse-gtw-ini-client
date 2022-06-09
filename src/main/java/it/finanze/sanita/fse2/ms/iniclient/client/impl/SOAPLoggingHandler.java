package it.finanze.sanita.fse2.ms.iniclient.client.impl;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/*
 * This simple SOAPHandler will output the contents of incoming
 * and outgoing messages.
 */
public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	// change this to redirect output if desired
	private static PrintStream out = System.out;

	public Set<QName> getHeaders() {
		final QName securityHeader = new QName(
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
				"Security", "wsse");

		final HashSet headers = new HashSet();
		headers.add(securityHeader);
		return headers;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		try {
			logToSystemOut(smc); 
		} catch (Exception e) {
			System.out.println(e);
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

		if (outboundProperty.booleanValue()) {
			out.println("\nOutbound message:");

		} else {
			out.println("\nInbound message:");
		}

		SOAPMessage message = smc.getMessage();
		try {
			message.writeTo(out);
			out.println("");   // just to add a newline
		} catch (Exception e) {
			out.println("Exception in handler: " + e);
		}
	}
}