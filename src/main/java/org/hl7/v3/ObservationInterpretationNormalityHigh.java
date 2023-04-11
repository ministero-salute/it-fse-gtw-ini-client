
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ObservationInterpretationNormalityHigh.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ObservationInterpretationNormalityHigh">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="H"/>
 *     &lt;enumeration value="HH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ObservationInterpretationNormalityHigh")
@XmlEnum
public enum ObservationInterpretationNormalityHigh {

    H,
    HH;

    public String value() {
        return name();
    }

    public static ObservationInterpretationNormalityHigh fromValue(String v) {
        return valueOf(v);
    }

}
