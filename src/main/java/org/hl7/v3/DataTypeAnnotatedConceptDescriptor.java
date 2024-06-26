
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeAnnotatedConceptDescriptor.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeAnnotatedConceptDescriptor">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ANT&lt;CD>"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeAnnotatedConceptDescriptor")
@XmlEnum
public enum DataTypeAnnotatedConceptDescriptor {

    @XmlEnumValue("ANT<CD>")
    ANT_CD("ANT<CD>");
    private final String value;

    DataTypeAnnotatedConceptDescriptor(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataTypeAnnotatedConceptDescriptor fromValue(String v) {
        for (DataTypeAnnotatedConceptDescriptor c: DataTypeAnnotatedConceptDescriptor.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
