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
package it.finanze.sanita.fse2.ms.iniclient.dto;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.ResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Base response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO extends ResponseDTO {

	@Schema(description = "Identificativo della classe del problema verificatosi")
	@Size(min = 0, max = 100)
	private String type;
	
	@Schema(description = "Descrizione della classe del problema verificatosi (invariante per occorrenze diverse dello stesso problema)")
	@Size(min = 0, max = 100)
	private String title;

	@Schema(description = "Descrizione del problema")
	@Size(min = 0, max = 1000)
	private String detail;

	@Schema(description = "URI che identifica la specifica occorrenza del problema")
	@Size(min = 0, max = 100)
	private String instance;

	public ErrorResponseDTO(final LogTraceInfoDTO traceInfo, final ErrorDTO inError) {
		super(traceInfo);
		this.type = inError.getType();
		this.title = inError.getTitle();
		this.detail = inError.getDetail();
		this.instance = inError.getInstance();
	}

}

