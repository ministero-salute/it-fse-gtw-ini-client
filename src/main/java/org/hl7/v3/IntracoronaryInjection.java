
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per IntracoronaryInjection.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="IntracoronaryInjection">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ICORONINJ"/>
 *     &lt;enumeration value="ICORONINJP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "IntracoronaryInjection")
@XmlEnum
public enum IntracoronaryInjection {

    ICORONINJ,
    ICORONINJP;

    public String value() {
        return name();
    }

    public static IntracoronaryInjection fromValue(String v) {
        return valueOf(v);
    }

}
