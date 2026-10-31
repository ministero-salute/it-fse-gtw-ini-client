/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.*;
import java.util.Date;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.SOAPLoggingHandler;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.IAuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;

@Tag("ISOLATION")
class SoapAuditIsolationTest {
    @ParameterizedTest @CsvSource({"true,false", "false,false", "true,true", "false,true"})
    void serializesRealSoapAndAuditsItsDirectionIncludingFaults(boolean outbound, boolean fault) throws Exception {
        var audit = mock(IAuditIniSrv.class);
        var configuration = mock(IConfigSRV.class);
        when(configuration.isAuditIniEnable()).thenReturn(true);
        var context = mock(SOAPMessageContext.class);
        var eventDate = new Date();
        when(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(outbound);
        when(context.get(WII)).thenReturn("workflow");
        when(context.get(EVENT_TYPE)).thenReturn(EventType.INI_CREATE_SOAP);
        when(context.get(EVENT_DATE)).thenReturn(eventDate);
        var message = MessageFactory.newInstance().createMessage();
        message.getSOAPBody().addBodyElement(new javax.xml.namespace.QName("urn:test", "request")).addTextNode("payload");
        when(context.getMessage()).thenReturn(message);
        var handler = new SOAPLoggingHandler(audit, configuration);
        assertTrue(fault ? handler.handleFault(context) : handler.handleMessage(context));
        verify(audit).save(eq("workflow"), eq(EventType.INI_CREATE_SOAP), same(eventDate), eq(outbound ? REQUEST : RESPONSE),
                argThat(xml -> xml.contains("payload") && xml.contains("urn:test") && xml.contains("Envelope")));
    }

    @Test void disabledAuditNeverSerializesOrPersistsSoap() {
        var audit = mock(IAuditIniSrv.class);
        var configuration = mock(IConfigSRV.class);
        var context = mock(SOAPMessageContext.class);
        var handler = new SOAPLoggingHandler(audit, configuration);
        assertTrue(handler.handleMessage(context));
        assertTrue(handler.handleFault(context));
        verifyNoInteractions(audit, context);
        assertNull(handler.getHeaders());
    }

    @Test void serializationFailurePreservesCauseAndDoesNotWriteAudit() throws Exception {
        var audit = mock(IAuditIniSrv.class);
        var configuration = mock(IConfigSRV.class);
        when(configuration.isAuditIniEnable()).thenReturn(true);
        var context = mock(SOAPMessageContext.class);
        var message = mock(javax.xml.soap.SOAPMessage.class);
        var failure = new SOAPException("cannot serialize");
        when(context.getMessage()).thenReturn(message);
        doThrow(failure).when(message).writeTo(any());
        assertSame(failure, assertThrows(BusinessException.class,
                () -> new SOAPLoggingHandler(audit, configuration).handleMessage(context)).getCause());
        verifyNoInteractions(audit);
    }

    @Test void closeRemovesOnlyAuditContextKeys() {
        var context = mock(SOAPMessageContext.class);
        new SOAPLoggingHandler(null, null).close(context);
        verify(context).remove(WII);
        verify(context).remove(EVENT_TYPE);
        verify(context).remove(EVENT_DATE);
        verifyNoMoreInteractions(context);
    }
}
