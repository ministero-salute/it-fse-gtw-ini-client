package it.finanze.sanita.fse2.ms.iniclient.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniPublicationResponseDTO;


/**
 *
 *	Controller INI publication.
 */
@RequestMapping(path = "/v1.0.0")
@Tag(name = "Servizio pubblicazione verso INI")
public interface IINIPublicationCTL {

    @PostMapping("/ini-publish")
	@Operation(summary = "Pubblicazione metadati ad INI", description = "Invia i metadati di una risorsa FHIR ad INI.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniPublicationResponseDTO.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Pubblicazione eseguita con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniPublicationResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))) })
    IniPublicationResponseDTO publicationCreation(@RequestBody String workflowInstanceId, HttpServletRequest request);

    
}
