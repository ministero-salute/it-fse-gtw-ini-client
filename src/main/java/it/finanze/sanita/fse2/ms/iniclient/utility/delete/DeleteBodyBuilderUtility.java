package it.finanze.sanita.fse2.ms.iniclient.utility.delete;

import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.RemoveObjectsRequestType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectRefListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectRefType;

import javax.xml.bind.JAXBElement;

@Slf4j
public final class DeleteBodyBuilderUtility {

	private DeleteBodyBuilderUtility() {}

	private static final ObjectFactory objectFactory = new ObjectFactory();

	/**
	 *
	 * @param uuid
	 * @return
	 */
	public static RemoveObjectsRequestType buildRemoveObjectsRequest(String uuid) {
		try {
			RemoveObjectsRequestType removeObjectsRequestType = new RemoveObjectsRequestType();
			ObjectRefListType objectRefList = buildObjectRefList(uuid);
			removeObjectsRequestType.setObjectRefList(objectRefList);

			JAXBElement<RemoveObjectsRequestType> removeObjectsRequest = objectFactory.createRemoveObjectsRequest(removeObjectsRequestType);
			return removeObjectsRequest.getValue();
		} catch(Exception ex) {
			log.error("Error while perform build submit object request : {}" , ex.getMessage());
			throw new BusinessException("Error while perform build submit object request : ", ex);
		}
	}

	/**
	 *
	 * @param uuid
	 * @return
	 */
	private static ObjectRefListType buildObjectRefList(String uuid) {
		ObjectRefListType objectRefListType = new ObjectRefListType();

		try {
			ObjectRefType objectRef = new ObjectRefType();
			objectRef.setId(uuid);
			objectRefListType.getObjectRef().add(objectRef);
			return objectRefListType;
		} catch(Exception ex) {
			log.error("Error while perform build registry object list : " , ex);
			throw new BusinessException("Error while perform build registry object list : " , ex);
		}
	}
}