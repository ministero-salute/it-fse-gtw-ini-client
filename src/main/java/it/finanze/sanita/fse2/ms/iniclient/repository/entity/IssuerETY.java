package it.finanze.sanita.fse2.ms.iniclient.repository.entity;

import com.mongodb.lang.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

@Document(collection = "#{@issuersBean}")
@Data
@NoArgsConstructor
public class IssuerETY {

    public static final String ISSUER = "issuer";
    public static final String MOCK = "mock";
    public static final String MAIL_RESPONSABILE = "mailResponsabile";
    public static final String MIDDLEWARE = "middleware";
    public static final String ETICHETTA_REGIONE = "etichettaRegione";
    public static final String NOME_DOCUMENT_REPOSITORY = "nomeDocumentRepository";

    @Id
    private String id;

    @Field(name = ISSUER)
    private String issuer;

    @Field(name = MOCK)
    private Boolean mock;

    @Field(name = MAIL_RESPONSABILE)
    private String mailResponsabile;

    @Field(name = MIDDLEWARE)
    private Boolean middleware;

    @Field(name = ETICHETTA_REGIONE)
    private String etichettaRegione;

    @Field(name = NOME_DOCUMENT_REPOSITORY)
    @Nullable
    private String nomeDocumentRepository;
}
