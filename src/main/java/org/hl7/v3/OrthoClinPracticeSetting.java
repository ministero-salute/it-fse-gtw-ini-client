
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per OrthoClinPracticeSetting.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="OrthoClinPracticeSetting">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ORTHO"/>
 *     &lt;enumeration value="HAND"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "OrthoClinPracticeSetting")
@XmlEnum
public enum OrthoClinPracticeSetting {

    ORTHO,
    HAND;

    public String value() {
        return name();
    }

    public static OrthoClinPracticeSetting fromValue(String v) {
        return valueOf(v);
    }

}
