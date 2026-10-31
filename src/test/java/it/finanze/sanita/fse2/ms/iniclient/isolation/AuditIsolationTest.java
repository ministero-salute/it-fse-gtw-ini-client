/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY.*;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.REQUEST;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.RESPONSE;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.AuditIniRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.AuditIniSrv;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig({AuditIniSrv.class, AuditIniRepo.class})
class AuditIsolationTest {
    @Autowired private AuditIniSrv service;
    @MockitoBean private MongoTemplate mongo;
    @MockitoBean private IConfigSRV configuration;
    private final EventType event = EventType.values()[0];

    @Test void requestIsPersistedWithWorkflowEventPayloadAndExpiry() {
        when(configuration.getExpirationDate()).thenReturn(7);
        Date eventDate = new Date();
        service.save("workflow", event, eventDate, REQUEST.toLowerCase(), "<request/>");
        ArgumentCaptor<AuditIniETY> entity = ArgumentCaptor.forClass(AuditIniETY.class);
        verify(mongo).insert(entity.capture());
        assertEquals("workflow", entity.getValue().getWorkflowInstanceId());
        assertSame(eventDate, entity.getValue().getEventDate());
        assertEquals(event, entity.getValue().getEventType());
        assertEquals("<request/>", entity.getValue().getSoapRequest());
        assertNull(entity.getValue().getSoapResponse());
        assertTrue(entity.getValue().getExpiringDate().after(new Date(System.currentTimeMillis() + 6L * 86400000)));
    }

    @Test void responseUpdatesOnlyMatchingWorkflowAndEvent() {
        service.save("workflow", event, new Date(), RESPONSE.toLowerCase(), "<response/>");
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> update = ArgumentCaptor.forClass(Update.class);
        verify(mongo).updateFirst(query.capture(), update.capture(), eq(AuditIniETY.class));
        assertEquals(new Document(WORKFLOW_INSTANCE_ID, "workflow").append(EVENT_TYPE, event.name()),
                query.getValue().getQueryObject());
        assertEquals(new Document("$set", new Document(SOAP_RESPONSE, "<response/>")), update.getValue().getUpdateObject());
        verifyNoInteractions(configuration);
    }

    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = "unknown")
    void unknownRecordTypesDoNotWrite(String type) {
        service.save("workflow", event, new Date(), type, "ignored");
        verifyNoInteractions(mongo, configuration);
    }

    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = "<soap/>")
    void lookupBuildsAuditMessagesAndUsesWorkflowQuery(String payload) {
        Date now = new Date();
        when(mongo.find(any(Query.class), eq(AuditIniETY.class)))
                .thenReturn(List.of(new AuditIniETY("workflow", event, now, payload, payload, now)));
        var response = service.findByWii("workflow");
        assertEquals(1, response.getAudit().size());
        var audit = response.getAudit().get(0);
        assertEquals("workflow", audit.getWorkflowInstanceId());
        assertEquals(event.name(), audit.getEventType());
        assertEquals(payload == null || payload.isEmpty() ? "" : "SOAP_REQUEST:<soap/> SOAP_RESPONSE:<soap/>", audit.getMessage());
        assertNotNull(audit.getEventDate());
        assertEquals(audit.getEventDate(), audit.getExpiringDate());
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).find(query.capture(), eq(AuditIniETY.class));
        assertEquals(new Document(WORKFLOW_INSTANCE_ID, "workflow"), query.getValue().getQueryObject());
    }

    @Test void absentAuditReturnsEmptyList() {
        assertTrue(service.findByWii("missing").getAudit().isEmpty());
    }

    @Test void databaseFailureIsNotReportedAsSuccessfulSave() {
        when(configuration.getExpirationDate()).thenReturn(7);
        RuntimeException failure = new RuntimeException("mongo unavailable");
        when(mongo.insert(any(AuditIniETY.class))).thenThrow(failure);
        assertSame(failure, assertThrows(RuntimeException.class,
                () -> service.save("workflow", event, new Date(), REQUEST, "<request/>")));
    }
}
