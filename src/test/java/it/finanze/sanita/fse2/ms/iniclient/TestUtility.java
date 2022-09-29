package it.finanze.sanita.fse2.ms.iniclient;

import it.finanze.sanita.fse2.ms.iniclient.dto.JWTPayloadDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestUtility {
    public static File getFile(String fileName) {
        ClassLoader classLoader = TestUtility.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);

        if (resource == null) {
            throw new IllegalArgumentException("File not found!");
        } else {
            return new File(resource.getFile());
        }
    }

    public static AdhocQueryResponse mockQueryResponse() throws JAXBException {
        File xmlFile = TestUtility.getFile("Files/query_response_leafclass.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(AdhocQueryResponse.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (AdhocQueryResponse) jaxbUnmarshaller.unmarshal(xmlFile);
    }

    public static RegistryResponseType mockRegistryError() {
        RegistryResponseType registryResponseType = new RegistryResponseType();
        RegistryErrorList registryErrorList = new RegistryErrorList();
        RegistryError registryError = new RegistryError();
        registryError.setErrorCode("520");
        registryError.setSeverity("Generic severity");
        registryError.setValue("Error: generic error");
        registryErrorList.getRegistryError().add(registryError);
        registryResponseType.setRegistryErrorList(registryErrorList);
        return registryResponseType;
    }

    public static JWTTokenDTO mockBasicToken() {
        JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
        jwtTokenDTO.setPayload(new JWTPayloadDTO());
        return jwtTokenDTO;
    }
}
