
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Kalapuyan.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="Kalapuyan">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="x-KAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Kalapuyan")
@XmlEnum
public enum Kalapuyan {

    @XmlEnumValue("x-KAL")
    X_KAL("x-KAL");
    private final String value;

    Kalapuyan(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Kalapuyan fromValue(String v) {
        for (Kalapuyan c: Kalapuyan.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
