
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Ojibwayan.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="Ojibwayan">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="x-POT"/>
 *     &lt;enumeration value="x-OJB"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Ojibwayan")
@XmlEnum
public enum Ojibwayan {

    @XmlEnumValue("x-POT")
    X_POT("x-POT"),
    @XmlEnumValue("x-OJB")
    X_OJB("x-OJB");
    private final String value;

    Ojibwayan(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Ojibwayan fromValue(String v) {
        for (Ojibwayan c: Ojibwayan.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
