
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActPaymentCode.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ActPaymentCode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ACH"/>
 *     &lt;enumeration value="CHK"/>
 *     &lt;enumeration value="DDP"/>
 *     &lt;enumeration value="NON"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActPaymentCode")
@XmlEnum
public enum ActPaymentCode {

    ACH,
    CHK,
    DDP,
    NON;

    public String value() {
        return name();
    }

    public static ActPaymentCode fromValue(String v) {
        return valueOf(v);
    }

}
