
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per AlgorithmicDecisionObservationMethod.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="AlgorithmicDecisionObservationMethod">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ALGM"/>
 *     &lt;enumeration value="BYCL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AlgorithmicDecisionObservationMethod")
@XmlEnum
public enum AlgorithmicDecisionObservationMethod {

    ALGM,
    BYCL;

    public String value() {
        return name();
    }

    public static AlgorithmicDecisionObservationMethod fromValue(String v) {
        return valueOf(v);
    }

}
