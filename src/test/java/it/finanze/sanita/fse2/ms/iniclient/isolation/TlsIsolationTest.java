/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.SecuritySRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.FileUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.ProfileUtility;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(SecuritySRV.class)
class TlsIsolationTest {
    @Autowired private SecuritySRV security;
    @MockitoBean private IniCFG configuration;
    @MockitoBean private ProfileUtility profiles;
    @TempDir Path directory;

    private Path store() throws Exception {
        var store = KeyStore.getInstance("JKS");
        store.load(null, "password".toCharArray());
        Path path = directory.resolve("empty.jks");
        try (OutputStream out = Files.newOutputStream(path)) { store.store(out, "password".toCharArray()); }
        return path;
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void initializesTlsWithLocalKeyStoreAndProfileSpecificTrust(boolean development) throws Exception {
        Path store = store();
        when(configuration.getAuthCertLocation()).thenReturn(store.toString());
        when(configuration.getAuthCertPassword()).thenReturn("password");
        when(profiles.isDevOrDockerProfile()).thenReturn(development);
        when(configuration.getTrustorePath()).thenReturn(store.toString());
        when(configuration.getTrustorePass()).thenReturn("password");
        var context = security.createSslCustomContext();
        assertEquals("TLS", context.getProtocol());
        assertNotNull(context.getSocketFactory());
        assertTrue(context.createSSLEngine().getEnabledProtocols().length > 0);
        if (development) verify(configuration, never()).getTrustorePath();
        else verify(configuration).getTrustorePath();
    }

    @Test void wrongKeystorePasswordFailsRatherThanFallingBackToTrustAll() throws Exception {
        when(configuration.getAuthCertLocation()).thenReturn(store().toString());
        when(configuration.getAuthCertPassword()).thenReturn("wrong");
        assertThrows(java.io.IOException.class, security::createSslCustomContext);
        verifyNoInteractions(profiles);
    }

    @Test void fileLoadingSupportsResourcesAndReportsMissingFiles() throws Exception {
        try (var stream = FileUtility.getFileFromAbsoluteOrResourceInputStream("Files/query_response_leafclass.xml")) {
            assertTrue(new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).contains("AdhocQueryResponse"));
        }
        assertThrows(BusinessException.class, () -> FileUtility.getFileFromAbsoluteOrResourceInputStream("missing-isolation-file"));
        assertNull(FileUtility.getFileFromInternalResources("missing-isolation-file"));
    }
}
