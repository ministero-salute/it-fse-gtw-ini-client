
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_RoleClassPayeePolicyRelationship.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_RoleClassPayeePolicyRelationship">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="COVPTY"/>
 *     &lt;enumeration value="GUAR"/>
 *     &lt;enumeration value="PROV"/>
 *     &lt;enumeration value="PRS"/>
 *     &lt;enumeration value="POLHOLD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_RoleClassPayeePolicyRelationship")
@XmlEnum
public enum XRoleClassPayeePolicyRelationship {

    COVPTY,
    GUAR,
    PROV,
    PRS,
    POLHOLD;

    public String value() {
        return name();
    }

    public static XRoleClassPayeePolicyRelationship fromValue(String v) {
        return valueOf(v);
    }

}
