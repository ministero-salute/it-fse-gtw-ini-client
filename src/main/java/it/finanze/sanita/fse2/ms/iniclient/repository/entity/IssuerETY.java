package it.finanze.sanita.fse2.ms.iniclient.repository.entity;

import com.mongodb.lang.Nullable;

import it.finanze.sanita.fse2.ms.iniclient.enums.TestTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    public static final String PAZIENTE_CF = "pazienteCf";
    public static final String EMAIL_SENT = "isEmailSent";
    public static final String READY_TO_SCAN = "readyToScan";
    public static final String MANDATORY_TESTS = "mandatoryTests";
    public static final String ESONERATO = "esonerato";


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

    @Field(name = PAZIENTE_CF)
    @Nullable
    private String pazienteCf;

    @Field(name = READY_TO_SCAN)
    private boolean readyToScan;

    @Field(name = EMAIL_SENT)
    private boolean emailSent;

    @Field(name = MANDATORY_TESTS)
    private List<TestTypeEnum> mandatoryTests;

    @Field(name = ESONERATO)
    private Boolean esonerato;

    
} 