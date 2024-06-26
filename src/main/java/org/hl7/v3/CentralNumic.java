
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per CentralNumic.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CentralNumic">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="x-COM"/>
 *     &lt;enumeration value="x-PAR"/>
 *     &lt;enumeration value="x-SHH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CentralNumic")
@XmlEnum
public enum CentralNumic {

    @XmlEnumValue("x-COM")
    X_COM("x-COM"),
    @XmlEnumValue("x-PAR")
    X_PAR("x-PAR"),
    @XmlEnumValue("x-SHH")
    X_SHH("x-SHH");
    private final String value;

    CentralNumic(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CentralNumic fromValue(String v) {
        for (CentralNumic c: CentralNumic.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
