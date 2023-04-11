
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ChildInLaw.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ChildInLaw">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="CHLDINLAW"/>
 *     &lt;enumeration value="DAUINLAW"/>
 *     &lt;enumeration value="SONINLAW"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ChildInLaw")
@XmlEnum
public enum ChildInLaw {

    CHLDINLAW,
    DAUINLAW,
    SONINLAW;

    public String value() {
        return name();
    }

    public static ChildInLaw fromValue(String v) {
        return valueOf(v);
    }

}
