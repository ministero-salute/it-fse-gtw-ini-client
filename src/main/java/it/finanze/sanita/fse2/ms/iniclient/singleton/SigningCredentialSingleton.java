package it.finanze.sanita.fse2.ms.iniclient.singleton;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.opensaml.xml.security.x509.BasicX509Credential;

import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.FileUtility;
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
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			try (InputStream authInStreamCrt = new ByteArrayInputStream(FileUtility.getFileFromInternalResources(iniCFG.getKeyStoreLocation()))) {
				keyStore.load(authInStreamCrt, iniCFG.getKeyStorePassword().toCharArray());
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
