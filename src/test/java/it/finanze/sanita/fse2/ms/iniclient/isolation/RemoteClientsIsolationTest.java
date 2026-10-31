/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.*;
import it.finanze.sanita.fse2.ms.iniclient.client.routes.ConfigClientRoutes;
import it.finanze.sanita.fse2.ms.iniclient.config.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuersDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.ProfileUtility;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@TestPropertySource(properties = "ms.url.gtw-config=http://config.invalid")
@SpringJUnitConfig({ConfigClient.class, CrashProgramClient.class, ConfigClientRoutes.class, RemoteClientsIsolationTest.Wiring.class})
class RemoteClientsIsolationTest {
    @org.springframework.context.annotation.Profile("ISOLATION")
    @Configuration static class Wiring {
        @Bean RestTemplate restTemplate() { return new RestTemplate(); }
    }
    @Autowired private ConfigClient config;
    @Autowired private CrashProgramClient crash;
    @Autowired private ConfigClientRoutes routes;
    @Autowired private RestTemplate http;
    @MockitoBean private ProfileUtility profiles;
    @MockitoBean private MicroservicesCFG microservices;
    private MockRestServiceServer server;

    @BeforeEach void setup() { server = MockRestServiceServer.bindTo(http).build(); }

    @Test void gatewayNameUsesRealHttpDeserialization() {
        server.expect(requestTo(routes.whois())).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"gatewayName\":\"gateway-test\"}", MediaType.APPLICATION_JSON));
        assertEquals("gateway-test", config.getGatewayName());
        server.verify();
    }

    @ParameterizedTest @ValueSource(booleans = {true, false})
    void unreachableConfigIsMockedOnlyInDevelopmentOrTest(boolean dev) {
        when(profiles.isDevOrDockerProfile()).thenReturn(dev);
        when(profiles.isTestProfile()).thenReturn(!dev);
        server.expect(requestTo(routes.status())).andRespond(withException(new IOException("offline")));
        assertEquals(Constants.AppConstants.MOCKED_GATEWAY_NAME, config.getGatewayName());
        server.verify();
    }

    @ParameterizedTest @ValueSource(strings = {"empty", "failure", "malformed", "offline"})
    void productionGatewayLookupDoesNotSilentlyMockErrors(String response) {
        var expected = server.expect(requestTo(routes.whois()));
        switch (response) {
            case "empty" -> expected.andRespond(withNoContent());
            case "failure" -> expected.andRespond(withServerError());
            case "malformed" -> expected.andRespond(withSuccess("{broken", MediaType.APPLICATION_JSON));
            default -> expected.andRespond(withException(new IOException("offline")));
        }
        assertThrows(BusinessException.class, config::getGatewayName);
        server.verify();
    }

    @Test void reachableDevelopmentConfigIsNotReplacedByMock() {
        when(profiles.isDevOrDockerProfile()).thenReturn(true);
        server.expect(requestTo(routes.status())).andRespond(withSuccess("UP", MediaType.TEXT_PLAIN));
        server.expect(requestTo(routes.whois())).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        assertNull(config.getGatewayName());
        server.verify();
    }

    @Test void configurationItemsAreDeserializedWithRequestedScope() {
        server.expect(requestTo(routes.getConfigItems(ConfigItemTypeEnum.INI_CLIENT)))
                .andRespond(withSuccess("{\"configurationItems\":[{\"key\":\"INI_CLIENT\",\"items\":{\"enabled\":\"true\"}}]}", MediaType.APPLICATION_JSON));
        var actual = config.getConfigurationItems(ConfigItemTypeEnum.INI_CLIENT);
        assertEquals("true", actual.getConfigurationItems().get(0).getItems().get("enabled"));
        server.verify();
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void propertiesPreferServiceValueAndFallbackToGenericWhenMissing(boolean fallback) {
        server.expect(requestTo(routes.status())).andRespond(withSuccess("UP", MediaType.TEXT_PLAIN));
        server.expect(requestTo(routes.getConfigItem(ConfigItemTypeEnum.INI_CLIENT, "enabled")))
                .andRespond(fallback ? withNoContent() : withSuccess("specific", MediaType.TEXT_PLAIN));
        if (fallback) server.expect(requestTo(routes.getConfigItem(ConfigItemTypeEnum.GENERIC, "enabled")))
                .andRespond(withSuccess("generic", MediaType.TEXT_PLAIN));
        assertEquals(fallback ? "generic" : "specific", config.getProps("enabled", "old", ConfigItemTypeEnum.INI_CLIENT));
        server.verify();
    }

    @Test void unreachableConfigPreservesPreviousPropertyWithoutFetchingIt() {
        server.expect(requestTo(routes.status())).andRespond(withException(new IOException("offline")));
        assertEquals("old", config.getProps("enabled", "old", ConfigItemTypeEnum.INI_CLIENT));
        server.verify();
    }

    @Test void crashProgramPostsIssuerPayloadAndReturnsAcknowledgement() {
        when(microservices.getUrlCrashProgramValidator()).thenReturn("http://crash.invalid");
        var issuers = new IssuersDTO();
        issuers.setCounter(3);
        server.expect(requestTo("http://crash.invalid/v1/issuer-create")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.counter").value(3)).andRespond(withSuccess("accepted", MediaType.TEXT_PLAIN));
        assertEquals("accepted", crash.sendIssuerData(issuers));
        server.verify();
    }
}
