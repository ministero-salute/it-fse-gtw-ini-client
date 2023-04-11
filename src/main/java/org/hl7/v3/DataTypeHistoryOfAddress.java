
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeHistoryOfAddress.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeHistoryOfAddress">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="HIST&lt;AD>"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeHistoryOfAddress")
@XmlEnum
public enum DataTypeHistoryOfAddress {

    @XmlEnumValue("HIST<AD>")
    HIST_AD("HIST<AD>");
    private final String value;

    DataTypeHistoryOfAddress(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataTypeHistoryOfAddress fromValue(String v) {
        for (DataTypeHistoryOfAddress c: DataTypeHistoryOfAddress.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
