package it.finanze.sanita.fse2.ms.iniclient.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "#{@issuersBean}")
@Data
@NoArgsConstructor
public class IssuerETY {

    public static final String ISSUER = "issuer";
    public static final String MOCK = "mock";

    @Id
    private String id;

    @Field(name = ISSUER)
    private String issuer;

    @Field(name = MOCK)
    private Boolean mock;
}
