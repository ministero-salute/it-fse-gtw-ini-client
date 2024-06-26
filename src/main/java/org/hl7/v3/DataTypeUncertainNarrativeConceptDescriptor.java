
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeUncertainNarrativeConceptDescriptor.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeUncertainNarrativeConceptDescriptor">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="UVN&lt;CD>"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeUncertainNarrativeConceptDescriptor")
@XmlEnum
public enum DataTypeUncertainNarrativeConceptDescriptor {

    @XmlEnumValue("UVN<CD>")
    UVN_CD("UVN<CD>");
    private final String value;

    DataTypeUncertainNarrativeConceptDescriptor(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataTypeUncertainNarrativeConceptDescriptor fromValue(String v) {
        for (DataTypeUncertainNarrativeConceptDescriptor c: DataTypeUncertainNarrativeConceptDescriptor.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
