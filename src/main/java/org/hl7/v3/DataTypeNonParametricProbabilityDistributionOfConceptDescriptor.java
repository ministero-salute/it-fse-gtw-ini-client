
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeNonParametricProbabilityDistributionOfConceptDescriptor.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeNonParametricProbabilityDistributionOfConceptDescriptor">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="NPPD&lt;CD>"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeNonParametricProbabilityDistributionOfConceptDescriptor")
@XmlEnum
public enum DataTypeNonParametricProbabilityDistributionOfConceptDescriptor {

    @XmlEnumValue("NPPD<CD>")
    NPPD_CD("NPPD<CD>");
    private final String value;

    DataTypeNonParametricProbabilityDistributionOfConceptDescriptor(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataTypeNonParametricProbabilityDistributionOfConceptDescriptor fromValue(String v) {
        for (DataTypeNonParametricProbabilityDistributionOfConceptDescriptor c: DataTypeNonParametricProbabilityDistributionOfConceptDescriptor.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
