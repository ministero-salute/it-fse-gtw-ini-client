package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_AUDIT_INI_ENABLED;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_CONTROL_LOG_ENABLED;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_EXP_DAYS;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_ISSUER_CF;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_KPI_LOG_ENABLED;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_REMOVE_METADATA_ENABLE;
import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.PROPS_NAME_SUBJECT;
import static it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum.INI_CLIENT;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.client.IConfigClient;
import it.finanze.sanita.fse2.ms.iniclient.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ConfigItemDTO.ConfigDataItemDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.ProfileUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfigSRV implements IConfigSRV {

	@Autowired
	private IConfigClient client;

	@Autowired
	private ProfileUtility profiles;
	
	@Value("${ms.config.refresh-rate:900000}")
	private Long refreshRate;

	private final Map<String, Pair<Long, String>> props;

	public ConfigSRV() {
		this.props = new HashMap<>();
	}

	@PostConstruct
	public void postConstruct() {
		if(!profiles.isTestProfile()) {
			init();
		} else {
			log.info("Skipping gtw-config initialization due to test profile");
		}
	}

	@Override
	public Boolean isRemoveMetadataEnable() {
		long lastUpdate = props.get(PROPS_NAME_REMOVE_METADATA_ENABLE).getKey();
		if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
			synchronized(Locks.REMOVE_METADATA_ENABLE) {
				if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
					refresh(PROPS_NAME_REMOVE_METADATA_ENABLE);
				}
			}
		}
		return Boolean.parseBoolean(
			props.get(PROPS_NAME_REMOVE_METADATA_ENABLE).getValue()
		);
	}

	@Override
	public Boolean isSubjectNotAllowed() {
		long lastUpdate = props.get(PROPS_NAME_SUBJECT).getKey();
		if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
			synchronized (Locks.SUBJECT_CLEANING) {
				if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
					refresh(PROPS_NAME_SUBJECT);
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
		if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
			synchronized(Locks.ISSUER_CF_CLEANING) {
				if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
					refresh(PROPS_NAME_ISSUER_CF);
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
		if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
			synchronized(Locks.CONTROL_LOG_ENABLED) {
				if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
					refresh(PROPS_NAME_CONTROL_LOG_ENABLED);
				}
			}
		}
		return Boolean.parseBoolean(props.get(PROPS_NAME_CONTROL_LOG_ENABLED).getValue());
	}
	
	@Override
	public Boolean isKpiLogPersistenceEnable() {
		long lastUpdate = props.get(PROPS_NAME_KPI_LOG_ENABLED).getKey();
		if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
			synchronized(Locks.KPI_LOG_ENABLED) {
				if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
					refresh(PROPS_NAME_KPI_LOG_ENABLED);
				}
			}
		}
		return Boolean.parseBoolean(props.get(PROPS_NAME_KPI_LOG_ENABLED).getValue());
	}

	@Override
	public Boolean isAuditIniEnable() {
		long lastUpdate = props.get(PROPS_NAME_AUDIT_INI_ENABLED).getKey();
		if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
			synchronized(Locks.AUDIT_INI_ENABLED) {
				if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
					refresh(PROPS_NAME_AUDIT_INI_ENABLED);
				}
			}
		}
		return Boolean.parseBoolean(props.get(PROPS_NAME_AUDIT_INI_ENABLED).getValue());
	}
	

	@Override
	public Integer getExpirationDate() {
		long lastUpdate = props.get(PROPS_NAME_EXP_DAYS).getKey();
		if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
			synchronized(Locks.EXP_DAYS) {
				if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
					refresh(PROPS_NAME_EXP_DAYS);
				}
			}
		}
		return Integer.parseInt(
			props.get(PROPS_NAME_EXP_DAYS).getValue()
		);
	}

	private void refresh(String name) {
		String previous = props.getOrDefault(name, Pair.of(0L, null)).getValue();
		String prop = client.getProps(name, previous, INI_CLIENT);
		props.put(name, Pair.of(new Date().getTime(), prop));
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

	private void init() {
		for(ConfigItemTypeEnum en : ConfigItemTypeEnum.priority()) {
			log.info("[GTW-CFG] Retrieving {} properties ...", en.name());
			ConfigItemDTO items = client.getConfigurationItems(en);
			List<ConfigDataItemDTO> opts = items.getConfigurationItems();
			for(ConfigDataItemDTO opt : opts) {
				opt.getItems().forEach((key, value) -> {
					log.info("[GTW-CFG] Property {} is set as {}", key, value);
					props.put(key, Pair.of(new Date().getTime(), value));
				});
			}
			if(opts.isEmpty()) log.info("[GTW-CFG] No props were found");
		}
		integrity();
	}

	@Override
	public long getRefreshRate() {
		return this.refreshRate;
	}

	private static final class Locks {
		public static final Object REMOVE_METADATA_ENABLE = new Object();
		public static final Object CONTROL_LOG_ENABLED = new Object();
		public static final Object KPI_LOG_ENABLED = new Object();
		public static final Object ISSUER_CF_CLEANING = new Object();
		public static final Object SUBJECT_CLEANING = new Object();
		public static final Object AUDIT_INI_ENABLED = new Object();
		public static final Object EXP_DAYS = new Object();
	}

}
