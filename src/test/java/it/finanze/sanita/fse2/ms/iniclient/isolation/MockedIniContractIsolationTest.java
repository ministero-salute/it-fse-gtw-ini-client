/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIniInvocationRepo;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.IniInvocationMockedSRV;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(IniInvocationMockedSRV.class)
class MockedIniContractIsolationTest {
    @Autowired private IniInvocationMockedSRV service;
    @MockitoBean private LoggerHelper logger;
    @MockitoBean private IIniInvocationRepo repository;

    @ParameterizedTest @ValueSource(strings = {"TRUE", "FALSE"})
    void mockedMetadataPreservesActualEdsPublicationDecision(String edsPublished) {
        var metadata = service.getDocumentMetadata("oid", new JWTTokenDTO(), "workflow", edsPublished);
        assertEquals(edsPublished, metadata.getEdsPublished());
        assertEquals("MOCK_UUID", metadata.getUuid());
        assertNotNull(metadata.getMetadata());
        assertFalse(metadata.getAdministrativeRequest().isEmpty());
    }

    @Test void recognizedAffinityDomainBuildsParseableCreationTimeResponse() throws Exception {
        var result = service.getMergedMetadati("AD-2.1.document", new MergedMetadatiRequestDTO(), "FALSE");
        var parsed = (AdhocQueryResponse) JAXBContext.newInstance(AdhocQueryResponse.class).createUnmarshaller()
                .unmarshal(new StringReader(result.getMarshallResponse()));
        var entry = (ExtrinsicObjectType) parsed.getRegistryObjectList().getIdentifiable().get(0).getValue();
        assertTrue(entry.getId().startsWith("urn:uuid:"));
        assertEquals("creationTime", entry.getSlot().get(0).getName());
        assertEquals("20200531100000+0100", entry.getSlot().get(0).getValueList().getValue().get(0));
        assertEquals("FALSE", result.getEdsPublished());
    }

    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = "unrecognized")
    void unknownDocumentPatternDoesNotFabricateMergedMetadata(String oid) {
        var result = service.getMergedMetadati(oid, new MergedMetadatiRequestDTO(), "TRUE");
        assertNull(result.getMarshallResponse());
        assertEquals("TRUE", result.getEdsPublished());
    }

    @Test void mockWritesReturnSuccessWithoutRepositoryOrExternalIni() {
        assertTrue(service.publishOrReplaceOnIni("workflow", ProcessorOperationEnum.PUBLISH, "FALSE").getEsito());
        assertTrue(service.deleteByDocumentId(new DeleteRequestDTO()).getEsito());
        assertTrue(service.updateByRequestBody(null, new UpdateRequestDTO()).getEsito());
        assertTrue(service.updateOscuramentoByRequestBody(new UpdateOscuramentoRequestDTO()).getMessage().contains("R220"));
        assertFalse(service.getReference("oid", new JWTTokenDTO()).getUuid().isEmpty());
        assertTrue(service.getMetadata("oid", new JWTTokenDTO()).getRequestId().contains("mock"));
        org.mockito.Mockito.verifyNoInteractions(repository);
    }
}
