/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigInteger;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import java.io.StringReader;
import java.io.StringWriter;
import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import it.finanze.sanita.fse2.ms.iniclient.TestConstants;
import it.finanze.sanita.fse2.ms.iniclient.TestUtility;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.PublishReplaceBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.delete.DeleteBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.read.ReadBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.UpdateBodyBuilderUtility;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;

@Tag("ISOLATION")
class RegistryRequestsIsolationTest {
    @ParameterizedTest @ValueSource(booleans = {false, true})
    void readQueryRestrictsDocumentAndOptionallyAuthor(boolean author) {
        var request = ReadBodyBuilderUtility.buildAdHocQueryRequest("document-oid", "LeafClass", author, "author-id");
        assertEquals("LeafClass", request.getResponseOption().getReturnType());
        assertTrue(request.getResponseOption().isReturnComposedObjects());
        assertFalse(request.isFederated());
        assertEquals(BigInteger.ZERO, request.getStartIndex());
        assertEquals(BigInteger.valueOf(-1), request.getMaxResults());
        assertEquals(author ? 2 : 1, request.getAdhocQuery().getSlot().size());
        assertEquals("$XDSDocumentEntryUniqueId", request.getAdhocQuery().getSlot().get(0).getName());
        assertEquals(List.of("('document-oid')"), request.getAdhocQuery().getSlot().get(0).getValueList().getValue());
        if (author) assertEquals(List.of("('author-id')"), request.getAdhocQuery().getSlot().get(1).getValueList().getValue());
    }

    @Test void deletionIncludesEveryUuidInOrder() {
        var ids = List.of("urn:uuid:first", "urn:uuid:second");
        var request = DeleteBodyBuilderUtility.buildRemoveObjectsRequest(ids);
        assertEquals(ids, request.getObjectRefList().getObjectRef().stream().map(ObjectRefType::getId).toList());
        assertTrue(DeleteBodyBuilderUtility.buildRemoveObjectsRequest(List.of()).getObjectRefList().getObjectRef().isEmpty());
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void publicationAndReplacementProduceSerializableRegistryGraph(boolean replace) throws Exception {
        var metadata = Document.parse(TestConstants.TEST_INI_EDS_ENTRY).getList("metadata", Document.class);
        var tree = RequestUtility.extractDocumentsFromMetadata(metadata);
        var document = CommonUtility.extractDocumentEntry(tree.getDocumentEntry());
        var submission = CommonUtility.extractSubmissionSetEntry(tree.getSubmissionSetEntry());
        var payload = JWTPayloadDTO.getMocked();
        var request = PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(document, submission, payload,
                replace ? "urn:uuid:previous" : null, "TRUE");
        var values = request.getRegistryObjectList().getIdentifiable().stream().map(JAXBElement::getValue).toList();
        assertEquals(replace ? 5 : 4, values.size());
        var entry = (ExtrinsicObjectType) values.get(0);
        assertEquals(Constants.IniClientConstants.DOCUMENT_ENTRY_ID, entry.getId());
        assertFalse(entry.getClassification().isEmpty());
        assertFalse(entry.getExternalIdentifier().isEmpty());
        assertEquals("TRUE", entry.getSlot().stream().filter(java.util.Objects::nonNull).filter(s -> s.getName().equals("urn:ita:fse:2025:EDSpublished"))
                .findFirst().orElseThrow().getValueList().getValue().get(0));
        var membership = (AssociationType1) values.get(2);
        assertEquals(Constants.IniClientConstants.SUBMISSION_ENTRY_ID, membership.getSourceObject());
        assertEquals(entry.getId(), membership.getTargetObject());
        if (replace) {
            var replacement = (AssociationType1) values.get(3);
            assertEquals("urn:ihe:iti:2007:AssociationType:RPLC", replacement.getAssociationType());
            assertEquals("urn:uuid:previous", replacement.getTargetObject());
        }
        var context = JAXBContext.newInstance(SubmitObjectsRequest.class);
        var writer = new StringWriter();
        context.createMarshaller().marshal(request, writer);
        var roundTrip = (SubmitObjectsRequest) context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertEquals(values.size(), roundTrip.getRegistryObjectList().getIdentifiable().size());
        assertEquals(entry.getId(), roundTrip.getRegistryObjectList().getIdentifiable().get(0).getValue().getId());
    }

    @Test void publicationRejectsAbsentDocumentBeforeBuildingPartialRequest() {
        assertThrows(BusinessException.class, () -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(null, null, null, null, "TRUE"));
        assertThrows(BusinessException.class, () -> PublishReplaceBodyBuilderUtility.buildSubmitObjectRequestOscuramento(null, null, null, null, null));
    }

    @Test void approvedVersionSelectionSkipsObjectReferencesAndDeprecatedEntries() {
        var factory = new ObjectFactory();
        var deprecated = new ExtrinsicObjectType();
        deprecated.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated");
        var approved = new ExtrinsicObjectType();
        approved.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
        var selected = factory.createExtrinsicObject(approved);
        assertSame(selected, UpdateBodyBuilderUtility.findApprovedExtrinsic(List.of(factory.createObjectRef(new ObjectRefType()),
                factory.createExtrinsicObject(deprecated), selected)));
        assertNull(UpdateBodyBuilderUtility.findApprovedExtrinsic(List.of(factory.createExtrinsicObject(deprecated))));
    }

    @Test void updatePreservesLineageAndRebindsIdentifiersToNewDocument() throws Exception {
        var response = TestUtility.mockQueryResponse();
        var approved = (ExtrinsicObjectType) UpdateBodyBuilderUtility.findApprovedExtrinsic(response.getRegistryObjectList().getIdentifiable()).getValue();
        var request = new MergedMetadatiRequestDTO();
        var body = new PublicationMetadataReqDTO();
        body.setDescriptions(List.of("updated description"));
        request.setBody(body);
        var token = new JWTTokenDTO(JWTPayloadDTO.getMocked());
        token.getPayload().setSubject_organization_id("0120");
        var updated = UpdateBodyBuilderUtility.buildSubmitObjectRequest(response.getRegistryObjectList(), request,
                "urn:uuid:lineage", token, "document-oid");
        var entry = (ExtrinsicObjectType) updated.getRegistryObjectList().getIdentifiable().get(0).getValue();
        assertSame(approved, entry);
        assertEquals("urn:uuid:lineage", entry.getLid());
        assertEquals(Constants.IniClientConstants.DOCUMENT_ENTRY_ID, entry.getId());
        assertTrue(entry.getClassification().stream().allMatch(c -> entry.getId().equals(c.getClassifiedObject())));
        assertTrue(entry.getExternalIdentifier().stream().allMatch(e -> entry.getId().equals(e.getRegistryObject())));
        assertEquals("updated description", entry.getSlot().stream().filter(s -> s.getName().equals("urn:ita:2022:description"))
                .findFirst().orElseThrow().getValueList().getValue().get(0));
        assertEquals(4, updated.getRegistryObjectList().getIdentifiable().size());
    }
}
