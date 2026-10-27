package it.finanze.sanita.fse2.ms.iniclient;

import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_AUDIT_INI_ENABLED;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_CONTROL_LOG_ENABLED;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_EXP_DAYS;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_ISSUER_CF;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_KPI_LOG_ENABLED;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_REMOVE_METADATA_ENABLE;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_SUBJECT;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum.INI_CLIENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import it.finanze.sanita.fse2.ms.iniclient.client.IConfigClient;
import it.finanze.sanita.fse2.ms.iniclient.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ConfigItemDTO.ConfigDataItemDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.ConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.ProfileUtility;

/**
 * Pure unit tests (no Spring context) for {@link ConfigSRV}.
 */
class ConfigSRVUnitTest {

	private IConfigClient client;
	private ProfileUtility profiles;
	private ConfigSRV srv;

	@BeforeEach
	void setUp() {
		client = mock(IConfigClient.class);
		profiles = mock(ProfileUtility.class);
		srv = new ConfigSRV();
		ReflectionTestUtils.setField(srv, "client", client);
		ReflectionTestUtils.setField(srv, "profiles", profiles);
		ReflectionTestUtils.setField(srv, "refreshRate", 900_000L);
	}

	private static ConfigItemDTO dto(Map<String, String> items) {
		ConfigDataItemDTO data = new ConfigDataItemDTO();
		data.setKey("k");
		data.setItems(items);
		ConfigItemDTO dto = new ConfigItemDTO();
		dto.setConfigurationItems(List.of(data));
		return dto;
	}

	private static Map<String, String> fullProps() {
		Map<String, String> m = new HashMap<>();
		m.put(PROPS_NAME_KPI_LOG_ENABLED, "true");
		m.put(PROPS_NAME_CONTROL_LOG_ENABLED, "false");
		m.put(PROPS_NAME_SUBJECT, "true");
		m.put(PROPS_NAME_ISSUER_CF, "false");
		m.put(PROPS_NAME_REMOVE_METADATA_ENABLE, "true");
		m.put(PROPS_NAME_AUDIT_INI_ENABLED, "true");
		m.put(PROPS_NAME_EXP_DAYS, "30");
		return m;
	}

	private void initWith(Map<String, String> props) {
		when(profiles.isTestProfile()).thenReturn(false);
		when(client.getConfigurationItems(any(ConfigItemTypeEnum.class))).thenReturn(dto(props));
		srv.postConstruct();
	}

	@Test
	void postConstructSkipsInitOnTestProfile() {
		when(profiles.isTestProfile()).thenReturn(true);
		srv.postConstruct();
		verify(client, never()).getConfigurationItems(any());
	}

	@Test
	void postConstructLoadsBothConfigTypes() {
		initWith(fullProps());
		verify(client).getConfigurationItems(ConfigItemTypeEnum.GENERIC);
		verify(client).getConfigurationItems(INI_CLIENT);
	}

	@Test
	void postConstructLoadsEmptyOptionsThenFailsIntegrity() {
		ConfigItemDTO empty = new ConfigItemDTO();
		empty.setConfigurationItems(List.of());
		when(profiles.isTestProfile()).thenReturn(false);
		when(client.getConfigurationItems(any(ConfigItemTypeEnum.class))).thenReturn(empty);
		assertThrows(IllegalStateException.class, srv::postConstruct);
	}

	@Test
	void postConstructFailsWhenMandatoryPropMissing() {
		Map<String, String> props = fullProps();
		props.remove(PROPS_NAME_SUBJECT);
		when(profiles.isTestProfile()).thenReturn(false);
		when(client.getConfigurationItems(any(ConfigItemTypeEnum.class))).thenReturn(dto(props));
		IllegalStateException ex = assertThrows(IllegalStateException.class, srv::postConstruct);
		assertTrue(ex.getMessage().contains(PROPS_NAME_SUBJECT));
	}

	@Test
	void gettersReturnCachedValuesWithoutRefresh() {
		initWith(fullProps());

		assertTrue(srv.isKpiLogPersistenceEnable());
		assertFalse(srv.isControlLogPersistenceEnable());
		assertTrue(srv.isSubjectNotAllowed());
		assertFalse(srv.isCfOnIssuerNotAllowed());
		assertTrue(srv.isRemoveMetadataEnable());
		assertTrue(srv.isAuditIniEnable());
		assertEquals(30, srv.getExpirationDate());
		assertEquals(900_000L, srv.getRefreshRate());

		verify(client, never()).getProps(any(), any(), any());
	}

	@Test
	void expiredEntryIsRefreshedFromClient() {
		initWith(fullProps());
		ReflectionTestUtils.setField(srv, "refreshRate", 0L);
		when(client.getProps(eq(PROPS_NAME_AUDIT_INI_ENABLED), eq("true"), eq(INI_CLIENT))).thenReturn("false");

		assertFalse(srv.isAuditIniEnable());
		verify(client).getProps(PROPS_NAME_AUDIT_INI_ENABLED, "true", INI_CLIENT);
	}

	@Test
	void everyGetterRefreshesItsOwnPropertyWhenExpired() {
		initWith(fullProps());
		ReflectionTestUtils.setField(srv, "refreshRate", 0L);
		when(client.getProps(any(), any(), eq(INI_CLIENT))).thenReturn("false");

		assertFalse(srv.isKpiLogPersistenceEnable());
		assertFalse(srv.isControlLogPersistenceEnable());
		assertFalse(srv.isSubjectNotAllowed());
		assertFalse(srv.isCfOnIssuerNotAllowed());
		assertFalse(srv.isRemoveMetadataEnable());

		verify(client).getProps(eq(PROPS_NAME_KPI_LOG_ENABLED), any(), eq(INI_CLIENT));
		verify(client).getProps(eq(PROPS_NAME_CONTROL_LOG_ENABLED), any(), eq(INI_CLIENT));
		verify(client).getProps(eq(PROPS_NAME_SUBJECT), any(), eq(INI_CLIENT));
		verify(client).getProps(eq(PROPS_NAME_ISSUER_CF), any(), eq(INI_CLIENT));
		verify(client).getProps(eq(PROPS_NAME_REMOVE_METADATA_ENABLE), any(), eq(INI_CLIENT));
	}

	@Test
	void expirationDaysRefreshed() {
		initWith(fullProps());
		ReflectionTestUtils.setField(srv, "refreshRate", 0L);
		when(client.getProps(PROPS_NAME_EXP_DAYS, "30", INI_CLIENT)).thenReturn("45");

		assertEquals(45, srv.getExpirationDate());
	}

	@Test
	void refreshOnlyHappensAfterExpiration() {
		initWith(fullProps());
		srv.isAuditIniEnable();
		srv.isAuditIniEnable();
		verify(client, times(0)).getProps(any(), any(), any());
	}
}
