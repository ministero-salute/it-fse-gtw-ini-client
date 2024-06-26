
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per RefillCompletePharmacySupplyType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="RefillCompletePharmacySupplyType">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="RFC"/>
 *     &lt;enumeration value="RFCS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RefillCompletePharmacySupplyType")
@XmlEnum
public enum RefillCompletePharmacySupplyType {

    RFC,
    RFCS;

    public String value() {
        return name();
    }

    public static RefillCompletePharmacySupplyType fromValue(String v) {
        return valueOf(v);
    }

}
