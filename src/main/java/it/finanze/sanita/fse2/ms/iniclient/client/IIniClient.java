package it.finanze.sanita.fse2.ms.iniclient.client;

import org.bson.Document;

import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

/**
 * Interface of Ini client.
 * 
 * @author vincenzoingenito
 */
public interface IIniClient {

    RegistryResponseType sendData(Document documentEntry, Document submissionSetEntry, Document jwtToken);
}
