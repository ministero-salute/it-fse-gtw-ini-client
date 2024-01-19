package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssuerCreateRequestDTO {

    private String issuer;
    private boolean mock;
}
