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
package it.finanze.sanita.fse2.ms.iniclient.singleton;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.opensaml.xml.security.x509.BasicX509Credential;

import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SigningCredentialSingleton {

	private static BasicX509Credential signingCertificateInstance;
	

	public static synchronized BasicX509Credential getInstance(final IniCFG iniCFG) {
        if (signingCertificateInstance == null) {
        	signingCertificateInstance = getSigningCredential(iniCFG);
        }
        return signingCertificateInstance;
    }

	
	private static BasicX509Credential getSigningCredential(IniCFG iniCFG) {
		BasicX509Credential credential = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			try (FileInputStream fis = new FileInputStream(new File(iniCFG.getKeyStoreLocation()))) {
				keyStore.load(fis, iniCFG.getKeyStorePassword().toCharArray());
			}
			Enumeration<String> en = keyStore.aliases();
			String keyAlias = "";
			while (en.hasMoreElements()) {
				keyAlias = en.nextElement();
				if (en.hasMoreElements() && iniCFG.getKeyStoreAlias() != null && !iniCFG.getKeyStoreAlias().isEmpty()) {
					keyAlias = iniCFG.getKeyStoreAlias();
					break;
				}
			}
			Certificate c = keyStore.getCertificate(keyAlias);
			PrivateKey key = (PrivateKey)keyStore.getKey(keyAlias, iniCFG.getKeyStorePassword().toCharArray());

			credential = new BasicX509Credential();
			credential.setEntityCertificate((java.security.cert.X509Certificate) c);
			credential.setPrivateKey(key);
		} catch (Exception ex) {
			log.error("Error while perform get signing credential : " + ex.getMessage());
			throw new BusinessException("Error while perform get signing credential : " + ex.getMessage());
		}
		return credential;
	}
}
