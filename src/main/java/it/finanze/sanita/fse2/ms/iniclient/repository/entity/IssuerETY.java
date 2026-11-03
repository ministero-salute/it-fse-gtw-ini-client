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

    public static final String ISSUER_FIELD = "issuer";
    public static final String MOCK_FIELD = "mock";
    public static final String MAIL_RESPONSABILE_FIELD = "mailResponsabile";
    public static final String MIDDLEWARE_FIELD = "middleware";
    public static final String ETICHETTA_REGIONE_FIELD = "etichettaRegione";
    public static final String NOME_DOCUMENT_REPOSITORY_FIELD = "nomeDocumentRepository";
    public static final String PAZIENTE_CF_FIELD = "pazienteCf";
    public static final String EMAIL_SENT_FIELD = "isEmailSent";
    public static final String READY_TO_SCAN_FIELD = "readyToScan";
    public static final String MANDATORY_TESTS_FIELD = "mandatoryTests";
    public static final String ESONERATO_FIELD = "esonerato";
    public static final String MOCK_UAR_FIELD = "mockUar";

    @Id
    private String id;

    @Field(name = ISSUER_FIELD)
    private String issuer;

    @Field(name = MOCK_FIELD)
    private Boolean mock;

    @Field(name = MAIL_RESPONSABILE_FIELD)
    private String mailResponsabile;

    @Field(name = MIDDLEWARE_FIELD)
    private Boolean middleware;

    @Field(name = ETICHETTA_REGIONE_FIELD)
    private String etichettaRegione;

    @Field(name = NOME_DOCUMENT_REPOSITORY_FIELD)
    @Nullable
    private String nomeDocumentRepository;

    @Field(name = PAZIENTE_CF_FIELD)
    @Nullable
    private String pazienteCf;

    @Field(name = READY_TO_SCAN_FIELD)
    private boolean readyToScan;

    @Field(name = EMAIL_SENT_FIELD)
    private boolean emailSent;

    @Field(name = MANDATORY_TESTS_FIELD)
    private List<TestTypeEnum> mandatoryTests;

    @Field(name = ESONERATO_FIELD)
    private Boolean esonerato;
    
    @Field(name = MOCK_UAR_FIELD)
    private Boolean mockUar;
} 