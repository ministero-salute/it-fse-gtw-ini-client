
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per RoleClassEmployee.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="RoleClassEmployee">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="EMP"/>
 *     &lt;enumeration value="MIL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RoleClassEmployee")
@XmlEnum
public enum RoleClassEmployee {

    EMP,
    MIL;

    public String value() {
        return name();
    }

    public static RoleClassEmployee fromValue(String v) {
        return valueOf(v);
    }

}
