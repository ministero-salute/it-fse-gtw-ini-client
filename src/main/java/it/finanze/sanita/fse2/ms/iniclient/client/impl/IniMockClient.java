package it.finanze.sanita.fse2.ms.iniclient.client.impl;

import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;


/**
 * Test implemention of Ini Client.
 * 
 * @author vincenzoingenito
 */
@Slf4j
@Component
@Profile("!" + Constants.Profile.DEV)
public class IniMockClient implements IIniClient {
 
	
	@Override
	public RegistryResponseType sendData(final Document documentEntry, final Document submissionSetEntry, final Document jwtEntry) {
		log.warn("ATTENZIONE : Si sta chiamando il client mockato . Assicurarsi che sia voluto");
		RegistryResponseType out = new RegistryResponseType();
		out.setStatus("OK");
		return out;
	}
	
 

}