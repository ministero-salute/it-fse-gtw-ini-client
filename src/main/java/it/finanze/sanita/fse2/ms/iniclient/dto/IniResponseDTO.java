package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class IniResponseDTO {

	private Boolean esito;
	
	private String errorMessage;
}
