/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import it.finanze.sanita.fse2.ms.iniclient.client.IConfigClient;
import it.finanze.sanita.fse2.ms.iniclient.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.ConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.ProfileUtility;

@Tag("ISOLATION")
class ConfigurationIsolationTest {
    private ConfigSRV service;
    private IConfigClient client;
    private ProfileUtility profiles;

    @BeforeEach void setup() {
        service = new ConfigSRV();
        client = mock(IConfigClient.class);
        profiles = mock(ProfileUtility.class);
        ReflectionTestUtils.setField(service, "client", client);
        ReflectionTestUtils.setField(service, "profiles", profiles);
        ReflectionTestUtils.setField(service, "refreshRate", 900000L);
    }

    private ConfigItemDTO items(Map<String, String> properties) {
        var item = new ConfigItemDTO.ConfigDataItemDTO();
        item.setItems(properties);
        var response = new ConfigItemDTO();
        response.setConfigurationItems(List.of(item));
        return response;
    }

    private void initialize() {
        when(client.getConfigurationItems(any())).thenReturn(items(Map.of(
                PROPS_NAME_SUBJECT, "true", PROPS_NAME_ISSUER_CF, "true",
                PROPS_NAME_CONTROL_LOG_ENABLED, "true", PROPS_NAME_KPI_LOG_ENABLED, "true",
                PROPS_NAME_AUDIT_INI_ENABLED, "true", PROPS_NAME_EXP_DAYS, "7")));
        service.postConstruct();
    }

    @Test void testProfileSkipsRemoteInitialization() {
        when(profiles.isTestProfile()).thenReturn(true);
        service.postConstruct();
        verifyNoInteractions(client);
    }

    @Test void readsFreshPropertiesWithoutFurtherRemoteCalls() {
        initialize();
        clearInvocations(client);
        assertTrue(service.isSubjectNotAllowed());
        assertTrue(service.isCfOnIssuerNotAllowed());
        assertTrue(service.isControlLogPersistenceEnable());
        assertTrue(service.isKpiLogPersistenceEnable());
        assertTrue(service.isAuditIniEnable());
        assertEquals(7, service.getExpirationDate());
        assertEquals(900000L, service.getRefreshRate());
        verifyNoInteractions(client);
    }

    @ParameterizedTest @ValueSource(strings = {"subject", "issuer", "control", "kpi", "audit", "expiration"})
    void expiredPropertiesRefreshUsingPreviousValue(String property) {
        initialize();
        ReflectionTestUtils.setField(service, "refreshRate", 0L);
        String key = switch (property) {
            case "subject" -> PROPS_NAME_SUBJECT;
            case "issuer" -> PROPS_NAME_ISSUER_CF;
            case "control" -> PROPS_NAME_CONTROL_LOG_ENABLED;
            case "kpi" -> PROPS_NAME_KPI_LOG_ENABLED;
            case "audit" -> PROPS_NAME_AUDIT_INI_ENABLED;
            default -> PROPS_NAME_EXP_DAYS;
        };
        when(client.getProps(key, property.equals("expiration") ? "7" : "true", ConfigItemTypeEnum.INI_CLIENT))
                .thenReturn(property.equals("expiration") ? "14" : "false");
        switch (property) {
            case "subject" -> assertFalse(service.isSubjectNotAllowed());
            case "issuer" -> assertFalse(service.isCfOnIssuerNotAllowed());
            case "control" -> assertFalse(service.isControlLogPersistenceEnable());
            case "kpi" -> assertFalse(service.isKpiLogPersistenceEnable());
            case "audit" -> assertFalse(service.isAuditIniEnable());
            default -> assertEquals(14, service.getExpirationDate());
        }
        verify(client).getProps(key, property.equals("expiration") ? "7" : "true", ConfigItemTypeEnum.INI_CLIENT);
    }

    @Test void serviceSpecificConfigurationOverridesGenericDefaults() {
        when(client.getConfigurationItems(ConfigItemTypeEnum.GENERIC)).thenReturn(items(Map.of(
                PROPS_NAME_SUBJECT, "false", PROPS_NAME_ISSUER_CF, "false",
                PROPS_NAME_CONTROL_LOG_ENABLED, "false", PROPS_NAME_KPI_LOG_ENABLED, "false")));
        when(client.getConfigurationItems(ConfigItemTypeEnum.INI_CLIENT))
                .thenReturn(items(Map.of(PROPS_NAME_SUBJECT, "true")));
        service.postConstruct();
        assertTrue(service.isSubjectNotAllowed());
        assertFalse(service.isCfOnIssuerNotAllowed());
        var ordered = inOrder(client);
        ordered.verify(client).getConfigurationItems(ConfigItemTypeEnum.GENERIC);
        ordered.verify(client).getConfigurationItems(ConfigItemTypeEnum.INI_CLIENT);
    }

    @Test void missingRequiredConfigurationFailsFast() {
        var empty = new ConfigItemDTO();
        empty.setConfigurationItems(List.of());
        when(client.getConfigurationItems(any())).thenReturn(empty);
        assertTrue(assertThrows(IllegalStateException.class, service::postConstruct).getMessage().contains("Missing props"));
    }

    @Test void failedRefreshDoesNotOverwriteCachedValue() {
        initialize();
        ReflectionTestUtils.setField(service, "refreshRate", 0L);
        when(client.getProps(PROPS_NAME_SUBJECT, "true", ConfigItemTypeEnum.INI_CLIENT))
                .thenThrow(new IllegalStateException("config unavailable"));
        assertThrows(IllegalStateException.class, service::isSubjectNotAllowed);
        ReflectionTestUtils.setField(service, "refreshRate", Long.MAX_VALUE);
        assertTrue(service.isSubjectNotAllowed());
    }
}
