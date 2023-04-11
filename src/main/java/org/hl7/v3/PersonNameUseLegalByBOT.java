
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per PersonNameUseLegalByBOT.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="PersonNameUseLegalByBOT">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="L"/>
 *     &lt;enumeration value="OR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PersonNameUseLegalByBOT")
@XmlEnum
public enum PersonNameUseLegalByBOT {

    L,
    OR;

    public String value() {
        return name();
    }

    public static PersonNameUseLegalByBOT fromValue(String v) {
        return valueOf(v);
    }

}
