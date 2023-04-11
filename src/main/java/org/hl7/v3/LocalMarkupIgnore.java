
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per LocalMarkupIgnore.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="LocalMarkupIgnore">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="all"/>
 *     &lt;enumeration value="markup"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LocalMarkupIgnore")
@XmlEnum
public enum LocalMarkupIgnore {

    @XmlEnumValue("all")
    ALL("all"),
    @XmlEnumValue("markup")
    MARKUP("markup");
    private final String value;

    LocalMarkupIgnore(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LocalMarkupIgnore fromValue(String v) {
        for (LocalMarkupIgnore c: LocalMarkupIgnore.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
