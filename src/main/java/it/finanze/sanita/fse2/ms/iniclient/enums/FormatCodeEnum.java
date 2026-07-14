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
package it.finanze.sanita.fse2.ms.iniclient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FormatCodeEnum {

    LAB("2.16.840.1.113883.2.9.10.1.1", "Referto di Laboratorio"),
    RAD("2.16.840.1.113883.2.9.10.1.7.1", "Referto di Radiologia"),
    LDO("2.16.840.1.113883.2.9.10.1.5", "Lettera di Dimissione Ospedaliera"),
    VPS("2.16.840.1.113883.2.9.10.1.6.1", "Verbale di Pronto Soccorso"),
    RSA("2.16.840.1.113883.2.9.10.1.9.1", "Referto di Specialistica Ambulatoriale"),
    PSS("2.16.840.1.113883.2.9.10.1.4.1.1", "Profilo Sanitario Sintetico"),
    CFV("2.16.840.1.113883.2.9.10.1.11.1.2", "Certificato Vaccinale"),
    PRS("2.16.840.1.113883.2.9.10.1.2", "Prescrizione"),
    PTO("2.16.840.1.113883.2.9.4.3.14", "Piano Terapeutico"),
    VAC("2.16.840.1.113883.2.9.10.1.11.1.1", "Scheda della singola Vaccinazione"),
    EFC("2.16.840.1.113883.2.9.10.1.13.2.1", "Erogazione Farmaceutica non a carico SSN"),
    PFC("2.16.840.1.113883.2.9.10.1.3.1", "Prescrizione Farmaceutica non a carico SSN"),
    LIV("2.16.840.1.113883.2.9.10.1.15.1", "Lettera di invito per Vaccinazione"),
    LIS("2.16.840.1.113883.2.9.10.1.14.1", "Lettera di invito per screening o ad altri percorsi di prevenzione"),
    RTV("2.16.840.1.113883.2.9.10.1.16.1.1", "Referto di Televisita"),
    RFTM("2.16.840.1.113883.2.9.10.1.16.3.1", "Relazione finale di telemonitoraggio"),
    RFTA("2.16.840.1.113883.2.9.10.1.16.4.1", "Relazione finale di teleassistenza"),
    RTC("2.16.840.1.113883.2.9.10.1.16.2.1", "Relazione di teleconsulto"),
    CAC("2.16.840.1.113883.2.9.10.1.18.1", "Cartella Clinica"),
    TPI("2.16.840.1.113883.2.9.10.1.17.1", "Tessera portatore di impianto"),
    FHIR_DOCUMENT_TELEVISITA("FHIRDocumentTelevisita", "Bundle contenente i dati FHIR relativi al Referto di Televisita"),
    FHIR_DOCUMENT_TELECONSULTO("FHIRDocumentTeleconsulto", "Bundle contenente i dati FHIR relativi alla Relazione collaborativa di Teleconsulto"),
    FHIR_DOCUMENT_TELEMONITORAGGIO("FHIRDocumentTelemonitoraggio", "Bundle contenente i dati FHIR relativi alla Relazione Finale di Telemonitoraggio"),
    FHIR_DOCUMENT_TELEASSISTENZA("FHIRDocumentTeleassistenza", "Bundle contenente i dati FHIR relativi alla Relazione Finale di Teleassistenza");




    private final String templateId;
    private final String documentType;
}