package it.finanze.sanita.fse2.ms.iniclient.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IssuersDTO {

	private Integer counter;
	private Date dataAggiornamento;
	
	private List<IssuerDTO> issuers;
    

}
