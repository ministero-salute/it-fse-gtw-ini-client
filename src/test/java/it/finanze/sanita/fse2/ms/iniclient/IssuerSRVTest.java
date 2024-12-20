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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import it.finanze.sanita.fse2.ms.iniclient.config.MicroservicesCFG;
import it.finanze.sanita.fse2.ms.iniclient.enums.TestTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.iniclient.client.ICrashProgramClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuersDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerDeleteResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.InputValidationException;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.NotFoundException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IssuerETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIssuerRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;

import java.util.Arrays;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IssuerSRVTest {

    @Autowired
    private IIssuerSRV issuerSRV;

    @MockBean
    private IIssuerRepo repo;

    @MockBean
    private ICrashProgramClient crashProgramClient;

    @MockBean
    private MicroservicesCFG msCfg;


    @Test
    @DisplayName("Test when is mocked")
    void isMockedTest(){
        IssuerETY entity = new IssuerETY();
        Boolean expected = true;
        entity.setIssuer("issuerProva");
        entity.setMock(expected);

        when(repo.findByName(anyString())).thenReturn(entity);

        Boolean actual = issuerSRV.isMocked("issuerProva");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test when issuer is already in the database")
    void createIssuerAlreadyExistsTest(){
        IssuerETY entity = new IssuerETY();
        String expected = "idProva";
        entity.setIssuer("issuerProva");
        entity.setMock(true);
        entity.setId(expected);

        IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
        request.setIssuer("issuerProva");
        request.setMock(true);

        when(repo.findByName(anyString())).thenReturn(entity);
        when(repo.createIssuer(Mockito.any(IssuerETY.class))).thenReturn(expected);

        assertThrows(InputValidationException.class,() -> issuerSRV.createIssuer(request));
    }

    @Test
    @DisplayName("Test when id is null")
    void createIssuerIdIsNullTest(){
        IssuerETY entity = new IssuerETY();
        String expected = "idProva";
        entity.setIssuer("issuerProva");
        entity.setMock(true);
        entity.setId(expected);

        IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
        request.setIssuer("issuerProva");
        request.setMock(true);

        when(repo.findByName(anyString())).thenReturn(entity);
        when(repo.createIssuer(Mockito.any(IssuerETY.class))).thenReturn(null);

        assertThrows(InputValidationException.class,() -> issuerSRV.createIssuer(request));
    }

    @Test
    @DisplayName("Test when createIssuer return id")
    void createIssuerTest(){
        IssuerETY entity = new IssuerETY();
        String expected = "idProva";
        entity.setIssuer("issuerProva");
        entity.setMock(true);
        entity.setId(expected);

        IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
        request.setIssuer("issuerProva2");
        request.setMock(true);

        when(repo.findByName(anyString())).thenReturn(null);
        when(repo.createIssuer(Mockito.any(IssuerETY.class))).thenReturn(expected);
        when(crashProgramClient.sendIssuerData(any(IssuersDTO.class))).thenReturn(null);

        IssuerResponseDTO response = issuerSRV.createIssuer(request);
        String actual = response.getId();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test when remove return a count != 0")
    void removeIssuerTest(){
        String name = "test";
        Integer expected = 1;

        when(repo.removeByName(name)).thenReturn(expected);

        IssuerDeleteResponseDTO response = issuerSRV.removeIssuer(name);
        Integer actual = response.getCount();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test when remove return a count == 0")
    void removeIssuerCountIsZeroTest(){
        String name = "test";
        Integer expected = 0;

        when(repo.removeByName(name)).thenReturn(expected);

        assertThrows(NotFoundException.class, () -> issuerSRV.removeIssuer(name));
    }

        @Test
        @DisplayName("isMocked returns true if issuer is null")
        void isMockedIssuerNullTest() {
            when(repo.findByName("unknownIssuer")).thenReturn(null);
            boolean mocked = issuerSRV.isMocked("unknownIssuer");
            assertTrue(mocked, "If issuer not found, default mocked should be true");
        }

        @Test
        @DisplayName("Create issuer - La regione indicata ha già un middleware")
        void createIssuerRegioneAlreadyMiddlewareTest() {
            IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
            request.setIssuer("issuerNew");
            request.setMock(false);
            request.setEtichettaRegione("someRegion");
            request.setMiddleware(true);

            IssuerETY regione = new IssuerETY();
            regione.setMiddleware(true);

            when(repo.findByName("issuerNew")).thenReturn(null);
            when(repo.findRegioneMiddleware("someRegion")).thenReturn(regione);

            assertThrows(BadRequestException.class, () -> issuerSRV.createIssuer(request));
        }

        @Test
        @DisplayName("Create issuer - CF paziente already present")
        void createIssuerPazienteCFAlreadyPresentTest() {
            IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
            request.setIssuer("issuerNew");
            request.setMock(false);
            request.setEtichettaRegione("someRegion");
            request.setPazienteCf("CF123");
            request.setMiddleware(false);

            IssuerETY paziente = new IssuerETY();
            paziente.setPazienteCf("CF123");

            when(repo.findByName("issuerNew")).thenReturn(null);
            when(repo.findRegioneMiddleware("someRegion")).thenReturn(null);
            when(repo.findByFiscalCode("CF123")).thenReturn(paziente);

            assertThrows(InputValidationException.class, () -> issuerSRV.createIssuer(request));
        }

        @Test
        @DisplayName("Create issuer - ASL present and middleware true")
        void createIssuerAslPresentMiddlewareTest() {
            IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
            request.setIssuer("issuerNew");
            request.setMock(false);
            request.setEtichettaRegione("someRegion");
            request.setPazienteCf("CF123");
            request.setMiddleware(true);
            request.setNomeDocumentRepository("ASLDocRepo");

            IssuerETY asl = new IssuerETY();
            when(repo.findByName("issuerNew")).thenReturn(null);
            when(repo.findRegioneMiddleware("someRegion")).thenReturn(null);
            when(repo.findByFiscalCode("CF123")).thenReturn(null);
            when(repo.findByNomeDocumentRepository("ASLDocRepo")).thenReturn(asl);

            assertThrows(BadRequestException.class, () -> issuerSRV.createIssuer(request));
        }

        @Test
        @DisplayName("Create issuer - msCfg.getUrlCrashProgramValidator is empty, no crashProgramClient call")
        void createIssuerNoCrashProgramCallTest() {
            IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
            request.setIssuer("newIssuer");
            request.setMock(false);

            when(repo.findByName("newIssuer")).thenReturn(null);
            when(repo.findRegioneMiddleware(anyString())).thenReturn(null);
            when(repo.findByFiscalCode(anyString())).thenReturn(null);
            when(repo.findByNomeDocumentRepository(anyString())).thenReturn(null);
            when(repo.createIssuer(any(IssuerETY.class))).thenReturn("newId");
            when(msCfg.getUrlCrashProgramValidator()).thenReturn("");

            IssuerResponseDTO response = issuerSRV.createIssuer(request);
            assertNotNull(response.getId());
            assertTrue(response.getEsito());

            verify(crashProgramClient, never()).sendIssuerData(any());
        }

       @Test
       @DisplayName("Create issuer with mandatoryTests not empty")
       void createIssuerWithMandatoryTests() {
           IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
           request.setIssuer("issuerTests");
           request.setMock(false);
           request.setMandatoryTests(Arrays.asList(TestTypeEnum.CREATE, TestTypeEnum.REPLACE));

           when(repo.findByName("issuerTests")).thenReturn(null);
           when(repo.findRegioneMiddleware(anyString())).thenReturn(null);
           when(repo.findByFiscalCode(anyString())).thenReturn(null);
           when(repo.findByNomeDocumentRepository(anyString())).thenReturn(null);
           when(repo.createIssuer(any(IssuerETY.class))).thenReturn("testId");
           when(msCfg.getUrlCrashProgramValidator()).thenReturn("");

           IssuerResponseDTO response = issuerSRV.createIssuer(request);
           assertEquals("testId", response.getId());
           assertTrue(response.getEsito());
       }

       @Test
       @DisplayName("Update issuer - BadRequestException when nomeDocumentRepository not null and middleware = true")
       void updateIssuerBadRequestTest() {
           IssuerETY existing = new IssuerETY();
           existing.setId("oldId");
           existing.setEmailSent(false);
           existing.setMiddleware(true);

           IssuerCreateRequestDTO dto = new IssuerCreateRequestDTO();
           dto.setIssuer("updatedIssuer");
           dto.setMiddleware(true);
           dto.setNomeDocumentRepository("repoName");

           assertThrows(BadRequestException.class, () -> issuerSRV.updateIssuer(existing, dto));
       }

       @Test
       @DisplayName("Update issuer - success scenario")
       void updateIssuerSuccessTest() {
           IssuerETY existing = new IssuerETY();
           existing.setId("oldId");
           existing.setEmailSent(false);
           existing.setMiddleware(false);

           IssuerCreateRequestDTO dto = new IssuerCreateRequestDTO();
           dto.setIssuer("updatedIssuer");
           dto.setMiddleware(false);
           dto.setNomeDocumentRepository(null);

           when(repo.updateIssuer(any(IssuerETY.class))).thenReturn("updatedId");

           IssuerResponseDTO out = issuerSRV.updateIssuer(existing, dto);
           assertTrue(out.getEsito());
           assertEquals("updatedId", out.getId());
       }

       @Test
       @DisplayName("Update issuer - id is null or empty")
       void updateIssuerNoIdReturnedTest() {
           IssuerETY existing = new IssuerETY();
           existing.setId("oldId");
           existing.setEmailSent(false);
           existing.setMiddleware(false);

           IssuerCreateRequestDTO dto = new IssuerCreateRequestDTO();
           dto.setIssuer("updatedIssuer");

           // Return null id
           when(repo.updateIssuer(any(IssuerETY.class))).thenReturn(null);

           IssuerResponseDTO out = issuerSRV.updateIssuer(existing, dto);
           assertFalse(out.getEsito());
           assertNull(out.getId());
       }

       @Test
       @DisplayName("findByIssuer returns null when not found")
       void findByIssuerNotFoundTest() {
           when(repo.findByName("notFound")).thenReturn(null);
           IssuerETY result = issuerSRV.findByIssuer("notFound");
           assertNull(result);
       }

       @Test
       @DisplayName("createIssuer calls crashProgramClient when URL not empty and issuers found")
       void createIssuerWithCrashProgramCallTest() {
           IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
           request.setIssuer("crashProgramIssuer");
           request.setMock(false);

           IssuerETY i1 = new IssuerETY();
           i1.setIssuer("i1");
           i1.setEtichettaRegione("reg1");
           i1.setPazienteCf("cf1");

           IssuerETY i2 = new IssuerETY();
           i2.setIssuer("i2");
           i2.setEtichettaRegione("reg2");
           i2.setPazienteCf("cf2");

           when(repo.findByName("crashProgramIssuer")).thenReturn(null);
           when(repo.findRegioneMiddleware(anyString())).thenReturn(null);
           when(repo.findByFiscalCode(anyString())).thenReturn(null);
           when(repo.findByNomeDocumentRepository(anyString())).thenReturn(null);
           when(repo.createIssuer(any(IssuerETY.class))).thenReturn("crashId");
           when(msCfg.getUrlCrashProgramValidator()).thenReturn("http://some-url");
           when(repo.findIssuersCrashProgrm()).thenReturn(Arrays.asList(i1, i2));

           IssuerResponseDTO response = issuerSRV.createIssuer(request);
           assertEquals("crashId", response.getId());
           assertTrue(response.getEsito());

           verify(crashProgramClient, times(1)).sendIssuerData(any());
       }

       @Test
       @DisplayName("createIssuer no issuers found for crashProgram")
       void createIssuerNoIssuersForCrashProgram() {
           IssuerCreateRequestDTO request = new IssuerCreateRequestDTO();
           request.setIssuer("noCrashIssuer");
           request.setMock(false);

           when(repo.findByName("noCrashIssuer")).thenReturn(null);
           when(repo.findRegioneMiddleware(anyString())).thenReturn(null);
           when(repo.findByFiscalCode(anyString())).thenReturn(null);
           when(repo.findByNomeDocumentRepository(anyString())).thenReturn(null);
           when(repo.createIssuer(any(IssuerETY.class))).thenReturn("noCrashId");
           when(msCfg.getUrlCrashProgramValidator()).thenReturn("http://some-url");
           when(repo.findIssuersCrashProgrm()).thenReturn(Collections.emptyList());

           IssuerResponseDTO response = issuerSRV.createIssuer(request);
           assertEquals("noCrashId", response.getId());
           assertTrue(response.getEsito());

           // Even with no issuers found, crashProgramClient is called with empty data
           verify(crashProgramClient, times(1)).sendIssuerData(any());
       }

}
