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
package it.finanze.sanita.fse2.ms.iniclient.utility.read;

import static it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlBodyBuilderCommonUtility.buildSlotObject;

import java.math.BigInteger;
import java.util.Collections;

import javax.xml.bind.JAXBElement;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.ResponseOptionType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AdhocQueryType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReadBodyBuilderUtility {


    private static final ObjectFactory objectFactory = new ObjectFactory();

	/**
	 * @param searchId will be idDoc for get reference, or entryUUID for get metadata
	 * @return
	 */
	public static AdhocQueryRequest buildAdHocQueryRequest(String searchId,String tipoRicerca) {
		AdhocQueryType adhocQueryType = buildAdHocQuery(searchId);
		ResponseOptionType responseOptionType = buildResponseOption(tipoRicerca);

		AdhocQueryRequest adhocQueryRequest = new AdhocQueryRequest();
		adhocQueryRequest.setResponseOption(responseOptionType);
		adhocQueryRequest.setStartIndex(BigInteger.valueOf(0));
		adhocQueryRequest.setFederated(false);
		adhocQueryRequest.setMaxResults(BigInteger.valueOf(-1));
		adhocQueryRequest.setAdhocQuery(adhocQueryType);

		return adhocQueryRequest;
	}

	/**
	 * @param searchId
	 * @return
	 */
	private static AdhocQueryType buildAdHocQuery(String searchId) {
		AdhocQueryType adhocQuery = new AdhocQueryType();
		adhocQuery.setId("urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4");
		adhocQuery.getSlot().add(buildSlotObject("$XDSDocumentEntryUniqueId",null,Collections.singletonList("('" + searchId + "')")));
		JAXBElement<AdhocQueryType> jaxbAdhocQuery = objectFactory.createAdhocQuery(adhocQuery);
		return jaxbAdhocQuery.getValue();
	}


	private static ResponseOptionType buildResponseOption(String tipoRicerca) {
		ResponseOptionType responseOptionType = new ResponseOptionType();
		responseOptionType.setReturnType(tipoRicerca);
		responseOptionType.setReturnComposedObjects(true);
		return responseOptionType;
	}
}
