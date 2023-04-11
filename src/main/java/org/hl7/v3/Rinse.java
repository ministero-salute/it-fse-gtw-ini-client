
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Rinse.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="Rinse">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="DENRINSE"/>
 *     &lt;enumeration value="ORRINSE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Rinse")
@XmlEnum
public enum Rinse {

    DENRINSE,
    ORRINSE;

    public String value() {
        return name();
    }

    public static Rinse fromValue(String v) {
        return valueOf(v);
    }

}
