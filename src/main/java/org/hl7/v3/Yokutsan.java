
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Yokutsan.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="Yokutsan">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="x-GSH"/>
 *     &lt;enumeration value="x-ENH"/>
 *     &lt;enumeration value="x-PYL"/>
 *     &lt;enumeration value="x-TKH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Yokutsan")
@XmlEnum
public enum Yokutsan {

    @XmlEnumValue("x-GSH")
    X_GSH("x-GSH"),
    @XmlEnumValue("x-ENH")
    X_ENH("x-ENH"),
    @XmlEnumValue("x-PYL")
    X_PYL("x-PYL"),
    @XmlEnumValue("x-TKH")
    X_TKH("x-TKH");
    private final String value;

    Yokutsan(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Yokutsan fromValue(String v) {
        for (Yokutsan c: Yokutsan.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
