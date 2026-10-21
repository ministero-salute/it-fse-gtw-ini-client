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
package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;

import it.finanze.sanita.fse2.ms.iniclient.utility.common.CommonUtility;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;

/**
 * Regressione per il bug segnalato in it-fse-support#3164: l'oscuramento a catena non riproponeva
 * correttamente la Classification ClassCode (XDSDocumentEntry.classCode) del documento esistente.
 * ClassCode e' un codice mnemonico breve del value set dell'Affinity Domain (es. "LDO", "REF", "PRE" -
 * vedi TipoDocAltoLivEnum), NON un codice LOINC: va estratto dal nodeRepresentation della classification
 * con scheme ClassCode (41a5887f-...), diverso sia dal TypeCode/LOINC (f0306f51-..., risorsa SAML
 * resource:hl7:type) sia dalla descrizione testuale esposta da extractDocumentTypeFromQueryResponse.
 * <p>Puro unit test (nessun contesto Spring): CommonUtility e TestUtility.mockQueryResponse sono
 * utility statiche che non richiedono beans/Mongo.
 */
class CommonUtilityTest {

	@Test
	void extractClassCodeFromQueryResponse_returnsNodeRepresentation_notTypeCodeOrDescription() throws JAXBException {
		AdhocQueryResponse response = TestUtility.mockQueryResponse();

		String classCode = CommonUtility.extractClassCodeFromQueryResponse(response);
		String documentType = CommonUtility.extractDocumentTypeFromQueryResponse(response);

		// Il fixture ha Classification ClassCode (scheme 41a5887f-...) con nodeRepresentation="PRE"
		// (vedi query_response_leafclass.xml).
		assertEquals("PRE", classCode);
		// documentType e' la descrizione del TypeCode (scheme f0306f51-..., LOINC "29304-3"), un
		// metadato diverso: non va confuso/scambiato con ClassCode, come accadeva nel bug originale.
		assertNotEquals(classCode, documentType);
	}

	@Test
	void extractClassCodeFromQueryResponse_returnsNull_whenMetadataMissing() {
		assertNull(CommonUtility.extractClassCodeFromQueryResponse(null));
		assertNull(CommonUtility.extractClassCodeFromQueryResponse(new AdhocQueryResponse()));
	}
}
