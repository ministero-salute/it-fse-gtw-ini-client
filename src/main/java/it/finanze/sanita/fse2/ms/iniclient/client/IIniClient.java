/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.client;

import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.bson.Document;

/**
 * Interface of Ini client.
 * 
 * @author vincenzoingenito
 */
public interface IIniClient {

    RegistryResponseType sendPublicationData(Document documentEntry, Document submissionSetEntry, Document jwtToken);

    RegistryResponseType sendDeleteData(String idDoc, JWTPayloadDTO jwtToken, String uuid);

    RegistryResponseType sendUpdateData(SubmitObjectsRequest submitObjectsRequest, JWTTokenDTO jwtTokenDTO);
    
    RegistryResponseType sendReplaceData(Document documentEntry, Document submissionSetEntry, Document jwtToken, String uuid);
    
    String getReferenceUUID(String idDoc, JWTTokenDTO jwtToken);

    AdhocQueryResponse getReferenceMetadata(String idDoc, JWTTokenDTO jwtToken);
}
