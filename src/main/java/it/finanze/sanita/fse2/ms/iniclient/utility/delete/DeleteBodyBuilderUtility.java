/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient.utility.delete;

import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.RemoveObjectsRequestType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectRefListType;
import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.*;

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
}
