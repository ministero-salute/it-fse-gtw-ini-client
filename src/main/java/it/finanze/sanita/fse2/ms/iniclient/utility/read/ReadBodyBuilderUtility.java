package it.finanze.sanita.fse2.ms.iniclient.utility.read;

import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.ResponseOptionType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AdhocQueryType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Slf4j
public final class ReadBodyBuilderUtility {

	private ReadBodyBuilderUtility() {}

	private static final ObjectFactory objectFactory = new ObjectFactory();

	/**
	 *
	 * @param identificativoDocUpdate
	 * @return
	 */
	public static AdhocQueryRequest buildAdHocQueryRequest(String identificativoDocUpdate, ActionEnumType actionType) {
		try {
			AdhocQueryType adhocQueryType = buildAdHocQuery(identificativoDocUpdate);
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
	 * @param identificativoDocUpdate
	 * @return
	 */
	private static AdhocQueryType buildAdHocQuery(String identificativoDocUpdate) {
		try {
			AdhocQueryType adhocQuery = new AdhocQueryType();
			adhocQuery.setId("urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4");
			adhocQuery.getSlot().add(buildSlotObject("$XDSDocumentEntryUniqueId", null, Collections.singletonList("('" + identificativoDocUpdate + "')")));
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

	/**
	 *
	 * @param name
	 * @param type
	 * @param values
	 * @return
	 */
	private static SlotType1 buildSlotObject(String name, String type, List<String> values) {
		SlotType1 slotObject = new SlotType1();
		try {
			slotObject.setName(name);
			slotObject.setSlotType(type);
			ValueListType valueList = new ValueListType();
			for (String value : values) {
				valueList.getValue().add(value);
			}
			slotObject.setValueList(valueList);

			return slotObject;
		} catch (Exception e) {
			log.error("Error while invoking buildSlotObject: {}", e.getMessage());
			throw new BusinessException("Error while invoking buildSlotObject: " + e.getMessage());
		}
	}
}
