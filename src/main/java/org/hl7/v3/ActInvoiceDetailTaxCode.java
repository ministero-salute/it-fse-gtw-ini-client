
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActInvoiceDetailTaxCode.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ActInvoiceDetailTaxCode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="FST"/>
 *     &lt;enumeration value="HST"/>
 *     &lt;enumeration value="PST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActInvoiceDetailTaxCode")
@XmlEnum
public enum ActInvoiceDetailTaxCode {

    FST,
    HST,
    PST;

    public String value() {
        return name();
    }

    public static ActInvoiceDetailTaxCode fromValue(String v) {
        return valueOf(v);
    }

}
