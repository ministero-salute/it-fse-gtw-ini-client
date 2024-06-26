package it.finanze.sanita.fse2.ms.iniclient.client.routes;

import it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.*;

@Component
public final class ConfigClientRoutes {

    @Value("${ms.url.gtw-config}")
    private String configHost;

    public UriComponentsBuilder base() {
        return UriComponentsBuilder.fromHttpUrl(configHost);
    }

    public String identifier() {
        return IDENTIFIER;
    }

    public String microservice() {
        return IDENTIFIER_MS;
    }

    public String status() {
        return base()
                .pathSegment(API_STATUS)
                .build()
                .toUriString();
    }

    public String whois() {
        return base()
                .pathSegment(API_VERSION, API_WHOIS)
                .build()
                .toUriString();
    }

    public String getConfigItem(ConfigItemTypeEnum type, String props) {
        return base()
                .pathSegment(API_VERSION, API_CONFIG_ITEMS, API_PROPS)
                .queryParam(QP_TYPE, type.name())
                .queryParam(QP_PROPS, props)
                .build()
                .toUriString();
    }

    public String getConfigItems(ConfigItemTypeEnum type) {
        return base()
                .pathSegment(API_VERSION, API_CONFIG_ITEMS)
                .queryParam(QP_TYPE, type.name())
                .build()
                .toUriString();
    }
}