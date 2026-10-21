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

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.WSBindingProvider;

import ihe.iti.xds_b._2007.DocumentRegistryPortType;
import ihe.iti.xds_b._2007.DocumentRegistryService;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.IniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.GovwayCfg;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;

@ExtendWith(MockitoExtension.class)
class IniClientPortIsolationTest {

    @Mock
    private SamlHeaderBuilderUtility samlHeaderBuilderUtility;

    @Mock
    private IniCFG iniCFG;

    @Mock
    private GovwayCfg govwayCfg;

    @InjectMocks
    private IniClient client;

    @Test
    @DisplayName("Each SOAP update uses a separate port for its headers and body, leaving the shared template untouched")
    void eachRequestUsesItsOwnPortHeadersAndBody() {
        // Arrange: each newly created service returns its request's SOAP port.
        DocumentRegistryPortType portA = mockPort();
        DocumentRegistryPortType portB = mockPort();
        DocumentRegistryPortType template = mockPort();
        when(((WSBindingProvider) template).getBinding().getHandlerChain()).thenReturn(new ArrayList<>());
        ReflectionTestUtils.setField(client, "documentRegistryPort", template);

        JWTTokenDTO tokenA = mock(JWTTokenDTO.class);
        JWTTokenDTO tokenB = mock(JWTTokenDTO.class);
        List<Header> headersA = List.of(mock(Header.class));
        List<Header> headersB = List.of(mock(Header.class));
        when(samlHeaderBuilderUtility.buildHeader(tokenA, ActionEnumType.UPDATE)).thenReturn(headersA);
        when(samlHeaderBuilderUtility.buildHeader(tokenB, ActionEnumType.UPDATE)).thenReturn(headersB);
        SubmitObjectsRequest bodyA = new SubmitObjectsRequest();
        SubmitObjectsRequest bodyB = new SubmitObjectsRequest();

        // Act: send two requests. No real transport or SOAP logging is executed.
        try (MockedConstruction<DocumentRegistryService> services = mockConstruction(
                DocumentRegistryService.class, (service, context) ->
                        when(service.getDocumentRegistryPortSoap12())
                                .thenReturn(context.getCount() == 1 ? portA : portB))) {
            client.sendUpdateData(bodyA, tokenA, "workflow-A", new Date());
            client.sendUpdateData(bodyB, tokenB, "workflow-B", new Date());

            org.junit.jupiter.api.Assertions.assertEquals(2, services.constructed().size());
        }

        // Assert: each port receives only its request's headers, then its body.
        InOrder requestA = inOrder(portA);
        requestA.verify((WSBindingProvider) portA).setOutboundHeaders(headersA);
        requestA.verify(portA).documentRegistryRegisterDocumentSetB(bodyA);

        InOrder requestB = inOrder(portB);
        requestB.verify((WSBindingProvider) portB).setOutboundHeaders(headersB);
        requestB.verify(portB).documentRegistryRegisterDocumentSetB(bodyB);

        verify((WSBindingProvider) portA, never()).setOutboundHeaders(headersB);
        verify((WSBindingProvider) portB, never()).setOutboundHeaders(headersA);
        verify(portA, never()).documentRegistryRegisterDocumentSetB(bodyB);
        verify(portB, never()).documentRegistryRegisterDocumentSetB(bodyA);
        verify((WSBindingProvider) template, never()).setOutboundHeaders(headersA);
        verify((WSBindingProvider) template, never()).setOutboundHeaders(headersB);
    }

    private DocumentRegistryPortType mockPort() {
        DocumentRegistryPortType port = mock(DocumentRegistryPortType.class,
                withSettings().extraInterfaces(WSBindingProvider.class));
        BindingProvider provider = (BindingProvider) port;
        Binding binding = mock(Binding.class);
        when(provider.getRequestContext()).thenReturn(new HashMap<>());
        when(provider.getBinding()).thenReturn(binding);
        return port;
    }
}
