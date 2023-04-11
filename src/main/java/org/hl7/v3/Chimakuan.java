
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Chimakuan.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="Chimakuan">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="x-QUI"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Chimakuan")
@XmlEnum
public enum Chimakuan {

    @XmlEnumValue("x-QUI")
    X_QUI("x-QUI");
    private final String value;

    Chimakuan(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Chimakuan fromValue(String v) {
        for (Chimakuan c: Chimakuan.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
