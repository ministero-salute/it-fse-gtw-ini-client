
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActBillableModifierCode.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ActBillableModifierCode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="CPTM"/>
 *     &lt;enumeration value="HCPCSA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActBillableModifierCode")
@XmlEnum
public enum ActBillableModifierCode {

    CPTM,
    HCPCSA;

    public String value() {
        return name();
    }

    public static ActBillableModifierCode fromValue(String v) {
        return valueOf(v);
    }

}
