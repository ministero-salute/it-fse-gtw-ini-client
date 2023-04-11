
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per LacrimalPunctaRoute.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="LacrimalPunctaRoute">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="LPINS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LacrimalPunctaRoute")
@XmlEnum
public enum LacrimalPunctaRoute {

    LPINS;

    public String value() {
        return name();
    }

    public static LacrimalPunctaRoute fromValue(String v) {
        return valueOf(v);
    }

}
