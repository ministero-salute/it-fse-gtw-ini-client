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

import com.google.gson.Gson;
import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.net.ssl.SSLContext;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class IniClientErrorTest extends AbstractTest {

    @MockBean
    private ISecuritySRV securitySRV;

    @BeforeEach
    void init() throws NoSuchAlgorithmException {
        Mockito.when(securitySRV.createSslCustomContext()).thenReturn(Mockito.mock(SSLContext.class));
    }

    @Test
    @DisplayName("errorPublishTest")
    void errorPublishTest() {
        String workflowInstanceId = "2.16.840.1.113883.2.9.2.120.4.4.030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J.4e25c802bbd04956a3f2355000976cc3^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> callPublishIniClient(workflowInstanceId));
    }

    @Test
    @DisplayName("notFoundPublishErrorTest")
    void notFoundPublishErrorTest() {
        String workflowInstanceId = "notfound";
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> callPublishIniClient(workflowInstanceId));
    }

    @Test
    @DisplayName("errorDeleteTest")
    void errorDeleteTest() {
        String identificativoDelete = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> callDeleteIniClient(identificativoDelete));
    }

    @Test
    @DisplayName("errorGetMetadataReferenceTest")
    void errorGetMetadataReferenceTest() {
        String idDoc = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> callGetMetadata(idDoc));
    }

    @Test
    @DisplayName("errorReplaceTest")
    void errorReplaceTest() {
        String workflowInstanceId = "2.16.840.1.113883.2.9.2.120.4.4.030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J.4e25c802bbd04956a3f2355000976cc3^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> callReplaceIniClient("idDoc", workflowInstanceId));
    }

    @Test
    @DisplayName("errorUpdateTest")
    void errorUpdateTest() {
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> callUpdateIniClient(new Gson().fromJson(TestConstants.TEST_UPDATE_REQ_WITH_BODY, MergedMetadatiRequestDTO.class)));
    }
}
