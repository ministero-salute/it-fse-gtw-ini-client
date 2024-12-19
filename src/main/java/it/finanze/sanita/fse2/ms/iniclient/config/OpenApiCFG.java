
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
package it.finanze.sanita.fse2.ms.iniclient.config;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI configuration class
 */
@Configuration
public class OpenApiCFG {

    @Autowired
	private CustomSwaggerCFG customOpenapi;

    @Bean
    public OpenApiCustomiser disableAdditionalRequestProperties() {

        final List<String> required = new ArrayList<>();
		required.add("file");
        required.add("requestBody");

        return openApi -> {

            // Populating info section.
            openApi.getInfo().setTitle(customOpenapi.getTitle());
            openApi.getInfo().setVersion(customOpenapi.getVersion());
            openApi.getInfo().setDescription(customOpenapi.getDescription());
            openApi.getInfo().setTermsOfService(customOpenapi.getTermsOfService());

            // Adding contact to info section
            final Contact contact = new Contact();
            contact.setName(customOpenapi.getContactName());
            contact.setUrl(customOpenapi.getContactUrl());
            contact.setEmail(customOpenapi.getContactMail());
            openApi.getInfo().setContact(contact);

            // Adding extensions
            openApi.getInfo().addExtension("x-api-id", customOpenapi.getApiId());
            openApi.getInfo().addExtension("x-summary", customOpenapi.getApiSummary());

            for (final Server server : openApi.getServers()) {
                final Pattern pattern = Pattern.compile("^https://.*");
                if (!pattern.matcher(server.getUrl()).matches()) {
                    server.addExtension("x-sandbox", true);
                }
            }

            openApi.getComponents().getSchemas().values().forEach(schema -> {
				schema.setAdditionalProperties(false);
			});
 
        };
    }

    /**
     * Disable additional properties on every response object
     * @return The {@link OpenApiCustomiser} instance
     */
    @Bean
    public OpenApiCustomiser disableAdditionalResponseProperties() {
        return openApi -> openApi.getComponents().
            getSchemas().
            values().
            forEach( s -> s.setAdditionalProperties(false));
    }

}
