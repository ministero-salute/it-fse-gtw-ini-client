
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActFinancialTransactionCode.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ActFinancialTransactionCode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="CHRG"/>
 *     &lt;enumeration value="REV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActFinancialTransactionCode")
@XmlEnum
public enum ActFinancialTransactionCode {

    CHRG,
    REV;

    public String value() {
        return name();
    }

    public static ActFinancialTransactionCode fromValue(String v) {
        return valueOf(v);
    }

}
