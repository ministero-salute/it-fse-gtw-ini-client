package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.finanze.sanita.fse2.ms.iniclient.utility.delete.DeleteBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.read.ReadBodyBuilderUtility;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.RemoveObjectsRequestType;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;

/**
 * Pure unit tests for the read and delete SOAP body builders.
 */
class BodyBuilderUtilityTest {

	@Test
	void adHocQueryWithoutAuthorHasOnlyUniqueIdSlot() {
		AdhocQueryRequest req = ReadBodyBuilderUtility.buildAdHocQueryRequest("doc-1", "LeafClass", false, "RSSMRA80A01H501U");

		assertEquals("LeafClass", req.getResponseOption().getReturnType());
		assertTrue(req.getResponseOption().isReturnComposedObjects());
		assertEquals(BigInteger.ZERO, req.getStartIndex());
		assertEquals(BigInteger.valueOf(-1), req.getMaxResults());
		assertFalse(req.isFederated());
		assertEquals("urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4", req.getAdhocQuery().getId());
		assertEquals(1, req.getAdhocQuery().getSlot().size());
		assertEquals("$XDSDocumentEntryUniqueId", req.getAdhocQuery().getSlot().get(0).getName());
		assertEquals(List.of("('doc-1')"), req.getAdhocQuery().getSlot().get(0).getValueList().getValue());
	}

	@Test
	void adHocQueryWithAuthorAddsAuthorSlot() {
		AdhocQueryRequest req = ReadBodyBuilderUtility.buildAdHocQueryRequest("doc-1", "ObjectRef", true, "RSSMRA80A01H501U");

		assertEquals(2, req.getAdhocQuery().getSlot().size());
		assertEquals("$XDSDocumentEntry.author", req.getAdhocQuery().getSlot().get(1).getName());
		assertEquals(List.of("('RSSMRA80A01H501U')"), req.getAdhocQuery().getSlot().get(1).getValueList().getValue());
	}

	@Test
	void removeObjectsRequestContainsAllUuids() {
		RemoveObjectsRequestType req = DeleteBodyBuilderUtility.buildRemoveObjectsRequest(List.of("uuid-1", "uuid-2"));

		assertEquals(2, req.getObjectRefList().getObjectRef().size());
		assertEquals("uuid-1", req.getObjectRefList().getObjectRef().get(0).getId());
		assertEquals("uuid-2", req.getObjectRefList().getObjectRef().get(1).getId());
	}

	@Test
	void removeObjectsRequestWithEmptyListHasNoRefs() {
		RemoveObjectsRequestType req = DeleteBodyBuilderUtility.buildRemoveObjectsRequest(List.of());
		assertTrue(req.getObjectRefList().getObjectRef().isEmpty());
	}
}
