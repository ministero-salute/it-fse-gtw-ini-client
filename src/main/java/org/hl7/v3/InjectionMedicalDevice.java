
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per InjectionMedicalDevice.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="InjectionMedicalDevice">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="AINJ"/>
 *     &lt;enumeration value="PEN"/>
 *     &lt;enumeration value="SYR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "InjectionMedicalDevice")
@XmlEnum
public enum InjectionMedicalDevice {

    AINJ,
    PEN,
    SYR;

    public String value() {
        return name();
    }

    public static InjectionMedicalDevice fromValue(String v) {
        return valueOf(v);
    }

}
