
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per LiquidCleanser.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="LiquidCleanser">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="LIQCLN"/>
 *     &lt;enumeration value="LIQSOAP"/>
 *     &lt;enumeration value="SHMP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LiquidCleanser")
@XmlEnum
public enum LiquidCleanser {

    LIQCLN,
    LIQSOAP,
    SHMP;

    public String value() {
        return name();
    }

    public static LiquidCleanser fromValue(String v) {
        return valueOf(v);
    }

}
