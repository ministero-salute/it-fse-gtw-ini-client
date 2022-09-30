package it.finanze.sanita.fse2.ms.iniclient.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMetadatiReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.ReplaceRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;


/**
 *
 *	Controller INI publication.
 */
@RequestMapping(path = "/v1")
@Tag(name = "Servizio pubblicazione verso INI")
public interface IIniOperationCTL {

    @PostMapping("/ini-publish")
	@Operation(summary = "Pubblicazione metadati ad INI", description = "Invia i metadati di una risorsa FHIR ad INI.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Pubblicazione eseguita con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))) })
	IniTraceResponseDTO create(@RequestBody String workflowInstanceId, HttpServletRequest request);

	@DeleteMapping("/ini-delete")
	@Operation(summary = "Cancellazione metadati su INI", description = "Cancella i metadati di una risorsa FHIR su INI.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Cancellazione eseguita con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))) })
	IniTraceResponseDTO delete(@RequestBody DeleteRequestDTO requestBody, HttpServletRequest request);

	@PutMapping("/ini-update")
	@Operation(summary = "Aggiornamento metadati ad INI", description = "Invia i metadati di una risorsa FHIR ad INI.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Aggiornamento eseguito con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))) })
	IniTraceResponseDTO update(@RequestBody UpdateRequestDTO requestBody, HttpServletRequest request);

	@PutMapping("/ini-replace")
	@Operation(summary = "Sostituzione metadati ad INI", description = "Invia i metadati di una risorsa FHIR ad INI.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Aggiornamento eseguito con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))) })
	IniTraceResponseDTO replace(@RequestBody ReplaceRequestDTO requestDTO, HttpServletRequest request);
	
	@PostMapping("/get-metadati/{idDoc}")
	@Operation(summary = "Get metadati INI", description = "Get dei metadati dato un oid in input.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class)))
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))) })
	AdhocQueryResponse getMetadati(@PathVariable(required = true) String idDoc,@RequestBody GetMetadatiReqDTO jwtPayload, HttpServletRequest request);
}
