
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

    public static RegistryResponseType mockRegistrySuccess() {
        RegistryResponseType registryResponseType = new RegistryResponseType();
        registryResponseType.setStatus("OK");
        return registryResponseType;
    }

    public static JWTTokenDTO mockBasicToken() {
        JWTTokenDTO jwtTokenDTO = new JWTTokenDTO();
        jwtTokenDTO.setPayload(new JWTPayloadDTO());
        return jwtTokenDTO;
    }
}
