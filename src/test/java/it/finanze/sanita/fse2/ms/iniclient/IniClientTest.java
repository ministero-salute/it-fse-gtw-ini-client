/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class IniClientTest extends AbstractTest {

    @Autowired
    private IIniClient iniClient;

    @Test
    @DisplayName("errorPublishTest")
    void errorPublishTest() {
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
        String workflowInstanceId = "2.16.840.1.113883.2.9.2.120.4.4.030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J.4e25c802bbd04956a3f2355000976cc3^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        ResponseEntity<IniTraceResponseDTO> response = callPublishIniClient(workflowInstanceId);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        //assertNull(response.getBody().getErrorMessage());
        //assertEquals(true, response.getBody().getEsito());
    }

    @Test
    @DisplayName("notFoundPublishErrorTest")
    void notFoundPublishErrorTest() {
        String workflowInstanceId = "notfound";
        ResponseEntity<IniTraceResponseDTO> response = callPublishIniClient(workflowInstanceId);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrorMessage());
        assertEquals(false, response.getBody().getEsito());
    }

    @Test
    @DisplayName("errorDeleteTest")
    void errorDeleteTest() {
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
        String identificativoDelete = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        ResponseEntity<IniTraceResponseDTO> response = callDeleteIniClient(identificativoDelete);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        //assertNull(response.getBody().getErrorMessage());
        //assertEquals(true, response.getBody().getEsito());
    }

    @Test
    @DisplayName("errorGetReferenceTest")
    void errorGetReferenceTest() {
        String idDoc = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        JWTTokenDTO jwtToken = buildJwtToken();
        assertThrows(BusinessException.class, () -> iniClient.getReferenceUUID(idDoc, jwtToken));
    }

    @Test
    @DisplayName("successGetReferenceTest")
    void successGetReferenceTest() {
        String idDoc = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        JWTTokenDTO jwtToken = buildJwtToken();
        assertThrows(BusinessException.class, () -> iniClient.getReferenceUUID(idDoc, jwtToken));
        //assertNotNull(uuid);
        //assertNull(response.getBody().getErrorMessage());
        //assertEquals(true, response.getBody().getEsito());
    }

    @Test
    @DisplayName("errorGetMetadataReferenceTest")
    void errorGetMetadataReferenceTest() {
        String idDoc = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        assertThrows(HttpClientErrorException.Forbidden.class, () -> callGetMetadata(idDoc));
    }

    @Test
    @Disabled("To launch manually")
    @DisplayName("successGetMetadataReferenceTest")
    void successGetMetadataReferenceTest() {
        String idDoc = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        ResponseEntity<IniTraceResponseDTO> response = callGetMetadata(idDoc);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("errorReplaceTest")
    void errorReplaceTest() {
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
        String workflowInstanceId = "2.16.840.1.113883.2.9.2.120.4.4.030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J.4e25c802bbd04956a3f2355000976cc3^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        ResponseEntity<IniTraceResponseDTO> response = callReplaceIniClient("idDoc", workflowInstanceId);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
 
 
}
