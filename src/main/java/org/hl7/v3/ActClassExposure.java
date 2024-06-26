
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActClassExposure.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ActClassExposure">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="EXPOS"/>
 *     &lt;enumeration value="AEXPOS"/>
 *     &lt;enumeration value="TEXPOS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActClassExposure")
@XmlEnum
public enum ActClassExposure {

    EXPOS,
    AEXPOS,
    TEXPOS;

    public String value() {
        return name();
    }

    public static ActClassExposure fromValue(String v) {
        return valueOf(v);
    }

}
