
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per FosterChild.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="FosterChild">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="CHLDFOST"/>
 *     &lt;enumeration value="DAUFOST"/>
 *     &lt;enumeration value="SONFOST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FosterChild")
@XmlEnum
public enum FosterChild {

    CHLDFOST,
    DAUFOST,
    SONFOST;

    public String value() {
        return name();
    }

    public static FosterChild fromValue(String v) {
        return valueOf(v);
    }

}
