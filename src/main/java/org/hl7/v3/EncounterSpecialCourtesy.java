
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per EncounterSpecialCourtesy.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="EncounterSpecialCourtesy">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="EXT"/>
 *     &lt;enumeration value="NRM"/>
 *     &lt;enumeration value="PRF"/>
 *     &lt;enumeration value="STF"/>
 *     &lt;enumeration value="VIP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EncounterSpecialCourtesy")
@XmlEnum
public enum EncounterSpecialCourtesy {

    EXT,
    NRM,
    PRF,
    STF,
    VIP;

    public String value() {
        return name();
    }

    public static EncounterSpecialCourtesy fromValue(String v) {
        return valueOf(v);
    }

}
