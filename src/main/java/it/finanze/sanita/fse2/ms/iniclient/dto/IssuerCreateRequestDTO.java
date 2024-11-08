package it.finanze.sanita.fse2.ms.iniclient.dto;

import javax.validation.constraints.NotBlank;

import com.mongodb.lang.Nullable;

import it.finanze.sanita.fse2.ms.iniclient.validators.ValidMiddlewareIssuer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidMiddlewareIssuer
public class IssuerCreateRequestDTO {

    @NonNull
    @NotBlank
    private String issuer;

    private boolean mock;

    @NonNull
    @NotBlank
    private String mailResponsabile;

    private boolean middleware;

    @NonNull
    @NotBlank
    private String etichettaRegione;

    @Nullable
    private String nomeDocumentRepository;
 
    private String pazienteCf;

    private boolean readyToScan;

    @Nullable
    private boolean esonerato;
}
