/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright (C) 2023 Ministero della Salute
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient.repository.entity;
 
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.lang.Nullable;

import it.finanze.sanita.fse2.ms.iniclient.enums.TestTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
 
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