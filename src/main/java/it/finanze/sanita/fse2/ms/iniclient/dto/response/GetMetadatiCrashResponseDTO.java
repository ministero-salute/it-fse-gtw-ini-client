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

	private String slotIdentificativoRep;
	private List<String> slotAdministrativeRequest;
	private String slotDataInizioPrestazione; 
	private String slotDataFinePrestazione;
	private String slotConservazioneANorma; 
	private List<String> slotDescriptions; 
	private String classificationTipoDocumentoLivAlto; 
	private String classificationTipologiaStruttura;
	private String classificationAssettoOrganizzativo; 
	private List<String> classificationAttiCliniciRegoleAccesso; 

	// private String healthDataFormat; 
	// private String tipoAttivitaClinica; 
	// private String identificativoSottomissione; 
	 

	public GetMetadatiCrashResponseDTO() {
		super();
	}

	public GetMetadatiCrashResponseDTO(final LogTraceInfoDTO traceInfo, String identificativoRep, String healthDataFormat, String tipologiaStruttura,
			List<String> attiCliniciRegoleAccesso, String tipoDocumentoLivAlto, String assettoOrganizzativo,
			String dataInizioPrestazione, String dataFinePrestazione,
			String conservazioneANorma, String tipoAttivitaClinica, String identificativoSottomissione,
			List<String> descriptions, List<String> administrativeRequest) {
		super(traceInfo);
		 
		this.slotIdentificativoRep = identificativoRep;
		
		this.classificationTipologiaStruttura = tipologiaStruttura;
		this.classificationAttiCliniciRegoleAccesso = attiCliniciRegoleAccesso;
		this.classificationTipoDocumentoLivAlto = tipoDocumentoLivAlto;
		this.classificationAssettoOrganizzativo = assettoOrganizzativo;
		this.slotDataInizioPrestazione = dataInizioPrestazione;
		this.slotDataFinePrestazione = dataFinePrestazione;
		this.slotConservazioneANorma = conservazioneANorma;
		this.slotDescriptions = descriptions;
		this.slotAdministrativeRequest = administrativeRequest;

		// this.tipoAttivitaClinica = tipoAttivitaClinica;
		// this.identificativoSottomissione = identificativoSottomissione;
		// this.healthDataFormat = healthDataFormat;
	}

}
