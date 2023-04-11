
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_SUCC_REPL_PREV.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_SUCC_REPL_PREV">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="PREV"/>
 *     &lt;enumeration value="RPLC"/>
 *     &lt;enumeration value="SUCC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_SUCC_REPL_PREV")
@XmlEnum
public enum XSUCCREPLPREV {

    PREV,
    RPLC,
    SUCC;

    public String value() {
        return name();
    }

    public static XSUCCREPLPREV fromValue(String v) {
        return valueOf(v);
    }

}
