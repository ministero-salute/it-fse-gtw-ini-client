
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per RefillFirstHerePharmacySupplyType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="RefillFirstHerePharmacySupplyType">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="RFF"/>
 *     &lt;enumeration value="RFFS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RefillFirstHerePharmacySupplyType")
@XmlEnum
public enum RefillFirstHerePharmacySupplyType {

    RFF,
    RFFS;

    public String value() {
        return name();
    }

    public static RefillFirstHerePharmacySupplyType fromValue(String v) {
        return valueOf(v);
    }

}
