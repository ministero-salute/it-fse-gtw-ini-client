/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
    VAC("2.16.840.1.113883.2.9.10.1.11.1.1", "Scheda della singola Vaccinazione");

    private final String templateId;
    private final String documentType;
}