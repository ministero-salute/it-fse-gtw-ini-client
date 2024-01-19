package it.finanze.sanita.fse2.ms.iniclient.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssuerResponseDTO extends ResponseDTO{

    private Boolean esito;
    private String id;

}
