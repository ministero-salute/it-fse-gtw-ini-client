/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
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

	@Value("${ini.client.mock-enable}")
	private boolean mockEnable;
}
