
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeParametricProbabilityDistributionOfRealNumbers.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeParametricProbabilityDistributionOfRealNumbers">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="PPD&lt;REAL>"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeParametricProbabilityDistributionOfRealNumbers")
@XmlEnum
public enum DataTypeParametricProbabilityDistributionOfRealNumbers {

    @XmlEnumValue("PPD<REAL>")
    PPD_REAL("PPD<REAL>");
    private final String value;

    DataTypeParametricProbabilityDistributionOfRealNumbers(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataTypeParametricProbabilityDistributionOfRealNumbers fromValue(String v) {
        for (DataTypeParametricProbabilityDistributionOfRealNumbers c: DataTypeParametricProbabilityDistributionOfRealNumbers.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
