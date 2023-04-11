
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ResearchSubjectRoleBasis.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ResearchSubjectRoleBasis">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ERL"/>
 *     &lt;enumeration value="SCN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ResearchSubjectRoleBasis")
@XmlEnum
public enum ResearchSubjectRoleBasis {

    ERL,
    SCN;

    public String value() {
        return name();
    }

    public static ResearchSubjectRoleBasis fromValue(String v) {
        return valueOf(v);
    }

}
