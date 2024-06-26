
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_PhysicalVerbalParticipationMode.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_PhysicalVerbalParticipationMode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="PHYSICAL"/>
 *     &lt;enumeration value="VERBAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_PhysicalVerbalParticipationMode")
@XmlEnum
public enum XPhysicalVerbalParticipationMode {

    PHYSICAL,
    VERBAL;

    public String value() {
        return name();
    }

    public static XPhysicalVerbalParticipationMode fromValue(String v) {
        return valueOf(v);
    }

}
