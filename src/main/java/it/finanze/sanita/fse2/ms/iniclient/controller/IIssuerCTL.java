package it.finanze.sanita.fse2.ms.iniclient.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerDeleteResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IssuerResponseDTO;

@RequestMapping(path = "/v1")
@Tag(name = "Servizio di censimento issuer")
@Validated
public interface IIssuerCTL {

        @PostMapping("/issuer-create")
        @Operation(summary = "Creazione issuer", description = "Crea un issuer nel database.")
        @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IssuerResponseDTO.class)))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Creazione eseguita con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IssuerResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IssuerResponseDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IssuerResponseDTO.class))) })
        @ResponseStatus(HttpStatus.CREATED) 
        IssuerResponseDTO create(@Valid @RequestBody IssuerCreateRequestDTO requestBody, HttpServletRequest request);

        @DeleteMapping("/issuer-delete/{issuer}")
        @Operation(summary = "Cancellazione issuer", description = "Cancella l'issuer dal database.")
        @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IssuerDeleteResponseDTO.class)))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cancellazione eseguita con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IssuerDeleteResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IssuerDeleteResponseDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IssuerDeleteResponseDTO.class))) })
        IssuerDeleteResponseDTO delete(@PathVariable(required = true) String issuer, HttpServletRequest request);

        @PutMapping("/issuer-replace")
        @Operation(summary = "Sostituzione issuer", description = "Sostituisce issuer sul db.")
        @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IssuerResponseDTO.class)))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Aggiornamento eseguito con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IssuerResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IssuerResponseDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IssuerResponseDTO.class))) })
        IssuerResponseDTO replace(@Valid @RequestBody IssuerCreateRequestDTO requestBody, HttpServletRequest request);
}
