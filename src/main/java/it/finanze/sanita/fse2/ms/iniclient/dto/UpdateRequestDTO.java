package it.finanze.sanita.fse2.ms.iniclient.dto;

import lombok.Data;

@Data
public class UpdateRequestDTO {
    private String idDoc;
    private JWTPayloadDTO token;
    private PublicationMetadataReqDTO body;
}
