
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_ActOrderableOrBillable.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_ActOrderableOrBillable">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ACCM"/>
 *     &lt;enumeration value="PCPR"/>
 *     &lt;enumeration value="ENC"/>
 *     &lt;enumeration value="SBADM"/>
 *     &lt;enumeration value="TRNS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_ActOrderableOrBillable")
@XmlEnum
public enum XActOrderableOrBillable {

    ACCM,
    PCPR,
    ENC,
    SBADM,
    TRNS;

    public String value() {
        return name();
    }

    public static XActOrderableOrBillable fromValue(String v) {
        return valueOf(v);
    }

}
