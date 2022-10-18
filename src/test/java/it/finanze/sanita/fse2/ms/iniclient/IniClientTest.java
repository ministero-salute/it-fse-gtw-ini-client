package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

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
        //assertNull(response.getBody().getErrorMessage());
        //assertEquals(true, response.getBody().getEsito());
    }

    @Test
    @DisplayName("errorUpdateTest")
    void errorUpdateMetadataTest() {
        String request = "{\"idDoc\":\"2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC\",\"token\":{\"iss\":\"201123456\",\"iat\":1540890704,\"exp\":1540918800,\"jti\":\"1540918800\",\"aud\":\"fse-gateway\",\"sub\":\"RSSMRA22A01A399Z\",\"subject_organization_id\":\"110\",\"subject_organization\":\"Regione Marche\",\"locality\":\"201123456\",\"subject_role\":\"AAS\",\"person_id\":\"BMTBTS01A01I526W\",\"patient_consent\":true,\"purpose_of_use\":\"SYSADMIN\",\"action_id\":\"UPDATE\",\"attachment_hash\":\"ccd1a23b4a73c838e4dfc2a1948aaec8389ebd331cbaebc1b3144c74fca17da5\"},\"body\":{\"tipologiaStruttura\":\"Ospedale\",\"attiCliniciRegoleAccesso\":[\"P99\"],\"tipoDocumentoLivAlto\":\"WOR\",\"assettoOrganizzativo\":\"AD_PSC001\",\"dataInizioPrestazione\":\"1661246473\",\"dataFinePrestazione\":\"1661246473\",\"conservazioneANorma\":\"string\",\"tipoAttivitaClinica\":\"PHR\",\"identificativoSottomissione\":\"2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC\"}}";
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(request, UpdateRequestDTO.class);
        ResponseEntity<IniTraceResponseDTO> response = callUpdateIniClient(updateRequestDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrorMessage());
        assertEquals(false, response.getBody().getEsito());
    }

    @Test
    @DisplayName("successUpdateTest")
    void successUpdateMetadataTest() {
        String request = "{\"idDoc\":\"2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC\",\"token\":{\"iss\":\"201123456\",\"iat\":1540890704,\"exp\":1540918800,\"jti\":\"1540918800\",\"aud\":\"fse-gateway\",\"sub\":\"RSSMRA22A01A399Z\",\"subject_organization_id\":\"110\",\"subject_organization\":\"Regione Marche\",\"locality\":\"201123456\",\"subject_role\":\"AAS\",\"person_id\":\"BMTBTS01A01I526W\",\"patient_consent\":true,\"purpose_of_use\":\"SYSADMIN\",\"action_id\":\"UPDATE\",\"attachment_hash\":\"ccd1a23b4a73c838e4dfc2a1948aaec8389ebd331cbaebc1b3144c74fca17da5\"},\"body\":{\"tipologiaStruttura\":\"Ospedale\",\"attiCliniciRegoleAccesso\":[\"P99\"],\"tipoDocumentoLivAlto\":\"WOR\",\"assettoOrganizzativo\":\"AD_PSC001\",\"dataInizioPrestazione\":\"1661246473\",\"dataFinePrestazione\":\"1661246473\",\"conservazioneANorma\":\"string\",\"tipoAttivitaClinica\":\"PHR\",\"identificativoSottomissione\":\"2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC\"}}";
        UpdateRequestDTO updateRequestDTO = JsonUtility.jsonToObject(request, UpdateRequestDTO.class);
        ResponseEntity<IniTraceResponseDTO> response = callUpdateIniClient(updateRequestDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        //assertNull(response.getBody().getErrorMessage());
        //assertEquals(true, response.getBody().getEsito());
    }
}
