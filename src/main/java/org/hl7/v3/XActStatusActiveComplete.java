
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_ActStatusActiveComplete.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_ActStatusActiveComplete">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="active"/>
 *     &lt;enumeration value="completed"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_ActStatusActiveComplete")
@XmlEnum
public enum XActStatusActiveComplete {

    @XmlEnumValue("active")
    ACTIVE("active"),
    @XmlEnumValue("completed")
    COMPLETED("completed");
    private final String value;

    XActStatusActiveComplete(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static XActStatusActiveComplete fromValue(String v) {
        for (XActStatusActiveComplete c: XActStatusActiveComplete.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
