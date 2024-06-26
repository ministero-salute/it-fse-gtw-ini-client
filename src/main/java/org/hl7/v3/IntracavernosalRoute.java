
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per IntracavernosalRoute.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="IntracavernosalRoute">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ICAVINJ"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "IntracavernosalRoute")
@XmlEnum
public enum IntracavernosalRoute {

    ICAVINJ;

    public String value() {
        return name();
    }

    public static IntracavernosalRoute fromValue(String v) {
        return valueOf(v);
    }

}
