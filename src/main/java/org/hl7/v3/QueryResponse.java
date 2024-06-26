
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per QueryResponse.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="QueryResponse">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="AE"/>
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="NF"/>
 *     &lt;enumeration value="QE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "QueryResponse")
@XmlEnum
public enum QueryResponse {

    AE,
    OK,
    NF,
    QE;

    public String value() {
        return name();
    }

    public static QueryResponse fromValue(String v) {
        return valueOf(v);
    }

}
