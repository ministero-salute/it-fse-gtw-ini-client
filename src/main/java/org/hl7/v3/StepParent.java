
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per StepParent.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="StepParent">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="STPPRN"/>
 *     &lt;enumeration value="STPFTH"/>
 *     &lt;enumeration value="STPMTH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StepParent")
@XmlEnum
public enum StepParent {

    STPPRN,
    STPFTH,
    STPMTH;

    public String value() {
        return name();
    }

    public static StepParent fromValue(String v) {
        return valueOf(v);
    }

}
