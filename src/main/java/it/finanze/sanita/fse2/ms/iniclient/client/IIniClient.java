package it.finanze.sanita.fse2.ms.iniclient.client;

import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ReplaceRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
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

    RegistryResponseType sendDeleteData(String identificativoDocUpdate, JWTPayloadDTO jwtToken);

    RegistryResponseType sendUpdateData(UpdateRequestDTO updateRequestDTO);

    RegistryResponseType sendReplaceData(ReplaceRequestDTO requestDTO, Document documentEntry, Document submissionSetEntry, Document jwtToken);

    String getReferenceUUID(String identificativoDocUpdate, JWTTokenDTO jwtToken);

    AdhocQueryResponse getReferenceMetadata(String identificativoDocUpdate, JWTTokenDTO jwtToken);
}
