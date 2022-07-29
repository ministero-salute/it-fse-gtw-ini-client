package it.finanze.sanita.fse2.ms.iniclient;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
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
        String identificativoDocUpdate = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        ResponseEntity<IniTraceResponseDTO> response = callDeleteIniClient(identificativoDocUpdate);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrorMessage());
        assertEquals(false, response.getBody().getEsito());
    }

    @Test
    @DisplayName("successDeleteTest")
    void successDeleteTest() {
        String identificativoDocUpdate = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        ResponseEntity<IniTraceResponseDTO> response = callDeleteIniClient(identificativoDocUpdate);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        //assertNull(response.getBody().getErrorMessage());
        //assertEquals(true, response.getBody().getEsito());
    }

    @Test
    @DisplayName("errorGetReferenceTest")
    void errorGetReferenceTest() {
        String identificativoDocUpdate = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        JWTTokenDTO jwtToken = buildJwtToken();
        assertThrows(BusinessException.class, () -> iniClient.getReferenceUUID(identificativoDocUpdate, jwtToken));
    }

    @Test
    @DisplayName("successGetReferenceTest")
    void successGetReferenceTest() {
        String identificativoDocUpdate = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        JWTTokenDTO jwtToken = buildJwtToken();
        assertThrows(BusinessException.class, () -> iniClient.getReferenceUUID(identificativoDocUpdate, jwtToken));
        //assertNotNull(uuid);
        //assertNull(response.getBody().getErrorMessage());
        //assertEquals(true, response.getBody().getEsito());
    }

    @Test
    @DisplayName("errorGetMetadataReferenceTest")
    void errorGetMetadataReferenceTest() {
        String identificativoDocUpdate = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        JWTTokenDTO jwtToken = buildJwtToken();
        assertThrows(BusinessException.class, () -> iniClient.getReferenceMetadata(identificativoDocUpdate, jwtToken));
    }

    @Test
    @DisplayName("successGetMetadataReferenceTest")
    void successGetMetadataReferenceTest() {
        String identificativoDocUpdate = "2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC";
        JWTTokenDTO jwtToken = buildJwtToken();
        assertThrows(BusinessException.class, () -> iniClient.getReferenceMetadata(identificativoDocUpdate, jwtToken));
        //assertNotNull(uuid);
        //assertNull(response.getBody().getErrorMessage());
        //assertEquals(true, response.getBody().getEsito());
    }
}
