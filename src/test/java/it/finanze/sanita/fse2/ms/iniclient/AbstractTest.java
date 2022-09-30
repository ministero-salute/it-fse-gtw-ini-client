package it.finanze.sanita.fse2.ms.iniclient;

import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    @Autowired
    RestTemplate restTemplate;

    /**
     * Init collection
     */
    @BeforeAll
    void init() {
        List<IniEdsInvocationETY> transactionEventsETYList = initList();
        mongoTemplate.insertAll(transactionEventsETYList);
    }

    /**
     * call publish ini client
     * @param workflowInstanceId of the transaction
     */
    ResponseEntity<IniTraceResponseDTO> callPublishIniClient(String workflowInstanceId) {
        String url = "http://localhost:" +
                webServerAppCtxt.getWebServer().getPort() +
                webServerAppCtxt.getServletContext().getContextPath() +
                "/v1/ini-publish";
        return restTemplate.postForEntity(url, workflowInstanceId, IniTraceResponseDTO.class);
    }

    /**
     * call delete method on ini
     * @param workflowInstanceId of the transaction
     */
    ResponseEntity<IniTraceResponseDTO> callDeleteIniClient(String workflowInstanceId) {
        String url = "http://localhost:" +
                webServerAppCtxt.getWebServer().getPort() +
                webServerAppCtxt.getServletContext().getContextPath() +
                "/v1/ini-delete";
        String requestString = "{\"idDoc\":\"2.16.840.1.113883.2.9.2.90.4.4^090A02205783394_PRESPEC\",\"iss\":\"201123456\",\"sub\":\"RSSMRA22A01A399Z\",\"subject_organization_id\":\"110\",\"subject_organization\":\"Regione Marche\",\"locality\":\"201123456\",\"subject_role\":\"AAS\",\"person_id\":\"BMTBTS01A01I526W\",\"patient_consent\":true,\"purpose_of_use\":\"SYSADMIN\",\"action_id\":\"DELETE\"}";
        DeleteRequestDTO requestBody = JsonUtility.jsonToObject(requestString, DeleteRequestDTO.class);
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, null);

        return restTemplate.exchange(url, HttpMethod.DELETE, entity, IniTraceResponseDTO.class);
    }

    @SuppressWarnings("unchecked")
    protected List<IniEdsInvocationETY> initList() {
        String start = "{\"_id\":\"<TO_REPLACE>\",\"workflow_instance_id\":\"2.16.840.1.113883.2.9.2.120.4.4.030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J.4e25c802bbd04956a3f2355000976cc3^^^^urn:ihe:iti:xdw:2013:workflowInstanceId\"}";
        String dataString = "{\"resourceType\":\"DocumentReference\",\"masterIdentifier\":{\"id\":\"030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J\"},\"identifier\":[{\"id\":\"Document00\"}],\"status\":\"current\",\"type\":{\"coding\":[{\"code\":\"11502-2\"}]},\"category\":[{\"coding\":[{\"code\":\"WOR\"}]}],\"subject\":{\"reference\":\"NGNVCN92S19L259C\"},\"date\":\"2022-06-10T20:24:08.444+02:00\",\"author\":[{\"reference\":\"MTCORV58E63L294G\"}],\"authenticator\":{\"reference\":\"GPSDGK80E76C765V\"},\"custodian\":{\"reference\":\"120148\"},\"securityLabel\":[{\"coding\":[{\"code\":\"N\"}]}],\"content\":[{\"attachment\":{\"contentType\":\"application/pdf\",\"language\":\"it-IT\",\"url\":\"string\",\"size\":57590,\"hash\":\"Y2NkMWEyM2I0YTczYzgzOGU0ZGZjMmExOTQ4YWFlYzgzODllYmQzMzFjYmFlYmMxYjMxNDRjNzRmY2ExN2RhNQ==\"},\"format\":{\"code\":\"2.16.840.1.113883.2.9.10.1.1\"}}],\"context\":{\"event\":[{\"coding\":[{\"code\":\"P99\"}]}],\"period\":{\"start\":\"1654-04-07T01:31:07+01:00\",\"end\":\"1654-04-07T01:31:07+01:00\"},\"facilityType\":{\"coding\":[{\"code\":\"Ospedale\"}]},\"practiceSetting\":{\"coding\":[{\"code\":\"Allergologia\"}]},\"sourcePatientInfo\":{\"reference\":\"NGNVCN92S19L259C\"},\"related\":[{\"reference\":\"[NRE]\"}]}}";
        String metadataString = "[{\"submissionSetEntry\":{\"submissionTime\":\"20220617165100\",\"sourceId\":\"2.16.840.1.113883.2.9.2.80\",\"contentTypeCode\":\"PHR\",\"contentTypeCodeName\":\"Personal Health Record Update\",\"uniqueID\":\"string\"}},{\"documentEntry\":{\"mimeType\":\"application/pdf+text/x-cda-r2+xml\",\"entryUUID\":\"Document01\",\"creationTime\":\"20220617164426\",\"hash\":\"ccd1a23b4a73c838e4dfc2a1948aaec8389ebd331cbaebc1b3144c74fca17da5\",\"size\":57590,\"status\":\"approved\",\"languageCode\":\"it-IT\",\"patientId\":\"120148\",\"confidentialityCode\":\"N\",\"confidentialityCodeDisplayName\":\"Normal\",\"typeCode\":\"11502-2\",\"typeCodeName\":\"Referto di laboratorio\",\"formatCode\":\"2.16.840.1.113883.2.9.10.1.1\",\"formatCodeName\":\"Referto di Laboratorio\",\"legalAuthenticator\":\"GPSDGK80E76C765V\",\"sourcePatientInfo\":\"100 120 RM Roma 058091 00187 Via Aurora 12 100 120 RM Roma 058091 00138 Via Canevari 12B Verdi Giuseppe 100 120 RM Roma 058091\",\"author\":\"\",\"representedOrganizationName\":\"Nuovo Ospedale S.Agostino (MO)\",\"representedOrganizationCode\":\"080105\",\"uniqueId\":\"2.16.840.1.113883.2.9.2.120.4.4^030702.TSTSMN63A01F205H.20220325112426.OQlvTq1J\",\"referenceIdList\":[\"[NRE]\"],\"healthcareFacilityTypeCode\":\"Ospedale\",\"healthcareFacilityTypeCodeName\":\"Ospedale\",\"eventCodeList\":[\"P99\"],\"repositoryUniqueId\":\"2.16.840.1.113883.2.9.2.80.4.5.999\",\"classCode\":\"WOR\",\"classCodeName\":\"Documento di workflow\",\"practiceSettingCode\":\"AD_PSC001\",\"practiceSettingCodeName\":\"Allergologia\",\"sourcePatientId\":\"NGNVCN92S19L259C\",\"serviceStartTime\":\"20220617164426\",\"serviceStopTime\":\"20220617164426\"}},{\"tokenEntry\":{\"header\":{\"alg\":\"RS256\",\"typ\":\"JWT\",\"x5c\":\"X5C cert base 64\"},\"payload\":{\"iss\":\"201123456\",\"iat\":1540890704,\"exp\":1540918800,\"jti\":\"1540918800\",\"aud\":\"fse-gateway\",\"sub\":\"RSSMRA22A01A399Z\",\"subject_organization_id\":\"110\",\"subject_organization\":\"Regione Marche\",\"locality\":\"201123456\",\"subject_role\":\"AAS\",\"person_id\":\"BMTBTS01A01I526W\",\"patient_consent\":true,\"purpose_of_use\":\"TREATMENT\",\"action_id\":\"CREATE\",\"attachment_hash\":\"ccd1a23b4a73c838e4dfc2a1948aaec8389ebd331cbaebc1b3144c74fca17da5\"}}}]";
        start = start.replace("<TO_REPLACE>", UUID.randomUUID().toString());
        List<IniEdsInvocationETY> list = new ArrayList<>();
        Document documentId = JsonUtility.jsonToObject(start, Document.class);
        Document data = JsonUtility.jsonToObject(dataString, Document.class);
        List<Document> metadata = JsonUtility.jsonToObject(metadataString, List.class);

        String id = (String) documentId.get("_id");
        String workflowInstanceId = (String) documentId.get("workflow_instance_id");

        IniEdsInvocationETY iniEdsInvocationETY = new IniEdsInvocationETY();
        iniEdsInvocationETY.setId(id);
        iniEdsInvocationETY.setWorkflowInstanceId(workflowInstanceId);
        iniEdsInvocationETY.setData(data);
        iniEdsInvocationETY.setMetadata(metadata);

        list.add(iniEdsInvocationETY);
        return list;
    }

    protected JWTTokenDTO buildJwtToken() {
        JWTTokenDTO jwtToken = new JWTTokenDTO();
        String tokenPayloadStringified = "{\"iss\":\"201123456\",\"iat\":1540890704,\"exp\":1540918800,\"jti\":\"1540918800\",\"aud\":\"fse-gateway\",\"sub\":\"RSSMRA22A01A399Z\",\"subject_organization_id\":\"110\",\"subject_organization\":\"Regione Marche\",\"locality\":\"201123456\",\"subject_role\":\"AAS\",\"person_id\":\"BMTBTS01A01I526W\",\"patient_consent\":true,\"purpose_of_use\":\"SYSADMIN\",\"action_id\":\"CREATE\",\"attachment_hash\":\"ccd1a23b4a73c838e4dfc2a1948aaec8389ebd331cbaebc1b3144c74fca17da5\"}";
        JWTPayloadDTO jwtPayloadDTO = JsonUtility.jsonToObject(tokenPayloadStringified, JWTPayloadDTO.class);
        jwtToken.setPayload(jwtPayloadDTO);
        return jwtToken;
    }

    /**
     * call replace on ini
     * @param idDoc of the transaction
     */
    ResponseEntity<IniTraceResponseDTO> callReplaceIniClient(String idDoc, String workflowInstanceId) {
        String url = "http://localhost:" +
                webServerAppCtxt.getWebServer().getPort() +
                webServerAppCtxt.getServletContext().getContextPath() +
                "/v1/ini-replace";

        String stringObj = "{\"idDoc\":\"<ID_DOC>\",\"workflowInstanceId\":\"<WORKFLOW_ID>\"}";
        stringObj = stringObj
                .replace("<ID_DOC>", idDoc)
                .replace("<WORKFLOW_ID>", workflowInstanceId);
        ReplaceRequestDTO requestDTO = JsonUtility.jsonToObject(stringObj, ReplaceRequestDTO.class);
        HttpEntity<Object> entity = new HttpEntity<>(requestDTO, null);

        return restTemplate.exchange(url, HttpMethod.PUT, entity, IniTraceResponseDTO.class);
    }

    /**
     * call update metadata on ini
     */
    ResponseEntity<IniTraceResponseDTO> callUpdateIniClient(UpdateRequestDTO updateRequestDTO) {
        String url = "http://localhost:" +
                webServerAppCtxt.getWebServer().getPort() +
                webServerAppCtxt.getServletContext().getContextPath() +
                "/v1/ini-update";

        HttpEntity<Object> entity = new HttpEntity<>(updateRequestDTO, null);

        return restTemplate.exchange(url, HttpMethod.PUT, entity, IniTraceResponseDTO.class);
    }

    /**
     * call publish ini client
     * @param oid of the transaction
     */
    ResponseEntity<IniTraceResponseDTO> callGetMetadata(String oid) {
        String url = "http://localhost:" +
                webServerAppCtxt.getWebServer().getPort() +
                webServerAppCtxt.getServletContext().getContextPath() +
                "/v1/get-metadati/" + oid;

        GetMetadatiReqDTO getMetadatiReqDTO = JsonUtility.jsonToObject(TestConstants.TEST_GET_META_REQ, GetMetadatiReqDTO.class);
        HttpEntity<?> entity = new HttpEntity<>(getMetadatiReqDTO, null);
        return restTemplate.exchange(url, HttpMethod.POST, entity, IniTraceResponseDTO.class);
    }
}
