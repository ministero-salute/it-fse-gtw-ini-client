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
package it.finanze.sanita.fse2.ms.iniclient.client;

import it.finanze.sanita.fse2.ms.iniclient.dto.DocumentEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.SubmissionSetEntryDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

/**
 * Interface of Ini client.
 */
public interface IIniClient {

    RegistryResponseType sendPublicationData(DocumentEntryDTO documentEntry, SubmissionSetEntryDTO submissionSetEntry, JWTTokenDTO jwtTokenDTO);
    
    RegistryResponseType sendDeleteData(String idDoc, JWTPayloadDTO jwtToken, String uuid);

    RegistryResponseType sendUpdateData(SubmitObjectsRequest submitObjectsRequest, JWTTokenDTO jwtTokenDTO);
    RegistryResponseType sendUpdateV2Data(SubmitObjectsRequest submitObjectsRequest, JWTTokenDTO jwtTokenDTO);
    
    RegistryResponseType sendReplaceData(DocumentEntryDTO documentEntryDTO, SubmissionSetEntryDTO submissionSetEntryDTO, JWTTokenDTO jwtTokenDTO, String uuid);
    
    AdhocQueryResponse getReferenceUUID(String idDoc, String tipoRicerca,JWTTokenDTO tokenDTO);
    
    AdhocQueryResponse getReferenceMetadata(String uuid, String tipoRicerca, JWTTokenDTO jwtToken);

    AdhocQueryResponse getReferenceMetadata(String uuid, String tipoRicerca, JWTTokenDTO jwtToken, ActionEnumType actionEnumType);
}
