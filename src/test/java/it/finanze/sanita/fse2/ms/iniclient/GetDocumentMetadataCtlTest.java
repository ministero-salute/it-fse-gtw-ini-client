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
package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetReferenceReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetDocumentMetadataResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.DocumentReferenceNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.MergeMetadatoNotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;

/**
 * Test del controller dell'endpoint POST /v1/get-document-metadata/{idDoc}.
 *
 * <p>Verifica che un documento non trovato su INI sia restituito come 404 (come l'endpoint
 * get-reference) e non come 200 con {@code errorMessage} nullo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetDocumentMetadataCtlTest {

	private static final String ID_DOC = "2.16.840.1.113883.2.9.2.120.4.4^120.20261104123404";

	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt;

	@Autowired
	private RestTemplate restTemplate;

	/** Evita il caricamento dei certificati SOAP all'avvio del bean reale. */
	@MockitoBean
	private IIniClient iniClient;

	@MockitoBean
	private IIniInvocationSRV iniInvocationSRV;

	@Test
	@DisplayName("GetDocumentMetadata - documento trovato: 200 con uuid")
	void foundReturnsOkTest() {
		GetDocumentMetadataResponseDTO srvOut = new GetDocumentMetadataResponseDTO();
		srvOut.setUuid("urn:uuid:9e5a08cf-5cb9-4ad3-b779-44618b2d4f7a");
		srvOut.setEdsPublished("TRUE");
		Mockito.when(iniInvocationSRV.getDocumentMetadata(anyString(), any(JWTTokenDTO.class), anyString()))
				.thenReturn(srvOut);

		ResponseEntity<GetDocumentMetadataResponseDTO> response = callGetDocumentMetadata();

		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());
		assertEquals("urn:uuid:9e5a08cf-5cb9-4ad3-b779-44618b2d4f7a", response.getBody().getUuid());
		assertEquals("TRUE", response.getBody().getEdsPublished());
		assertNull(response.getBody().getErrorMessage());
	}

	@Test
	@DisplayName("GetDocumentMetadata - nessun risultato da INI (DocumentReferenceNotFoundException): 404")
	void referenceNotFoundReturns404Test() {
		Mockito.when(iniInvocationSRV.getDocumentMetadata(anyString(), any(JWTTokenDTO.class), anyString()))
				.thenThrow(new DocumentReferenceNotFoundException());

		HttpClientErrorException.NotFound ex = assertThrows(HttpClientErrorException.NotFound.class,
				this::callGetDocumentMetadata);

		// Il body e' l'ErrorResponseDTO prodotto dal ControllerExceptionHandler a partire dall'ErrorDTO dell'eccezione
		String body = ex.getResponseBodyAsString();
		assertTrue(body.contains("References not found"), body);
		assertTrue(body.contains("/missing-references"), body);
	}

	@Test
	@DisplayName("GetDocumentMetadata - ExtrinsicObject assente (MergeMetadatoNotFoundException): 404 con dettaglio")
	void metadataNotFoundReturns404Test() {
		Mockito.when(iniInvocationSRV.getDocumentMetadata(anyString(), any(JWTTokenDTO.class), anyString()))
				.thenThrow(new MergeMetadatoNotFoundException("Metadati non trovati per documento: " + ID_DOC));

		HttpClientErrorException.NotFound ex = assertThrows(HttpClientErrorException.NotFound.class,
				this::callGetDocumentMetadata);

		String body = ex.getResponseBodyAsString();
		assertTrue(body.contains("Metadati non trovati per documento"), body);
		assertTrue(body.contains("/missing-metadata"), body);
	}

	@Test
	@DisplayName("GetDocumentMetadata - altro errore: 200 con errorMessage valorizzato")
	void genericErrorReturnsOkWithErrorMessageTest() {
		Mockito.when(iniInvocationSRV.getDocumentMetadata(anyString(), any(JWTTokenDTO.class), anyString()))
				.thenThrow(new BusinessException("Errore INI"));

		ResponseEntity<GetDocumentMetadataResponseDTO> response = callGetDocumentMetadata();

		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());
		assertEquals("Errore INI", response.getBody().getErrorMessage());
		assertNull(response.getBody().getUuid());
	}

	private ResponseEntity<GetDocumentMetadataResponseDTO> callGetDocumentMetadata() {
		String url = "http://localhost:" +
				webServerAppCtxt.getWebServer().getPort() +
				webServerAppCtxt.getServletContext().getContextPath() +
				"/v1/get-document-metadata/" + ID_DOC;

		JWTPayloadDTO payload = JWTPayloadDTO.builder()
				.iss("201123456")
				.sub("RSSMRA22A01A399Z^^^&2.16.840.1.113883.2.9.4.3.2&ISO")
				.subject_organization("Regione Lazio")
				.subject_organization_id("120")
				.locality("Regione Lazio")
				.subject_role("AAS")
				.person_id("RSSMRA22A01A399Z")
				.patient_consent(true)
				.purpose_of_use("TREATMENT")
				.resource_hl7_type("('11502-2^^2.16.840.1.113883.6.1')")
				.action_id("DELETE")
				.build();

		GetReferenceReqDTO body = new GetReferenceReqDTO();
		body.setToken(payload);
		body.setWorkflowInstanceId(TestConstants.TEST_WII);

		return restTemplate.postForEntity(url, new HttpEntity<>(body), GetDocumentMetadataResponseDTO.class);
	}
}
