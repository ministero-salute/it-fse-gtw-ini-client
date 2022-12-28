/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentTreeDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl.IniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.PublishReplaceBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.delete.DeleteBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.UpdateBodyBuilderUtility;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.bind.JAXBException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityTest {

    @Autowired
    private ISecuritySRV securitySRV;

    @Autowired
    private ConfigurableApplicationContext context;

    @SpyBean
    private IniCFG iniCFG;

    @Test
    @DisplayName("SSLContext - success test")
    void sslContextSuccessTest() throws NoSuchAlgorithmException {
        SSLContext sslContext = securitySRV.createSslCustomContext();
        assertNotNull(sslContext);
        assertNotNull(sslContext.getSocketFactory());
    }

    @Test
    @DisplayName("SSLContext - failed to load trust store")
    void sslContextTrustStoreFailureTest() {
        when(iniCFG.getTrustStoreLocation()).thenReturn("nothing");
        assertThrows(BusinessException.class, () -> securitySRV.createSslCustomContext());
    }

    @Test
    @DisplayName("SSLContext - failed to load key store")
    void sslContextKeyStoreFailureTest() {
        when(iniCFG.getKeyStoreLocation()).thenReturn("nothing");
        assertThrows(BusinessException.class, () -> securitySRV.createSslCustomContext());
    }
}
