/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class IniCFG {

	@Value("${ini.client.publish-wsdl}")
	private String wsdlPublishLocation;

	@Value("${ini.client.delete-wsdl}")
	private String wsdlDeleteLocation;

	@Value("${ini.client.update-wsdl}")
	private String wsdlUpdateLocation;

	@Value("${ini.client.get-wsdl}")
	private String wsdlGetLocation;
	
	@Value("${ini.client.enable-log}")
	private boolean enableLog;

	@Value("${ini.client.auth-cert.path}")
	private String trustStoreLocation;

	@Value("${ini.client.auth-cert.password}")
	private String trustStorePassword;

	@Value("${ini.client.auth-cert.alias}")
	private String trustStoreAlias;

	@Value("${ini.client.ds-cert.path}")
	private String keyStoreLocation;

	@Value("${ini.client.ds-cert.password}")
	private String keyStorePassword;

	@Value("${ini.client.ds-cert.alias}")
	private String keyStoreAlias;
	
	@Value("${ini.client.enable-ssl}")
	private boolean enableSSL;
}
