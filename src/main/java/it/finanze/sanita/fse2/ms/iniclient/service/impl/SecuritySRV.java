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
package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SecuritySRV implements ISecuritySRV {

	public static final String PKCS12_STRING = "PKCS12";

	@Autowired
	private IniCFG iniCFG;

//	@Override
//	public SSLContext createSslCustomContext() throws NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
//	 
//		KeyStore keystore = KeyStore.getInstance("JKS");
//		keystore.load(new ByteArrayInputStream(FileUtility.getFileFromInternalResources(iniCFG.getAuthCertLocation())), iniCFG.getAuthCertPassword().toCharArray());
//
//		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//		keyManagerFactory.init(keystore, iniCFG.getAuthCertPassword().toCharArray());
//
//		SSLContext sslContext = SSLContext.getInstance("TLS");
//		sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
//		return sslContext;
//
//	}
	
	@Override
	public SSLContext createSslCustomContext() throws NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {

	    KeyStore keystore = KeyStore.getInstance("JKS");

	    try (FileInputStream fis = new FileInputStream(new File(iniCFG.getAuthCertLocation()))) {
	        keystore.load(fis, iniCFG.getAuthCertPassword().toCharArray());
	    }

	    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	    keyManagerFactory.init(keystore, iniCFG.getAuthCertPassword().toCharArray());

	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());

	    return sslContext;
	}

	private static TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
					log.info("Check client trusted:" + authType);
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
					log.info("Check server trusted:" + authType);
				}
			}
	};

  
}
