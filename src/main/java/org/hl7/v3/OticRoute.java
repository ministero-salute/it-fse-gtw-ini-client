
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per OticRoute.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="OticRoute">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="OT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "OticRoute")
@XmlEnum
public enum OticRoute {

    OT;

    public String value() {
        return name();
    }

    public static OticRoute fromValue(String v) {
        return valueOf(v);
    }

}
