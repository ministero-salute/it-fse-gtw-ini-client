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
package it.finanze.sanita.fse2.ms.iniclient.dto;
 
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.mongodb.lang.Nullable;

import it.finanze.sanita.fse2.ms.iniclient.enums.TestTypeEnum;
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

    private List<TestTypeEnum> mandatoryTests;

    @Nullable
    private Boolean esonerato;
}
