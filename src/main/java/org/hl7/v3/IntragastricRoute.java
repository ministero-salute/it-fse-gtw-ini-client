
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per IntragastricRoute.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="IntragastricRoute">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="IGASTINSTIL"/>
 *     &lt;enumeration value="IGASTIRR"/>
 *     &lt;enumeration value="IGASTLAV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "IntragastricRoute")
@XmlEnum
public enum IntragastricRoute {

    IGASTINSTIL,
    IGASTIRR,
    IGASTLAV;

    public String value() {
        return name();
    }

    public static IntragastricRoute fromValue(String v) {
        return valueOf(v);
    }

}
