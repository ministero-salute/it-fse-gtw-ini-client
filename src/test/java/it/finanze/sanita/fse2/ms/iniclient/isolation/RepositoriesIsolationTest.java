/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY.*;
import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.mongodb.client.result.DeleteResult;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.*;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.*;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig({IssuerRepo.class, IniInvocationRepo.class})
class RepositoriesIsolationTest {
    @Autowired private IssuerRepo issuers;
    @Autowired private IniInvocationRepo invocations;
    @MockitoBean private MongoTemplate mongo;

    @ParameterizedTest @ValueSource(strings = {ISSUER, PAZIENTE_CF, NOME_DOCUMENT_REPOSITORY, ETICHETTA_REGIONE})
    void queriesTheCorrectIssuerField(String field) {
        IssuerETY entity = new IssuerETY();
        when(mongo.findOne(any(Query.class), eq(IssuerETY.class))).thenReturn(entity);
        IssuerETY actual = switch (field) {
            case ISSUER -> issuers.findByName("value");
            case PAZIENTE_CF -> issuers.findByFiscalCode("value");
            case NOME_DOCUMENT_REPOSITORY -> issuers.findByNomeDocumentRepository("value");
            default -> issuers.findRegioneMiddleware("value");
        };
        assertSame(entity, actual);
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).findOne(query.capture(), eq(IssuerETY.class));
        Document expected = new Document(field, "value");
        if (field.equals(ETICHETTA_REGIONE)) expected.append(MIDDLEWARE, true);
        assertEquals(expected, query.getValue().getQueryObject());
    }

    @Test void insertAndUpdateReturnPersistedIdentity() {
        IssuerETY entity = new IssuerETY();
        entity.setId("id");
        when(mongo.insert(entity)).thenReturn(entity);
        when(mongo.save(entity)).thenReturn(entity);
        assertEquals("id", issuers.createIssuer(entity));
        assertEquals("id", issuers.updateIssuer(entity));
    }

    @ParameterizedTest @ValueSource(longs = {0, 2})
    void deletionUsesIssuerNameAndReturnsDeletedCount(long count) {
        when(mongo.remove(any(Query.class), eq(IssuerETY.class))).thenReturn(DeleteResult.acknowledged(count));
        assertEquals((int) count, issuers.removeByName("issuer"));
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).remove(query.capture(), eq(IssuerETY.class));
        assertEquals(new Document(ISSUER, "issuer"), query.getValue().getQueryObject());
    }

    @Test void crashProgramSelectsOnlyNonMockedIssuersWithRegionAndPatient() {
        List<IssuerETY> entities = List.of(new IssuerETY());
        when(mongo.find(any(Query.class), eq(IssuerETY.class))).thenReturn(entities);
        assertSame(entities, issuers.findIssuersCrashProgrm());
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).find(query.capture(), eq(IssuerETY.class));
        assertEquals(new Document(MOCK, false).append(ETICHETTA_REGIONE, new Document("$ne", null))
                .append(PAZIENTE_CF, new Document("$ne", null)), query.getValue().getQueryObject());
    }

    @ParameterizedTest @ValueSource(strings = {"name", "region", "insert", "delete"})
    void wrapsDatabaseFailuresWithOriginalCause(String operation) {
        RuntimeException failure = new RuntimeException("database offline");
        BusinessException actual;
        if (operation.equals("insert")) {
            when(mongo.insert(any(IssuerETY.class))).thenThrow(failure);
            actual = assertThrows(BusinessException.class, () -> issuers.createIssuer(new IssuerETY()));
        } else if (operation.equals("delete")) {
            when(mongo.remove(any(Query.class), eq(IssuerETY.class))).thenThrow(failure);
            actual = assertThrows(BusinessException.class, () -> issuers.removeByName("issuer"));
        } else {
            when(mongo.findOne(any(Query.class), eq(IssuerETY.class))).thenThrow(failure);
            actual = assertThrows(BusinessException.class, () -> {
                if (operation.equals("name")) issuers.findByName("issuer");
                else issuers.findRegioneMiddleware("region");
            });
        }
        assertSame(failure, actual.getCause());
    }

    @Test void invocationLookupUsesWorkflowIdAndPreservesMissingResult() {
        IniEdsInvocationETY entity = new IniEdsInvocationETY();
        when(mongo.findOne(any(Query.class), eq(IniEdsInvocationETY.class))).thenReturn(entity).thenReturn(null);
        assertSame(entity, invocations.findByWorkflowInstanceId("workflow"));
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).findOne(query.capture(), eq(IniEdsInvocationETY.class));
        assertEquals(new Document("workflow_instance_id", "workflow"), query.getValue().getQueryObject());
        assertNull(invocations.findByWorkflowInstanceId("missing"));
    }
}
