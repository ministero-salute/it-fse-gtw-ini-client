package it.finanze.sanita.fse2.ms.iniclient;

import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.controller.impl.IniOperationCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.AuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.IniInvocationMockedSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.IniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.IssuerSRV;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Arrays;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.Profile.TEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST)
@AutoConfigureMockMvc
class IniClientCTLTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IniCFG iniCFG;

    @MockBean
    private IniInvocationSRV iniInvocationSRV;

    @MockBean
    private IssuerSRV issuerSRV;

    @MockBean
    private IniInvocationMockedSRV iniMockInvocationSRV;

    @MockBean
    private AuditIniSrv auditIniSrv;

    @Autowired
    private IniOperationCTL controller;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetReference_nonMocked() throws Exception {
        // Given
        when(iniCFG.isMockEnable()).thenReturn(false);
        GetReferenceResponseDTO mockResponse = new GetReferenceResponseDTO();
        when(iniInvocationSRV.getReference(eq("DOC123"), any(JWTTokenDTO.class), eq("WII123"))).thenReturn(mockResponse);

        // The request payload
        GetReferenceReqDTO req = new GetReferenceReqDTO();
        JWTPayloadDTO token = new JWTPayloadDTO();
        token.setIss("REALISS");
        req.setToken(token);
        req.setWorkflowInstanceId("WII123");

        mockMvc.perform(post("/v1/get-reference/DOC123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":{\"iss\":\"REALISS\"},\"workflowInstanceId\":\"WII123\"}"))
                .andExpect(status().isOk());

    }

    @Test
    void testGetReference_mockedAndNotIssuserMocked() throws Exception {
        // Given
        when(iniCFG.isMockEnable()).thenReturn(true);
        when(issuerSRV.isMocked("REALISS")).thenReturn(false);
        GetReferenceResponseDTO mockResponse = new GetReferenceResponseDTO();
        when(iniInvocationSRV.getReference(eq("DOC123"), any(JWTTokenDTO.class), eq("WII123"))).thenReturn(mockResponse);

        // Request payload
        GetReferenceReqDTO req = new GetReferenceReqDTO();
        JWTPayloadDTO token = new JWTPayloadDTO();
        token.setIss("REALISS");
        req.setToken(token);
        req.setWorkflowInstanceId("WII123");

        mockMvc.perform(post("/v1/get-reference/DOC123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":{\"iss\":\"REALISS\"},\"workflowInstanceId\":\"WII123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetReference_mockedAndIssuserMocked() throws Exception {
        // Given
        when(iniCFG.isMockEnable()).thenReturn(true);
        when(issuerSRV.isMocked("MOCKEDISS")).thenReturn(true);
        GetReferenceResponseDTO mockResponse = new GetReferenceResponseDTO();
        when(iniMockInvocationSRV.getReference(eq("DOC123"), any(JWTTokenDTO.class))).thenReturn(mockResponse);

        // Request payload
        GetReferenceReqDTO req = new GetReferenceReqDTO();
        JWTPayloadDTO token = new JWTPayloadDTO();
        token.setIss("MOCKEDISS");
        req.setToken(token);
        req.setWorkflowInstanceId("WII123");

        mockMvc.perform(post("/v1/get-reference/DOC123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":{\"iss\":\"MOCKEDISS\"},\"workflowInstanceId\":\"WII123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMergedMetadati_nonMocked() throws Exception {
        when(iniCFG.isMockEnable()).thenReturn(false);

        MergedMetadatiRequestDTO requestBody = new MergedMetadatiRequestDTO();
        requestBody.setIdDoc("DOC456");
        JWTPayloadDTO token = new JWTPayloadDTO();
        token.setIss("REALISS");
        requestBody.setToken(token);

        GetMergedMetadatiDTO mergedMetadati = new GetMergedMetadatiDTO();
        mergedMetadati.setErrorMessage(null);
        mergedMetadati.setMarshallResponse("MARSHALL");
        mergedMetadati.setDocumentType("DOC_TYPE");
        mergedMetadati.setAuthorInstitution("AUTH_INST");
        mergedMetadati.setAdministrativeRequest(Arrays.asList("REQ1"));

        when(iniInvocationSRV.getMergedMetadati(eq("DOC456"), any(MergedMetadatiRequestDTO.class))).thenReturn(mergedMetadati);

        mockMvc.perform(put("/v1/get-merged-metadati")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idDoc\":\"DOC456\",\"token\":{\"iss\":\"REALISS\"},\"body\":{}}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMergedMetadati_mockedAndNotIssuserMocked() throws Exception {
        when(iniCFG.isMockEnable()).thenReturn(true);
        when(issuerSRV.isMocked("REALISS")).thenReturn(false);

        MergedMetadatiRequestDTO requestBody = new MergedMetadatiRequestDTO();
        requestBody.setIdDoc("DOC456");
        JWTPayloadDTO token = new JWTPayloadDTO();
        token.setIss("REALISS");
        requestBody.setToken(token);

        GetMergedMetadatiDTO mergedMetadati = new GetMergedMetadatiDTO();
        mergedMetadati.setErrorMessage(null);
        mergedMetadati.setMarshallResponse("MARSHALL");
        mergedMetadati.setDocumentType("DOC_TYPE");
        mergedMetadati.setAuthorInstitution("AUTH_INST");
        mergedMetadati.setAdministrativeRequest(Arrays.asList("REQ1"));

        when(iniInvocationSRV.getMergedMetadati(eq("DOC456"), any(MergedMetadatiRequestDTO.class))).thenReturn(mergedMetadati);

        mockMvc.perform(put("/v1/get-merged-metadati")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idDoc\":\"DOC456\",\"token\":{\"iss\":\"REALISS\"},\"body\":{}}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMergedMetadati_mockedAndIssuserMocked() throws Exception {
        when(iniCFG.isMockEnable()).thenReturn(true);
        when(issuerSRV.isMocked("MOCKISS")).thenReturn(true);

        MergedMetadatiRequestDTO requestBody = new MergedMetadatiRequestDTO();
        requestBody.setIdDoc("DOC456");
        JWTPayloadDTO token = new JWTPayloadDTO();
        token.setIss("MOCKISS");
        requestBody.setToken(token);

        GetMergedMetadatiDTO mergedMetadati = new GetMergedMetadatiDTO();
        mergedMetadati.setErrorMessage("ERROR");
        mergedMetadati.setMarshallResponse("MARSHALL");
        mergedMetadati.setDocumentType("DOC_TYPE");
        mergedMetadati.setAuthorInstitution("AUTH_INST");
        mergedMetadati.setAdministrativeRequest(Arrays.asList("REQ1"));

        when(iniMockInvocationSRV.getMergedMetadati(eq("DOC456"), any(MergedMetadatiRequestDTO.class))).thenReturn(mergedMetadati);

        mockMvc.perform(put("/v1/get-merged-metadati")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idDoc\":\"DOC456\",\"token\":{\"iss\":\"MOCKISS\"},\"body\":{}}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMetadatiPostCrash_documentFound() throws Exception {
        // Mocking AdhocQueryResponse with a found document
        AdhocQueryResponse response = new AdhocQueryResponse();

        // Build extrinsic object with slots & classifications
        ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();

        SlotType1 repoSlot = new SlotType1();
        repoSlot.setName("repositoryUniqueId");
        repoSlot.setValueList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType());
        repoSlot.getValueList().getValue().add("REPO_ID");

        SlotType1 adminSlot = new SlotType1();
        adminSlot.setName("urn:ita:2022:administrativeRequest");
        adminSlot.setValueList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType());
        adminSlot.getValueList().getValue().add("ADMIN_REQ");

        extrinsicObject.getSlot().add(repoSlot);
        extrinsicObject.getSlot().add(adminSlot);

        ClassificationType classType = new ClassificationType();
        classType.setId("ClassCodeId_1");
        classType.setNodeRepresentation("DOC_ALTO_LIV");
        extrinsicObject.getClassification().add(classType);

        // Prepare identifiable list
        JAXBElement<IdentifiableType> el = new JAXBElement<>(new javax.xml.namespace.QName("ExtrinsicObject"), IdentifiableType.class, extrinsicObject);
        response.setRegistryObjectList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType());
        response.getRegistryObjectList().getIdentifiable().add(el);

        when(iniInvocationSRV.getMetadata(eq("DOC789"), any(JWTTokenDTO.class))).thenReturn(response);

        GetMetadatiReqDTO jwtPayload = new GetMetadatiReqDTO();
        jwtPayload.setIss("REALISS");
        jwtPayload.setSub("SUB");
        jwtPayload.setSubject_organization_id("ORG_ID");
        jwtPayload.setSubject_organization("ORG");
        jwtPayload.setLocality("LOCALITY");
        jwtPayload.setSubject_role("ROLE");
        jwtPayload.setPerson_id("PERSON");
        jwtPayload.setPatient_consent(true);
        jwtPayload.setPurpose_of_use("USE");
        jwtPayload.setResource_hl7_type("HL7");
        jwtPayload.setAction_id("READ");
        jwtPayload.setSubject_application_id("APP_ID");
        jwtPayload.setSubject_application_version("1.0");
        jwtPayload.setSubject_application_vendor("VENDOR");

        mockMvc.perform(post("/v1/get-metadati-post-crash/DOC789")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"iss\":\"REALISS\", \"sub\":\"SUB\",\"subject_organization_id\":\"ORG_ID\","
                                + "\"subject_organization\":\"ORG\",\"locality\":\"LOCALITY\",\"subject_role\":\"ROLE\","
                                + "\"person_id\":\"PERSON\",\"patient_consent\":true,\"purpose_of_use\":\"USE\","
                                + "\"resource_hl7_type\":\"HL7\",\"action_id\":\"READ\","
                                + "\"subject_application_id\":\"APP_ID\",\"subject_application_version\":\"1.0\","
                                + "\"subject_application_vendor\":\"VENDOR\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMetadatiPostCrash_documentNotFound() throws Exception {
        AdhocQueryResponse response = new AdhocQueryResponse();
        RegistryErrorList errorList = new RegistryErrorList();
        RegistryError error = new RegistryError();
        error.setCodeContext("No results from the query");
        errorList.getRegistryError().add(error);
        response.setRegistryErrorList(errorList);

        when(iniInvocationSRV.getMetadata(eq("DOC789"), any(JWTTokenDTO.class))).thenReturn(response);

        GetMetadatiReqDTO jwtPayload = new GetMetadatiReqDTO();
        jwtPayload.setIss("REALISS");
        jwtPayload.setSub("SUB");
        jwtPayload.setSubject_organization_id("ORG_ID");
        jwtPayload.setSubject_organization("ORG");
        jwtPayload.setLocality("LOCALITY");
        jwtPayload.setSubject_role("ROLE");
        jwtPayload.setPerson_id("PERSON");
        jwtPayload.setPatient_consent(true);
        jwtPayload.setPurpose_of_use("USE");
        jwtPayload.setResource_hl7_type("HL7");
        jwtPayload.setAction_id("READ");
        jwtPayload.setSubject_application_id("APP_ID");
        jwtPayload.setSubject_application_version("1.0");
        jwtPayload.setSubject_application_vendor("VENDOR");

        mockMvc.perform(post("/v1/get-metadati-post-crash/DOC789")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"iss\":\"REALISS\", \"sub\":\"SUB\",\"subject_organization_id\":\"ORG_ID\","
                                + "\"subject_organization\":\"ORG\",\"locality\":\"LOCALITY\",\"subject_role\":\"ROLE\","
                                + "\"person_id\":\"PERSON\",\"patient_consent\":true,\"purpose_of_use\":\"USE\","
                                + "\"resource_hl7_type\":\"HL7\",\"action_id\":\"READ\","
                                + "\"subject_application_id\":\"APP_ID\",\"subject_application_version\":\"1.0\","
                                + "\"subject_application_vendor\":\"VENDOR\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testBuildFromAdhocQueryRes_allConditions() throws Exception {
        // Mocking AdhocQueryResponse
        AdhocQueryResponse response = new AdhocQueryResponse();

        // Build ExtrinsicObjectType with all required slots
        ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();

        // Slots
        SlotType1 repoSlot = new SlotType1();
        repoSlot.setName("repositoryUniqueId");
        repoSlot.setValueList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType());
        repoSlot.getValueList().getValue().add("REPO_ID");

        SlotType1 adminSlot = new SlotType1();
        adminSlot.setName("urn:ita:2022:administrativeRequest");
        adminSlot.setValueList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType());
        adminSlot.getValueList().getValue().add("ADMIN_REQ");

        SlotType1 startTimeSlot = new SlotType1();
        startTimeSlot.setName("serviceStartTime");
        startTimeSlot.setValueList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType());
        startTimeSlot.getValueList().getValue().add("2023-01-01T10:00:00");

        SlotType1 stopTimeSlot = new SlotType1();
        stopTimeSlot.setName("serviceStopTime");
        stopTimeSlot.setValueList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType());
        stopTimeSlot.getValueList().getValue().add("2023-01-01T12:00:00");

        SlotType1 repositoryTypeSlot = new SlotType1();
        repositoryTypeSlot.setName("urn:ita:2017:repository-type");
        repositoryTypeSlot.setValueList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType());
        repositoryTypeSlot.getValueList().getValue().add("CONSERVATION");

        SlotType1 descriptionSlot = new SlotType1();
        descriptionSlot.setName("urn:ita:2022:urn:ita:2022:description");
        descriptionSlot.setValueList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType());
        descriptionSlot.getValueList().getValue().add("DESCRIPTION_1");
        descriptionSlot.getValueList().getValue().add("DESCRIPTION_2");

        extrinsicObject.getSlot().add(repoSlot);
        extrinsicObject.getSlot().add(adminSlot);
        extrinsicObject.getSlot().add(startTimeSlot);
        extrinsicObject.getSlot().add(stopTimeSlot);
        extrinsicObject.getSlot().add(repositoryTypeSlot);
        extrinsicObject.getSlot().add(descriptionSlot);

        // Classifications
        ClassificationType classType1 = new ClassificationType();
        classType1.setId("ClassCodeId_1");
        classType1.setNodeRepresentation("DOC_ALTO_LIV");

        ClassificationType classType2 = new ClassificationType();
        classType2.setId("healthcareFacilityTypeCode_1");
        classType2.setNodeRepresentation("HEALTH_FACILITY");

        ClassificationType classType3 = new ClassificationType();
        classType3.setId("practiceSettingCode_1");
        classType3.setNodeRepresentation("PRACTICE_SETTING");

        ClassificationType classType4 = new ClassificationType();
        classType4.setId("EventCodeList_1_1");
        classType4.setNodeRepresentation("EVENT_CODE");

        extrinsicObject.getClassification().add(classType1);
        extrinsicObject.getClassification().add(classType2);
        extrinsicObject.getClassification().add(classType3);
        extrinsicObject.getClassification().add(classType4);

        // Prepare identifiable list
        JAXBElement<IdentifiableType> el = new JAXBElement<>(new javax.xml.namespace.QName("ExtrinsicObject"), IdentifiableType.class, extrinsicObject);
        response.setRegistryObjectList(new oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType());
        response.getRegistryObjectList().getIdentifiable().add(el);

        // Mocking iniInvocationSRV.getMetadata
        when(iniInvocationSRV.getMetadata(eq("DOC789"), any(JWTTokenDTO.class))).thenReturn(response);

        // Create Request DTO
        GetMetadatiReqDTO jwtPayload = new GetMetadatiReqDTO();
        jwtPayload.setIss("REALISS");
        jwtPayload.setSub("SUB");
        jwtPayload.setSubject_organization_id("ORG_ID");
        jwtPayload.setSubject_organization("ORG");
        jwtPayload.setLocality("LOCALITY");
        jwtPayload.setSubject_role("ROLE");
        jwtPayload.setPerson_id("PERSON");
        jwtPayload.setPatient_consent(true);
        jwtPayload.setPurpose_of_use("USE");
        jwtPayload.setResource_hl7_type("HL7");
        jwtPayload.setAction_id("READ");
        jwtPayload.setSubject_application_id("APP_ID");
        jwtPayload.setSubject_application_version("1.0");
        jwtPayload.setSubject_application_vendor("VENDOR");

        // Perform MockMvc request
        mockMvc.perform(post("/v1/get-metadati-post-crash/DOC789")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"iss\":\"REALISS\", \"sub\":\"SUB\",\"subject_organization_id\":\"ORG_ID\","
                                + "\"subject_organization\":\"ORG\",\"locality\":\"LOCALITY\",\"subject_role\":\"ROLE\","
                                + "\"person_id\":\"PERSON\",\"patient_consent\":true,\"purpose_of_use\":\"USE\","
                                + "\"resource_hl7_type\":\"HL7\",\"action_id\":\"READ\","
                                + "\"subject_application_id\":\"APP_ID\",\"subject_application_version\":\"1.0\","
                                + "\"subject_application_vendor\":\"VENDOR\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetEventByWii() throws Exception {
        IniAuditsDto mockAudit = new IniAuditsDto();
        mockAudit.setAudit(new ArrayList<>());
        when(auditIniSrv.findByWii("WII123")).thenReturn(mockAudit);

        mockMvc.perform(get("/v1/WII123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workflow_instance_id\":\"WII123\"}"))
                .andExpect(status().isOk());
    }

}
