
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per StudentRoleType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="StudentRoleType">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="STUD"/>
 *     &lt;enumeration value="FSTUD"/>
 *     &lt;enumeration value="PSTUD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StudentRoleType")
@XmlEnum
public enum StudentRoleType {

    STUD,
    FSTUD,
    PSTUD;

    public String value() {
        return name();
    }

    public static StudentRoleType fromValue(String v) {
        return valueOf(v);
    }

}
