
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per EquipmentAlertLevel.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="EquipmentAlertLevel">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="C"/>
 *     &lt;enumeration value="N"/>
 *     &lt;enumeration value="S"/>
 *     &lt;enumeration value="W"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EquipmentAlertLevel")
@XmlEnum
public enum EquipmentAlertLevel {

    C,
    N,
    S,
    W;

    public String value() {
        return name();
    }

    public static EquipmentAlertLevel fromValue(String v) {
        return valueOf(v);
    }

}
