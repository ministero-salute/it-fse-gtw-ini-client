
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_RoleClassAccommodationRequestor.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_RoleClassAccommodationRequestor">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="AGNT"/>
 *     &lt;enumeration value="PROV"/>
 *     &lt;enumeration value="PAT"/>
 *     &lt;enumeration value="PRS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_RoleClassAccommodationRequestor")
@XmlEnum
public enum XRoleClassAccommodationRequestor {

    AGNT,
    PROV,
    PAT,
    PRS;

    public String value() {
        return name();
    }

    public static XRoleClassAccommodationRequestor fromValue(String v) {
        return valueOf(v);
    }

}
