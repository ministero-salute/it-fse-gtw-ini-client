/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@Service
@Slf4j
public class SecuritySRV implements ISecuritySRV {

    public static final String PKCS12_STRING = "PKCS12";

    @Autowired
    private IniCFG iniCFG;

    @Override
    public SSLContext createSslCustomContext() throws NoSuchAlgorithmException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Client keyStore
            KeyStore keyStore = this.loadKeyStore();
            keyManagerFactory.init(keyStore, iniCFG.getKeyStorePassword().toCharArray());

            // Client trustStore -> trust cert server
            KeyStore trustStore = this.loadTrustStore();
            trustManagerFactory.init(trustStore);

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            log.error("Failed to create SSLContext:" + e.getMessage());
            throw new BusinessException("Failed to create SSLContext:" + e.getMessage());
        }
        return sslContext;
    }

    private KeyStore loadKeyStore() throws KeyStoreException {
        KeyStore keyStore = KeyStore.getInstance(PKCS12_STRING);
        try (InputStream authInStreamCrt = FileUtility.getFileFromGenericResource(iniCFG.getKeyStoreLocation())) {
            keyStore.load(authInStreamCrt, iniCFG.getKeyStorePassword().toCharArray());
        } catch (Exception e) {
            log.error("Exception in loadKeyStore: " + e.getMessage());
        }
        return keyStore;
    }

    private KeyStore loadTrustStore() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
        KeyStore trustStore = KeyStore.getInstance(PKCS12_STRING);
        trustStore.load(null, null);
        try  {
            X509Certificate authCert = this.loadAuthCertificate(iniCFG.getTrustStoreLocation());
            trustStore.setCertificateEntry("sogei", authCert);
        } catch (Exception e) {
            log.error("Failed to load trust store:" + e.getMessage());
            throw new BusinessException("Failed to load trust store:" + e.getMessage());
        }
        return trustStore;
    }

    private X509Certificate loadAuthCertificate(String path) throws KeyStoreException {
        KeyStore keystore = KeyStore.getInstance(PKCS12_STRING);
        try (InputStream inputStream = FileUtility.getFileFromGenericResource(path)) {
            keystore.load(inputStream, iniCFG.getTrustStorePassword().toCharArray());
            Enumeration<String> en = keystore.aliases();
            String keyAlias = "";
            while (en.hasMoreElements()) {
                keyAlias = en.nextElement();
                if (en.hasMoreElements() && iniCFG.getTrustStoreAlias() != null && !iniCFG.getTrustStoreAlias().isEmpty()) {
                    keyAlias = iniCFG.getTrustStoreAlias();
                    break;
                }
            }
            return (X509Certificate) keystore.getCertificate(keyAlias);
        } catch (Exception e) {
            throw new BusinessException("Error on load auth certificate:" + e.getMessage());
        }
    }
}
