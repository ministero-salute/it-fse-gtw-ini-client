
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ParticipationTargetDevice.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ParticipationTargetDevice">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="DEV"/>
 *     &lt;enumeration value="NRD"/>
 *     &lt;enumeration value="RDV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ParticipationTargetDevice")
@XmlEnum
public enum ParticipationTargetDevice {

    DEV,
    NRD,
    RDV;

    public String value() {
        return name();
    }

    public static ParticipationTargetDevice fromValue(String v) {
        return valueOf(v);
    }

}
