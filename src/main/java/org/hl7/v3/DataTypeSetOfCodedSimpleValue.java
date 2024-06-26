
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeSetOfCodedSimpleValue.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeSetOfCodedSimpleValue">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="SET&lt;CS>"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeSetOfCodedSimpleValue")
@XmlEnum
public enum DataTypeSetOfCodedSimpleValue {

    @XmlEnumValue("SET<CS>")
    SET_CS("SET<CS>");
    private final String value;

    DataTypeSetOfCodedSimpleValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataTypeSetOfCodedSimpleValue fromValue(String v) {
        for (DataTypeSetOfCodedSimpleValue c: DataTypeSetOfCodedSimpleValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
