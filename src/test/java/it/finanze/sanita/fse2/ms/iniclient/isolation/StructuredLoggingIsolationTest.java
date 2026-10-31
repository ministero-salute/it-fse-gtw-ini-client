/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.AppConstants.*;
import java.util.*;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import it.finanze.sanita.fse2.ms.iniclient.client.IConfigClient;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.*;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import org.bson.Document;

@Tag("ISOLATION") @ActiveProfiles("ISOLATION")
@TestPropertySource(properties={"log.kafka-log.enable=true","spring.application.name=ini-test"})
@SpringJUnitConfig(LoggerHelper.class)
class StructuredLoggingIsolationTest {
    @Autowired LoggerHelper helper;
    @MockitoBean IConfigClient client;
    @MockitoBean IConfigSRV config;
    Logger logger;
    Level old;
    ListAppender<ILoggingEvent> capture;
    @BeforeEach void setup(){
        logger=(Logger)LoggerFactory.getLogger("kafka-logger");old=logger.getLevel();logger.setLevel(Level.TRACE);
        capture=new ListAppender<>();capture.start();logger.addAppender(capture);
        when(client.getGatewayName()).thenReturn("gateway");
    }
    @AfterEach void cleanup(){logger.detachAppender(capture);capture.stop();logger.setLevel(old);}
    @ParameterizedTest @ValueSource(booleans={false,true})
    void allLevelsEmitStructuredFieldsAndHonorPrivacy(boolean redact){
        when(config.isControlLogPersistenceEnable()).thenReturn(true);when(config.isKpiLogPersistenceEnable()).thenReturn(true);
        when(config.isCfOnIssuerNotAllowed()).thenReturn(redact);when(config.isSubjectNotAllowed()).thenReturn(redact);
        var p=new JWTPayloadDTO();p.setIss("issuer");p.setLocality("hospital");p.setSubject_role("doctor");p.setSubject_application_id("app");
        var op=OperationLogEnum.values()[0];var result=ResultLogEnum.values()[0];var date=new Date();
        helper.trace(LOG_TYPE_CONTROL,"workflow","trace",op,result,date,"issuer","report","doctor","fiscal","hospital");
        helper.debug(LOG_TYPE_KPI,"workflow","debug",op,result,date,"issuer","report","doctor","fiscal","hospital");
        helper.info(LOG_TYPE_CONTROL,"workflow","info",op,date,"report","fiscal",p,List.of("request"),"institution");
        helper.info(LOG_TYPE_KPI,"workflow","short-info",op,date,"report","fiscal",p);
        helper.warn(LOG_TYPE_CONTROL,"workflow","warn",op,result,date,"issuer","report","doctor","fiscal","hospital","app","vendor","version");
        helper.error(LOG_TYPE_CONTROL,"workflow","error",op,date,op,"report","fiscal",p,List.of("request"),"institution");
        helper.error(LOG_TYPE_KPI,"workflow","short-error",op,date,op,"report","fiscal",p);
        assertEquals(7,capture.list.size());
        for(var event:capture.list){
            var data=Document.parse(event.getFormattedMessage());
            assertEquals("gateway",data.getString("gateway_name"));assertEquals("ini-test",data.getString("microservice_name"));
            assertEquals("hospital",data.getString("op_locality"));assertEquals(op.getCode(),data.getString("operation"));
            assertEquals(redact?null:"issuer",data.getString("op_issuer"));assertEquals(redact?null:"fiscal",data.getString("op_fiscal_code"));
            assertNotNull(data.getString("op_timestamp_start"));assertNotNull(data.getString("op_timestamp_end"));
        }
        var info=Document.parse(capture.list.get(2).getFormattedMessage());
        assertEquals(List.of("request"),info.getList("administrative_request",String.class));assertEquals("institution",info.getString("author_institution"));
        verify(client,atMostOnce()).getGatewayName();
    }
    @Test void disabledPersistenceDoesNotLookupGatewayOrEmit(){
        helper.info(LOG_TYPE_CONTROL,"workflow","disabled",null,null,null,null,null);
        helper.error(LOG_TYPE_KPI,"workflow","disabled",null,null,null,null,null,null);
        helper.trace("unknown",null,null,null,null,null,null,null,null,null,null);
        assertTrue(capture.list.isEmpty());verifyNoInteractions(client);
    }
}
