
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per CodeSystemType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CodeSystemType">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="E"/>
 *     &lt;enumeration value="EI"/>
 *     &lt;enumeration value="I"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CodeSystemType")
@XmlEnum
public enum CodeSystemType {

    E,
    EI,
    I;

    public String value() {
        return name();
    }

    public static CodeSystemType fromValue(String v) {
        return valueOf(v);
    }

}
