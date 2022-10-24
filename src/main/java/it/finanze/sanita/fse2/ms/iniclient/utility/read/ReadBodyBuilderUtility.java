/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.utility.read;

import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.ResponseOptionType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AdhocQueryType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.Collections;

import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;

@Slf4j
public final class ReadBodyBuilderUtility {

	private ReadBodyBuilderUtility() {}

	private static final ObjectFactory objectFactory = new ObjectFactory();

	/**
	 *
	 * @param searchId will be idDoc for get reference, or entryUUID for get metadata
	 * @return
	 */
	public static AdhocQueryRequest buildAdHocQueryRequest(String searchId, ActionEnumType actionType) {
		try {
			AdhocQueryType adhocQueryType = buildAdHocQuery(searchId, actionType);
			ResponseOptionType responseOptionType = buildResponseOption(actionType);

			AdhocQueryRequest adhocQueryRequest = new AdhocQueryRequest();
			adhocQueryRequest.setResponseOption(responseOptionType);
			adhocQueryRequest.setStartIndex(BigInteger.valueOf(0));
			adhocQueryRequest.setFederated(false);
			adhocQueryRequest.setMaxResults(BigInteger.valueOf(-1));
			adhocQueryRequest.setAdhocQuery(adhocQueryType);

			return adhocQueryRequest;
		} catch(Exception ex) {
			log.error("Error while perform buildAdHocQueryRequest : {}" , ex.getMessage());
			throw new BusinessException("Error while perform buildAdHocQueryRequest : ", ex);
		}
	}

	/**
	 *
	 * @param searchId
	 * @return
	 */
	private static AdhocQueryType buildAdHocQuery(String searchId, ActionEnumType actionType) {
		try {
			AdhocQueryType adhocQuery = new AdhocQueryType();
			adhocQuery.setId("urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4");
			adhocQuery.getSlot().add(buildSlotObject(
					actionType == ActionEnumType.READ_REFERENCE ? "$XDSDocumentEntryUniqueId" : "$XDSDocumentEntryEntryUUID",
					null,
					Collections.singletonList("('" + searchId + "')")));
			JAXBElement<AdhocQueryType> jaxbAdhocQuery = objectFactory.createAdhocQuery(adhocQuery);
			return jaxbAdhocQuery.getValue();
		} catch(Exception ex) {
			log.error("Error while perform buildAdHocQuery : ", ex);
			throw new BusinessException("Error while perform buildAdHocQuery : ", ex);
		}
	}

	/**
	 *
	 * @return
	 */
	private static ResponseOptionType buildResponseOption(ActionEnumType actionType) {
		try {
			ResponseOptionType responseOptionType = new ResponseOptionType();
			switch (actionType) {
				case READ_REFERENCE:
					responseOptionType.setReturnType("ObjectRef");
					responseOptionType.setReturnComposedObjects(false);
					break;
				case READ_METADATA:
					responseOptionType.setReturnType("LeafClass");
					responseOptionType.setReturnComposedObjects(true);
					break;
				default:
					break;
			}
			return responseOptionType;
		} catch (Exception e) {
			log.error("Error while invoking buildResponseOption: {}", e.getMessage());
			throw new BusinessException("Error while invoking buildResponseOption: " + e.getMessage());
		}
	}
}
