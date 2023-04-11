
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeIntervalOfIntegerNumbers.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeIntervalOfIntegerNumbers">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="IVL&lt;INT>"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeIntervalOfIntegerNumbers")
@XmlEnum
public enum DataTypeIntervalOfIntegerNumbers {

    @XmlEnumValue("IVL<INT>")
    IVL_INT("IVL<INT>");
    private final String value;

    DataTypeIntervalOfIntegerNumbers(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataTypeIntervalOfIntegerNumbers fromValue(String v) {
        for (DataTypeIntervalOfIntegerNumbers c: DataTypeIntervalOfIntegerNumbers.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
