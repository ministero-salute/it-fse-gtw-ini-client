
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_ClinicalStatementSupplyMood.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_ClinicalStatementSupplyMood">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="EVN"/>
 *     &lt;enumeration value="INT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_ClinicalStatementSupplyMood")
@XmlEnum
public enum XClinicalStatementSupplyMood {

    EVN,
    INT;

    public String value() {
        return name();
    }

    public static XClinicalStatementSupplyMood fromValue(String v) {
        return valueOf(v);
    }

}
