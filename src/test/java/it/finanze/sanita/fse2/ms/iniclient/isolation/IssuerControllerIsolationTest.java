/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import it.finanze.sanita.fse2.ms.iniclient.controller.impl.IssuerCTL;
import it.finanze.sanita.fse2.ms.iniclient.controller.handler.ControllerExceptionHandler;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.*;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.*;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@TestPropertySource(properties = "uar.client.mock-enable=false")
@SpringJUnitConfig({IssuerCTL.class, ControllerExceptionHandler.class})
class IssuerControllerIsolationTest {
    @Autowired private IssuerCTL controller;
    @Autowired private ControllerExceptionHandler advice;
    @MockitoBean private IIssuerSRV service;
    @MockitoBean private Tracer tracer;
    private MockMvc mvc;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(advice).build();
    }

    @Test void httpCreateReturnsCreatedAndSerializedBusinessResult() throws Exception {
        var response = new IssuerResponseDTO();
        response.setId("id");
        response.setEsito(true);
        when(service.createIssuer(any())).thenReturn(response);
        mvc.perform(post("/v1/issuer-create").contentType("application/json").content("""
                {"issuer":"issuer","mailResponsabile":"owner@example.org","etichettaRegione":"region",
                 "nomeDocumentRepository":"repository","middleware":false}
                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value("id"))
                .andExpect(jsonPath("$.esito").value(true));
    }

    @Test void invalidJsonAndBeanValidationDoNotReachService() throws Exception {
        mvc.perform(post("/v1/issuer-create").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/v1/issuer-create").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void missingIssuerReturnsNotFound() throws Exception {
        mvc.perform(put("/v1/mock-eds/missing/true")).andExpect(status().isNotFound());
        verify(service, never()).update(any());
    }

    @Test void businessConflictAndBackendFailureHaveDistinctHttpStatuses() throws Exception {
        when(service.removeIssuer("conflict")).thenThrow(new InputValidationException("duplicate"));
        when(service.removeIssuer("bad")).thenThrow(new BadRequestException("invalid"));
        when(service.removeIssuer("offline")).thenThrow(new BusinessException("offline"));
        mvc.perform(delete("/v1/issuer-delete/conflict")).andExpect(status().isConflict());
        mvc.perform(delete("/v1/issuer-delete/bad")).andExpect(status().isBadRequest());
        mvc.perform(delete("/v1/issuer-delete/offline")).andExpect(status().isInternalServerError());
    }

    @Test void mockFlagUpdateMutatesAndPersistsTheExistingIssuer() throws Exception {
        IssuerETY entity = new IssuerETY();
        when(service.findByIssuer("issuer")).thenReturn(entity);
        when(service.update(entity)).thenReturn(new IssuerResponseDTO());
        mvc.perform(put("/v1/mock-eds/issuer/true")).andExpect(status().isOk());
        assertTrue(entity.getMockUar());
        verify(service).update(entity);
    }

    @Test void directCreateRejectsMissingIssuerOrRepositoryBeforeCallingService() {
        var request = new IssuerCreateRequestDTO();
        assertThrows(BusinessException.class, () -> controller.create(request, null));
        request.setIssuer("issuer");
        assertThrows(BusinessException.class, () -> controller.create(request, null));
        verifyNoInteractions(service);
    }

    @Test void replacementRequiresExistingIssuerAndRepository() {
        var request = new IssuerCreateRequestDTO();
        assertThrows(BusinessException.class, () -> controller.replace(request, "issuer", null));
        request.setNomeDocumentRepository("repository");
        assertThrows(NotFoundException.class, () -> controller.replace(request, "issuer", null));
        IssuerETY entity = new IssuerETY();
        var response = new IssuerResponseDTO();
        when(service.findByIssuer("issuer")).thenReturn(entity);
        when(service.updateIssuer(entity, request)).thenReturn(response);
        assertSame(response, controller.replace(request, "issuer", null));
    }

    @Test void deletionReturnsServiceResult() throws Exception {
        var response = new IssuerDeleteResponseDTO();
        response.setEsito(true);
        response.setCount(1);
        when(service.removeIssuer("issuer")).thenReturn(response);
        mvc.perform(delete("/v1/issuer-delete/issuer")).andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test void successfulControllerResponseIncludesAvailableTraceIdentifiers() {
        SpanBuilder builder = mock(SpanBuilder.class);
        Span span = Span.wrap(SpanContext.create("12345678901234567890123456789012", "1234567890123456",
                TraceFlags.getDefault(), TraceState.getDefault()));
        when(tracer.spanBuilder(anyString())).thenReturn(builder);
        when(builder.startSpan()).thenReturn(span);
        var response = new IssuerDeleteResponseDTO();
        when(service.removeIssuer("issuer")).thenReturn(response);
        controller.delete("issuer", null);
        assertNotNull(response.getTraceID());
        assertNotNull(response.getSpanID());
        assertFalse(response.getTraceID().isEmpty());
        assertFalse(response.getSpanID().isEmpty());
    }
}
