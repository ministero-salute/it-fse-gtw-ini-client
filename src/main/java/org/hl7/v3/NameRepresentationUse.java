
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per NameRepresentationUse.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="NameRepresentationUse">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ABC"/>
 *     &lt;enumeration value="IDE"/>
 *     &lt;enumeration value="SYL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NameRepresentationUse")
@XmlEnum
public enum NameRepresentationUse {

    ABC,
    IDE,
    SYL;

    public String value() {
        return name();
    }

    public static NameRepresentationUse fromValue(String v) {
        return valueOf(v);
    }

}
