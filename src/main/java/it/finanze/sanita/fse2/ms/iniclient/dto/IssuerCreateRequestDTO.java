package it.finanze.sanita.fse2.ms.iniclient.dto;

import com.mongodb.lang.Nullable;

import it.finanze.sanita.fse2.ms.iniclient.enums.TestTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.validators.ValidMiddlewareIssuer;
import lombok.*;

import java.util.List;

import javax.validation.constraints.NotBlank;

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

    private List<TestTypeEnum> mandatoryTests;
}
