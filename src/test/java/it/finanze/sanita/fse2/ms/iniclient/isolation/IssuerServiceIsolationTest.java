/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import it.finanze.sanita.fse2.ms.iniclient.client.ICrashProgramClient;
import it.finanze.sanita.fse2.ms.iniclient.config.MicroservicesCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.TestTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.*;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIssuerRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.IssuerSRV;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(IssuerSRV.class)
class IssuerServiceIsolationTest {
    @Autowired private IssuerSRV service;
    @MockitoBean private IIssuerRepo repository;
    @MockitoBean private ICrashProgramClient crashProgram;
    @MockitoBean private MicroservicesCFG configuration;

    private IssuerCreateRequestDTO request() {
        return IssuerCreateRequestDTO.builder().issuer("issuer").mailResponsabile("owner@example.org")
                .etichettaRegione("region").pazienteCf("fiscal-code").nomeDocumentRepository("repository")
                .readyToScan(true).esonerato(true).mandatoryTests(List.of(TestTypeEnum.values()[0])).build();
    }

    @Test void unknownIssuerIsMockedAndKnownIssuerUsesStoredFlag() {
        assertTrue(service.isMocked("unknown"));
        IssuerETY entity = new IssuerETY();
        entity.setMock(false);
        when(repository.findByName("issuer")).thenReturn(entity);
        assertFalse(service.isMocked("issuer"));
        assertSame(entity, service.findByIssuer("issuer"));
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void createsIssuerAndNotifiesOnlyWhenConfigured(boolean notify) {
        when(repository.createIssuer(any())).thenReturn("id");
        when(configuration.getUrlCrashProgramValidator()).thenReturn(notify ? "http://unused" : "");
        IssuerETY existing = new IssuerETY();
        existing.setIssuer("existing");
        when(repository.findIssuersCrashProgrm()).thenReturn(List.of(existing));
        var response = service.createIssuer(request());
        assertTrue(response.getEsito());
        assertEquals("id", response.getId());
        ArgumentCaptor<IssuerETY> entity = ArgumentCaptor.forClass(IssuerETY.class);
        verify(repository).createIssuer(entity.capture());
        assertEquals("issuer", entity.getValue().getIssuer());
        assertEquals("repository", entity.getValue().getNomeDocumentRepository());
        assertEquals("owner@example.org", entity.getValue().getMailResponsabile());
        assertEquals(request().getMandatoryTests(), entity.getValue().getMandatoryTests());
        assertTrue(entity.getValue().isReadyToScan());
        assertTrue(entity.getValue().getEsonerato());
        if (notify) {
            ArgumentCaptor<IssuersDTO> payload = ArgumentCaptor.forClass(IssuersDTO.class);
            verify(crashProgram).sendIssuerData(payload.capture());
            assertEquals(1, payload.getValue().getCounter());
            assertEquals("issuer", payload.getValue().getActualIssuer().getNome());
            assertNotNull(payload.getValue().getDataAggiornamento());
        } else verifyNoInteractions(crashProgram);
    }

    @ParameterizedTest @ValueSource(ints = {0, 1, 2, 3})
    void rejectsConflictingIssuerRegionPatientOrRepositoryBeforeWriting(int conflict) {
        IssuerETY existing = new IssuerETY();
        IssuerCreateRequestDTO request = request();
        if (conflict == 0) when(repository.findByName("issuer")).thenReturn(existing);
        if (conflict == 1) when(repository.findRegioneMiddleware("region")).thenReturn(existing);
        if (conflict == 2) when(repository.findByFiscalCode("fiscal-code")).thenReturn(existing);
        if (conflict == 3) {
            request.setMiddleware(true);
            when(repository.findByNomeDocumentRepository("repository")).thenReturn(existing);
        }
        if (conflict == 0 || conflict == 2)
            assertThrows(InputValidationException.class, () -> service.createIssuer(request));
        else assertThrows(BadRequestException.class, () -> service.createIssuer(request));
        verify(repository, never()).createIssuer(any());
        verifyNoInteractions(crashProgram);
    }

    @Test void failedInsertDoesNotNotify() {
        assertThrows(BusinessException.class, () -> service.createIssuer(request()));
        verifyNoInteractions(crashProgram);
    }

    @Test void acceptsEmptyOptionalListsAndRepositoryForMiddleware() {
        var request = request();
        request.setNomeDocumentRepository(null);
        request.setMandatoryTests(List.of());
        request.setMiddleware(true);
        when(repository.createIssuer(any())).thenReturn("id");
        assertTrue(service.createIssuer(request).getEsito());
        verify(repository, never()).findByNomeDocumentRepository(any());
    }

    @Test void updatePreservesIdentityAndEmailStatus() {
        IssuerETY existing = new IssuerETY();
        existing.setId("id");
        existing.setEmailSent(true);
        when(repository.updateIssuer(any())).thenReturn("id");
        assertTrue(service.updateIssuer(existing, request()).getEsito());
        ArgumentCaptor<IssuerETY> entity = ArgumentCaptor.forClass(IssuerETY.class);
        verify(repository).updateIssuer(entity.capture());
        assertEquals("id", entity.getValue().getId());
        assertTrue(entity.getValue().isEmailSent());
        assertEquals("fiscal-code", entity.getValue().getPazienteCf());
    }

    @Test void rejectsMiddlewareUpdateWithRepository() {
        var request = request();
        request.setMiddleware(true);
        assertThrows(BadRequestException.class, () -> service.updateIssuer(new IssuerETY(), request));
        verify(repository, never()).updateIssuer(any());
    }

    @Test void updateReportsMissingIdWithoutClaimingSuccess() {
        assertFalse(service.update(new IssuerETY()).getEsito());
        var request = request();
        request.setMandatoryTests(null);
        assertFalse(service.updateIssuer(new IssuerETY(), request).getEsito());
    }

    @Test void removalReportsCountOrNotFound() {
        when(repository.removeByName("issuer")).thenReturn(2);
        assertEquals(2, service.removeIssuer("issuer").getCount());
        assertThrows(NotFoundException.class, () -> service.removeIssuer("missing"));
    }

    @Test void repositoryFailurePropagatesWithoutNotification() {
        RuntimeException failure = new RuntimeException("database unavailable");
        when(repository.createIssuer(any())).thenThrow(failure);
        assertSame(failure, assertThrows(RuntimeException.class, () -> service.createIssuer(request())));
        verifyNoInteractions(crashProgram);
    }
}
