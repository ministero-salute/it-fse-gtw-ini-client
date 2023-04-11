
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per CentralSalish.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CentralSalish">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="x-CLM"/>
 *     &lt;enumeration value="x-LUT"/>
 *     &lt;enumeration value="x-STR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CentralSalish")
@XmlEnum
public enum CentralSalish {

    @XmlEnumValue("x-CLM")
    X_CLM("x-CLM"),
    @XmlEnumValue("x-LUT")
    X_LUT("x-LUT"),
    @XmlEnumValue("x-STR")
    X_STR("x-STR");
    private final String value;

    CentralSalish(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CentralSalish fromValue(String v) {
        for (CentralSalish c: CentralSalish.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
