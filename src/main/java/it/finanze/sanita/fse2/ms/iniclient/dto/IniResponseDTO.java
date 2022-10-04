package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.Data;

@Data
public class IniResponseDTO {

	private Boolean esito;
	
	private String errorMessage;

	public IniResponseDTO() {
		this.esito = false;
	}
}
