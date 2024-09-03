package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class IssuersDTO {

	private Integer counter;
	private Date dataAggiornamento;
	private IssuerDTO actualIssuer;
	
	private List<IssuerDTO> issuers;
    

}
