
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per IntraarterialInjection.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="IntraarterialInjection">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="IAINJ"/>
 *     &lt;enumeration value="IAINJP"/>
 *     &lt;enumeration value="IAINJSP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "IntraarterialInjection")
@XmlEnum
public enum IntraarterialInjection {

    IAINJ,
    IAINJP,
    IAINJSP;

    public String value() {
        return name();
    }

    public static IntraarterialInjection fromValue(String v) {
        return valueOf(v);
    }

}
