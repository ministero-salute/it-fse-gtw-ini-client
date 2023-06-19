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
package it.finanze.sanita.fse2.ms.iniclient.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import it.finanze.sanita.fse2.ms.iniclient.dto.GetReferenceReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMergedMetadatiResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMetadatiResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;


/**
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
	IniTraceResponseDTO replace(@RequestBody String workflowInstanceId, HttpServletRequest request);
	
	@PostMapping("/get-metadati/{idDoc}")
	@Operation(summary = "Get metadati INI", description = "Get dei metadati dato un oid in input.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetMetadatiResponseDTO.class)))
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetMetadatiResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))) })
	ResponseEntity<GetMetadatiResponseDTO> getMetadati(@PathVariable(required = true) String idDoc,@RequestBody GetMetadatiReqDTO jwtPayload, HttpServletRequest request);

	@PostMapping("/get-reference/{idDoc}")
	@Operation(summary = "Get reference INI", description = "Recupero UUID dato un oid in input")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetReferenceResponseDTO.class))),
		@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
		@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class)))
	})
	ResponseEntity<GetReferenceResponseDTO> getReference(@PathVariable String idDoc, @RequestBody GetReferenceReqDTO jwtPayload, HttpServletRequest request);
	
	@PutMapping("/get-merged-metadati")
	@Operation(summary = "Aggiornamento metadati ad INI", description = "Invia i metadati di una risorsa FHIR ad INI.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GetMergedMetadatiResponseDTO.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Aggiornamento eseguito con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = IniTraceResponseDTO.class))) })
	GetMergedMetadatiResponseDTO getMergedMetadati(@RequestBody MergedMetadatiRequestDTO requestBody, HttpServletRequest request);

}
