/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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