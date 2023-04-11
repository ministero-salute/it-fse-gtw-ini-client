
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per InclusionNotRequired.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="InclusionNotRequired">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="NR"/>
 *     &lt;enumeration value="X"/>
 *     &lt;enumeration value="RE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "InclusionNotRequired")
@XmlEnum
public enum InclusionNotRequired {

    NR,
    X,
    RE;

    public String value() {
        return name();
    }

    public static InclusionNotRequired fromValue(String v) {
        return valueOf(v);
    }

}
