
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ObservationInterpretationNormalityAlert.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ObservationInterpretationNormalityAlert">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="AA"/>
 *     &lt;enumeration value="HH"/>
 *     &lt;enumeration value="LL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ObservationInterpretationNormalityAlert")
@XmlEnum
public enum ObservationInterpretationNormalityAlert {

    AA,
    HH,
    LL;

    public String value() {
        return name();
    }

    public static ObservationInterpretationNormalityAlert fromValue(String v) {
        return valueOf(v);
    }

}
