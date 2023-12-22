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

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMetadatiResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;
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

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class IniClientTest extends AbstractTest {

    @Autowired
    private IIniClient iniClient;

    @MockBean
    private IIniInvocationSRV iniInvocationSRV;

    @Test
    @DisplayName("errorPublishTest")
    void errorPublishTest() {
        IniResponseDTO mockResponse = new IniResponseDTO();
        mockResponse.setEsito(false);
        mockResponse.setErrorMessage("error");
        Mockito.when(iniInvocationSRV.publishOrReplaceOnIni(anyString(), any(ProcessorOperationEnum.class)))
                .thenReturn(mockResponse);
        String workflowInstanceId = "2.16.840.1.113883.2.9.2.120.4.4.030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J.4e25c802bbd04956a3f2355000976cc3^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        ResponseEntity<IniTraceResponseDTO> response = callPublishIniClient(workflowInstanceId);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrorMessage());
        assertEquals(false, response.getBody().getEsito());
    }

    @Test
    @DisplayName("successPublishTest")
    void successPublishTest() {
        IniResponseDTO mockResponse = new IniResponseDTO();
        mockResponse.setEsito(true);
        Mockito.when(iniInvocationSRV.publishOrReplaceOnIni(anyString(), any(ProcessorOperationEnum.class)))
                .thenReturn(mockResponse);
        String workflowInstanceId = "2.16.840.1.113883.2.9.2.120.4.4.030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J.4e25c802bbd04956a3f2355000976cc3^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        ResponseEntity<IniTraceResponseDTO> response = callPublishIniClient(workflowInstanceId);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("errorDeleteTest")
    void errorDeleteTest() {
        IniResponseDTO mockResponse = new IniResponseDTO();
        mockResponse.setEsito(false);
        mockResponse.setErrorMessage("error");
        Mockito.when(iniInvocationSRV.deleteByDocumentId(any(DeleteRequestDTO.class)))
                .thenReturn(mockResponse);
        String identificativoDelete = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        ResponseEntity<IniTraceResponseDTO> response = callDeleteIniClient(identificativoDelete);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrorMessage());
        assertEquals(false, response.getBody().getEsito());
    }

    @Test
    @DisplayName("successDeleteTest")
    void successDeleteTest() {
        IniResponseDTO mockResponse = new IniResponseDTO();
        mockResponse.setEsito(true);
        Mockito.when(iniInvocationSRV.deleteByDocumentId(any(DeleteRequestDTO.class)))
                .thenReturn(mockResponse);
        String identificativoDelete = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        ResponseEntity<IniTraceResponseDTO> response = callDeleteIniClient(identificativoDelete);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("errorGetMetadataTest")
    void errorGetMetadataTest() {
        Mockito.when(iniInvocationSRV.getMetadata(any(), any()))
                .thenReturn(null);
        String idDoc = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        ResponseEntity<GetMetadatiResponseDTO> response = callGetMetadata(idDoc);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getResponse());
    }

    @Test
    @DisplayName("errorReplaceTest")
    void errorReplaceTest() {
        IniResponseDTO mockResponse = new IniResponseDTO();
        mockResponse.setEsito(false);
        mockResponse.setErrorMessage("error");
        Mockito.when(iniInvocationSRV.publishOrReplaceOnIni(anyString(), any(ProcessorOperationEnum.class)))
                .thenReturn(mockResponse);
        String workflowInstanceId = "2.16.840.1.113883.2.9.2.120.4.4.030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J.4e25c802bbd04956a3f2355000976cc3^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        ResponseEntity<IniTraceResponseDTO> response = callReplaceIniClient("idDoc", workflowInstanceId);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrorMessage());
        assertEquals(false, response.getBody().getEsito());
    }

    @Test
    @DisplayName("successReplaceTest")
    void successReplaceTest() {
        IniResponseDTO mockResponse = new IniResponseDTO();
        mockResponse.setEsito(true);
        Mockito.when(iniInvocationSRV.publishOrReplaceOnIni(anyString(), any(ProcessorOperationEnum.class)))
                .thenReturn(mockResponse);
        String workflowInstanceId = "2.16.840.1.113883.2.9.2.120.4.4.030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J.4e25c802bbd04956a3f2355000976cc3^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        ResponseEntity<IniTraceResponseDTO> response = callReplaceIniClient("idDoc", workflowInstanceId);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
 
 
}
