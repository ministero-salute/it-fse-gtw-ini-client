package it.finanze.sanita.fse2.ms.iniclient;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.EVENT_DATE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.EVENT_TYPE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.REQUEST;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.RESPONSE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.WII;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.SOAPLoggingHandler;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.IAuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;

/**
 * Pure unit tests for {@link SOAPLoggingHandler}.
 */
class SOAPLoggingHandlerTest {

	private static final String BODY = "<soap:Envelope/>";

	private IAuditIniSrv audit;
	private IConfigSRV config;
	private SOAPMessageContext smc;
	private SOAPLoggingHandler handler;
	private Date eventDate;

	@BeforeEach
	void setUp() throws Exception {
		audit = mock(IAuditIniSrv.class);
		config = mock(IConfigSRV.class);
		smc = mock(SOAPMessageContext.class);
		handler = new SOAPLoggingHandler(audit, config);
		eventDate = new Date();

		SOAPMessage message = mock(SOAPMessage.class);
		doAnswer(inv -> {
			OutputStream os = inv.getArgument(0);
			os.write(BODY.getBytes("UTF-8"));
			return null;
		}).when(message).writeTo(any(OutputStream.class));
		when(smc.getMessage()).thenReturn(message);
		when(smc.get(WII)).thenReturn("wii-1");
		when(smc.get(EVENT_TYPE)).thenReturn(EventType.INI_CREATE_SOAP);
		when(smc.get(EVENT_DATE)).thenReturn(eventDate);
	}

	@Test
	void getHeadersIsNull() {
		assertNull(handler.getHeaders());
	}

	@Test
	void handleMessageOutboundSavesRequest() {
		when(config.isAuditIniEnable()).thenReturn(true);
		when(smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(Boolean.TRUE);

		assertTrue(handler.handleMessage(smc));

		verify(audit).save("wii-1", EventType.INI_CREATE_SOAP, eventDate, REQUEST, BODY);
	}

	@Test
	void handleMessageInboundSavesResponse() {
		when(config.isAuditIniEnable()).thenReturn(true);
		when(smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(Boolean.FALSE);

		assertTrue(handler.handleMessage(smc));

		verify(audit).save("wii-1", EventType.INI_CREATE_SOAP, eventDate, RESPONSE, BODY);
	}

	@Test
	void missingOutboundPropertyIsTreatedAsResponse() {
		when(config.isAuditIniEnable()).thenReturn(true);
		when(smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(null);

		handler.handleMessage(smc);

		verify(audit).save(eq("wii-1"), eq(EventType.INI_CREATE_SOAP), eq(eventDate), eq(RESPONSE), anyString());
	}

	@Test
	void handleFaultSavesWhenAuditEnabled() {
		when(config.isAuditIniEnable()).thenReturn(true);
		when(smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(Boolean.FALSE);

		assertTrue(handler.handleFault(smc));

		verify(audit).save("wii-1", EventType.INI_CREATE_SOAP, eventDate, RESPONSE, BODY);
	}

	@Test
	void nothingIsSavedWhenAuditDisabled() {
		when(config.isAuditIniEnable()).thenReturn(false);

		assertTrue(handler.handleMessage(smc));
		assertTrue(handler.handleFault(smc));

		verifyNoInteractions(audit);
		verify(smc, never()).getMessage();
	}

	@Test
	void writeFailureIsWrappedInBusinessException() throws Exception {
		when(config.isAuditIniEnable()).thenReturn(true);
		when(smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(Boolean.TRUE);
		SOAPMessage failing = mock(SOAPMessage.class);
		doThrow(new IOException("boom")).when(failing).writeTo(any(OutputStream.class));
		when(smc.getMessage()).thenReturn(failing);

		assertThrows(BusinessException.class, () -> handler.handleMessage(smc));
		verifyNoInteractions(audit);
	}

	@Test
	void auditFailureIsWrappedInBusinessException() {
		when(config.isAuditIniEnable()).thenReturn(true);
		when(smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(Boolean.TRUE);
		doThrow(new IllegalStateException("db down")).when(audit).save(any(), any(), any(), any(), any());

		assertThrows(BusinessException.class, () -> handler.handleMessage(smc));
	}

	@Test
	void closeRemovesAuditProperties() {
		handler.close(smc);

		verify(smc).remove(WII);
		verify(smc).remove(EVENT_TYPE);
		verify(smc).remove(EVENT_DATE);
	}
}
