package it.finanze.sanita.fse2.ms.iniclient;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.REQUEST;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditDto;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniAuditsDto;
import it.finanze.sanita.fse2.ms.iniclient.enums.EventType;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IAuditIniRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.AuditIniSrv;

/**
 * Pure unit tests (no Spring context, no Mongo) for {@link AuditIniSrv}.
 */
class AuditIniSrvTest {

	private static final String WII = "wii-1";

	private IAuditIniRepo repo;
	private IConfigSRV configSRV;
	private AuditIniSrv srv;

	@BeforeEach
	void setUp() {
		repo = mock(IAuditIniRepo.class);
		configSRV = mock(IConfigSRV.class);
		srv = new AuditIniSrv();
		ReflectionTestUtils.setField(srv, "auditRepo", repo);
		ReflectionTestUtils.setField(srv, "configSRV", configSRV);
	}

	@Test
	void saveRequestInsertsEntityWithExpiringDate() {
		when(configSRV.getExpirationDate()).thenReturn(10);
		Date eventDate = new Date();
		long before = System.currentTimeMillis();

		srv.save(WII, EventType.INI_CREATE_SOAP, eventDate, REQUEST, "<soap/>");

		ArgumentCaptor<AuditIniETY> captor = ArgumentCaptor.forClass(AuditIniETY.class);
		verify(repo).insert(captor.capture());
		AuditIniETY ety = captor.getValue();
		assertEquals(WII, ety.getWorkflowInstanceId());
		assertEquals(EventType.INI_CREATE_SOAP, ety.getEventType());
		assertEquals(eventDate, ety.getEventDate());
		assertEquals("<soap/>", ety.getSoapRequest());
		assertNull(ety.getSoapResponse());
		long tenDays = 10L * 24 * 3600 * 1000;
		assertTrue(ety.getExpiringDate().getTime() >= before + tenDays - 1000);
		verify(repo, never()).updateResponseByWiiAndEventType(any(), any(), any());
	}

	@Test
	void saveRequestIsCaseInsensitive() {
		when(configSRV.getExpirationDate()).thenReturn(1);
		srv.save(WII, EventType.INI_DELETE_SOAP, new Date(), "REQUEST", "x");
		verify(repo).insert(any(AuditIniETY.class));
	}

	@Test
	void saveResponseUpdatesExistingRecord() {
		srv.save(WII, EventType.INI_UPDATE_SOAP, new Date(), RESPONSE, "<resp/>");

		verify(repo).updateResponseByWiiAndEventType(WII, "INI_UPDATE_SOAP", "<resp/>");
		verify(repo, never()).insert(any());
	}

	@Test
	void saveUnknownDirectionDoesNothing() {
		srv.save(WII, EventType.INI_UPDATE_SOAP, new Date(), "other", "x");
		verifyNoInteractions(repo);
	}

	@Test
	void findByWiiMapsEntitiesToDto() {
		Date eventDate = new Date();
		Date expiring = new Date(eventDate.getTime() + 1000);
		AuditIniETY both = new AuditIniETY(WII, EventType.INI_CREATE_SOAP, eventDate, "REQ", "RES", expiring);
		AuditIniETY onlyRequest = new AuditIniETY(WII, EventType.INI_DELETE_SOAP, eventDate, "REQ2", null, expiring);
		AuditIniETY empty = new AuditIniETY(WII, EventType.INI_RIFERIMENTO_SOAP, eventDate, "", "", expiring);
		when(repo.findByWii(WII)).thenReturn(List.of(both, onlyRequest, empty));

		IniAuditsDto out = srv.findByWii(WII);

		assertNotNull(out);
		List<IniAuditDto> audits = out.getAudit();
		assertEquals(3, audits.size());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		assertEquals(WII, audits.get(0).getWorkflowInstanceId());
		assertEquals("INI_CREATE_SOAP", audits.get(0).getEventType());
		assertEquals(sdf.format(eventDate), audits.get(0).getEventDate());
		assertEquals(sdf.format(expiring), audits.get(0).getExpiringDate());
		assertEquals("SOAP_REQUEST:REQ SOAP_RESPONSE:RES", audits.get(0).getMessage());
		assertEquals("SOAP_REQUEST:REQ2", audits.get(1).getMessage());
		assertEquals("", audits.get(2).getMessage());
	}

	@Test
	void findByWiiEmpty() {
		when(repo.findByWii(WII)).thenReturn(List.of());
		assertTrue(srv.findByWii(WII).getAudit().isEmpty());
	}
}
