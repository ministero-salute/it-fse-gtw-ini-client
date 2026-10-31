/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import it.finanze.sanita.fse2.ms.iniclient.controller.impl.AbstractCTL;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;

@Tag("ISOLATION")
class EdsPublicationIsolationTest {
    private static class Controller extends AbstractCTL {
        String resolve(IssuerETY issuer) { return resolveEdsPublished("issuer", issuer); }
    }

    @Test void globallyDisabledMockPublishesToEdsWithoutIssuerLookup() {
        Controller controller = new Controller();
        IIssuerSRV issuers = mock(IIssuerSRV.class);
        ReflectionTestUtils.setField(controller, "issuserSRV", issuers);
        assertEquals("TRUE", controller.resolve(null));
        verifyNoInteractions(issuers);
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void storedMockFlagDeterminesPublicationForLoadedAndFetchedIssuer(boolean mockUar) {
        Controller controller = new Controller();
        IIssuerSRV issuers = mock(IIssuerSRV.class);
        ReflectionTestUtils.setField(controller, "issuserSRV", issuers);
        ReflectionTestUtils.setField(controller, "enableMockUar", true);
        IssuerETY issuer = new IssuerETY();
        issuer.setMockUar(mockUar);
        when(issuers.findByIssuer("issuer")).thenReturn(issuer);
        assertEquals(mockUar ? "FALSE" : "TRUE", controller.resolve(issuer));
        verifyNoInteractions(issuers);
        assertEquals(mockUar ? "FALSE" : "TRUE", controller.resolve(null));
        verify(issuers).findByIssuer("issuer");
    }

    @Test void unknownIssuerAndUnsetMockFlagDefaultToNotPublished() {
        Controller controller = new Controller();
        IIssuerSRV issuers = mock(IIssuerSRV.class);
        ReflectionTestUtils.setField(controller, "issuserSRV", issuers);
        ReflectionTestUtils.setField(controller, "enableMockUar", true);
        assertEquals("FALSE", controller.resolve(null));
        assertEquals("FALSE", controller.resolve(new IssuerETY()));
        when(issuers.findByIssuer("issuer")).thenReturn(new IssuerETY());
        assertEquals("FALSE", controller.resolve(null));
    }
}
