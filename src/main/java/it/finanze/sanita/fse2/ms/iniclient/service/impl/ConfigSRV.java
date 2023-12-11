package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import it.finanze.sanita.fse2.ms.iniclient.client.IConfigClient;
import it.finanze.sanita.fse2.ms.iniclient.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ConfigItemDTO.ConfigDataItemDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.*;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum.GENERIC;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum.INI_CLIENT;

;

@Service
@Slf4j
public class ConfigSRV implements IConfigSRV {

	private static final long DELTA_MS = 300_000L;

	@Autowired
	private IConfigClient client;

	private final Map<String, Pair<Long, String>> props;

	public ConfigSRV() {
		this.props = new HashMap<>();
	}

	@PostConstruct
	public void postConstruct() {
		for(ConfigItemTypeEnum en : ConfigItemTypeEnum.values()) {
			log.info("[GTW-CFG] Retrieving {} properties ...", en.name());
			ConfigItemDTO items = client.getConfigurationItems(en);
			List<ConfigDataItemDTO> opts = items.getConfigurationItems();
			for(ConfigDataItemDTO opt : opts) {
				opt.getItems().forEach((key, value) -> {
					log.info("[GTW-CFG] Property {} is set as {}", key, value);
					props.put(key, Pair.of(new Date().getTime(), value));
				});
			}
		}
		integrity();
	}

	@Override
	public Boolean isRemoveMetadataEnable() {
		long lastUpdate = props.get(PROPS_NAME_REMOVE_METADATA_ENABLE).getKey();
		if (new Date().getTime() - lastUpdate >= DELTA_MS) {
			synchronized(ConfigSRV.class) {
				if (new Date().getTime() - lastUpdate >= DELTA_MS) {
					refresh(INI_CLIENT, PROPS_NAME_REMOVE_METADATA_ENABLE);
				}
			}
		}
		return Boolean.parseBoolean(
			props.get(PROPS_NAME_REMOVE_METADATA_ENABLE).getValue()
		);
	}

	private void refresh(ConfigItemTypeEnum type, String name) {
		String previous = props.getOrDefault(name, Pair.of(0L, null)).getValue();
		String prop = client.getProps(type, name, previous);
		props.put(name, Pair.of(new Date().getTime(), prop));
	}

	@Override
	public Boolean isSubjectPersistenceEnabled() {
		long lastUpdate = props.get(PROPS_NAME_SUBJECT).getKey();
		if (new Date().getTime() - lastUpdate >= DELTA_MS) {
			synchronized (PROPS_NAME_SUBJECT) {
				if (new Date().getTime() - lastUpdate >= DELTA_MS) {
					refresh(GENERIC, PROPS_NAME_SUBJECT);
				}
			}
		}
		return Boolean.parseBoolean(
			props.get(PROPS_NAME_SUBJECT).getValue()
		);
	}

	@Override
	public Boolean isCfOnIssuerNotAllowed() {
		long lastUpdate = props.get(PROPS_NAME_ISSUER_CF).getKey();
		if (new Date().getTime() - lastUpdate >= DELTA_MS) {
			synchronized(PROPS_NAME_ISSUER_CF) {
				if (new Date().getTime() - lastUpdate >= DELTA_MS) {
					refresh(GENERIC, PROPS_NAME_ISSUER_CF);
				}
			}
		}
		return Boolean.parseBoolean(
			props.get(PROPS_NAME_ISSUER_CF).getValue()
		);
	}

	@Override
	public Boolean isControlLogPersistenceEnable() {
		long lastUpdate = props.get(PROPS_NAME_CONTROL_LOG_ENABLED).getKey();
		if (new Date().getTime() - lastUpdate >= DELTA_MS) {
			synchronized(ConfigSRV.class) {
				if (new Date().getTime() - lastUpdate >= DELTA_MS) {
					refresh(GENERIC, PROPS_NAME_CONTROL_LOG_ENABLED);
				}
			}
		}
		return Boolean.parseBoolean(props.get(PROPS_NAME_CONTROL_LOG_ENABLED).getValue());
	}
	
	@Override
	public Boolean isKpiLogPersistenceEnable() {
		long lastUpdate = props.get(PROPS_NAME_KPI_LOG_ENABLED).getKey();
		if (new Date().getTime() - lastUpdate >= DELTA_MS) {
			synchronized(ConfigSRV.class) {
				if (new Date().getTime() - lastUpdate >= DELTA_MS) {
					refresh(GENERIC, PROPS_NAME_KPI_LOG_ENABLED);
				}
			}
		}
		return Boolean.parseBoolean(props.get(PROPS_NAME_KPI_LOG_ENABLED).getValue());
	}

	private void integrity() {
		String err = "Missing props {} from ini-client";
		String[] out = new String[]{
			PROPS_NAME_KPI_LOG_ENABLED,
			PROPS_NAME_CONTROL_LOG_ENABLED,
			PROPS_NAME_SUBJECT,
			PROPS_NAME_ISSUER_CF,
			PROPS_NAME_REMOVE_METADATA_ENABLE
		};
		for (String prop : out) {
			if(!props.containsKey(prop)) throw new IllegalStateException(err.replace("{}", prop));
		}
	}

}
