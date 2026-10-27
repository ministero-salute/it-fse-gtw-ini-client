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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import lombok.extern.slf4j.Slf4j;

/**
 * Network and security infrastructure used by the INI client: OpenSAML bootstrap,
 * custom {@link SSLContext} and GovWay proxy.
 * <p>
 * The {@link SSLContext} and {@link ProxySelector} beans also register themselves as JVM-wide defaults.
 */
@Slf4j
@Configuration
public class IniNetworkCFG {

	public static final String OPENSAML_BOOTSTRAP = "openSamlBootstrap";

	public static final String PROXY_URIS = "proxyUris";

	@Bean(OPENSAML_BOOTSTRAP)
	public Boolean openSamlBootstrap() {
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			log.error("Error in block while run default bootstrap : ", e);
		}
		return Boolean.TRUE;
	}

	@Bean
	@ConditionalOnProperty(name = "ini.client.enable-ssl", havingValue = "true")
	public SSLContext sslContext(ISecuritySRV securitySRV) throws Exception {
		SSLContext sslContext = securitySRV.createSslCustomContext();
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		return sslContext;
	}

	@Bean(PROXY_URIS)
	@ConditionalOnExpression("!'${govway-url:}'.isEmpty()")
	public List<URI> proxyUris(IniCFG iniCFG) {
		List<String> proxyDomains = Arrays.asList(
			iniCFG.getUrlWsdlDocumentRegistryService(),
			iniCFG.getUrlWsdlUpdateDocumentRegistryService(),
			iniCFG.getUrlWsdlDeletetService(),
			iniCFG.getUrlWsdlRecuperoRiferimentoService()
		);
		return proxyDomains.stream().map(this::toURI).filter(Objects::nonNull).toList();
	}

	@Bean
	@ConditionalOnExpression("!'${govway-url:}'.isEmpty()")
	public ProxySelector proxySelector(@Qualifier(PROXY_URIS) List<URI> uris, GovwayCfg govwayCfg) {
		ProxySelector selector = new ProxySelector() {
			@Override
			public List<Proxy> select(URI uri) {
				if (uris.stream().anyMatch(u -> u.getHost().equals(uri.getHost()))) {
					return Collections.singletonList(new Proxy(
						Proxy.Type.HTTP,
						new InetSocketAddress(govwayCfg.getGovwayUrl(), govwayCfg.getGovwayPort())
					));
				}

				return Collections.singletonList(Proxy.NO_PROXY);
			}

			@Override
			public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
				ioe.printStackTrace();
			}
		};
		ProxySelector.setDefault(selector);
		return selector;
	}

	private URI toURI(String url) {
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			log.error("Invalid URI: {}", url, e);
			return null;
		}
	}
}
