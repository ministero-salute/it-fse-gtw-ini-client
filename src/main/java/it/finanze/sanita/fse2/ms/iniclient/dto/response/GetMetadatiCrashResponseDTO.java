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
package it.finanze.sanita.fse2.ms.iniclient.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMetadatiCrashResponseDTO extends ResponseDTO {

	private String identificativoDoc;

	private String identificativoRep;

	private String healthDataFormat;

	private String tipologiaStruttura;

	private List<String> attiCliniciRegoleAccesso;

	private String tipoDocumentoLivAlto;

	private String assettoOrganizzativo;

	private String dataInizioPrestazione;

	private String dataFinePrestazione;

	private String conservazioneANorma;

	private String tipoAttivitaClinica;

	private String identificativoSottomissione;

	private List<String> descriptions;

	private List<String> administrativeRequest;

	public GetMetadatiCrashResponseDTO() {
		super();
	}

	public GetMetadatiCrashResponseDTO(final LogTraceInfoDTO traceInfo,
			String identificativoDoc, String identificativoRep, String healthDataFormat, String tipologiaStruttura,
			List<String> attiCliniciRegoleAccesso, String tipoDocumentoLivAlto, String assettoOrganizzativo,
			String dataInizioPrestazione, String dataFinePrestazione,
			String conservazioneANorma, String tipoAttivitaClinica, String identificativoSottomissione,
			List<String> descriptions, List<String> administrativeRequest) {
		super(traceInfo);
		this.identificativoDoc = identificativoDoc;
		this.identificativoRep = identificativoRep;
		this.healthDataFormat = healthDataFormat;
		this.tipologiaStruttura = tipologiaStruttura;
		this.attiCliniciRegoleAccesso = attiCliniciRegoleAccesso;
		this.tipoDocumentoLivAlto = tipoDocumentoLivAlto;
		this.assettoOrganizzativo = assettoOrganizzativo;
		this.dataInizioPrestazione = dataInizioPrestazione;
		this.dataFinePrestazione = dataFinePrestazione;
		this.conservazioneANorma = conservazioneANorma;
		this.tipoAttivitaClinica = tipoAttivitaClinica;
		this.identificativoSottomissione = identificativoSottomissione;
		this.descriptions = descriptions;
		this.administrativeRequest = administrativeRequest;
	}

}
