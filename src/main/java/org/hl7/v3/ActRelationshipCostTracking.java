
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActRelationshipCostTracking.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ActRelationshipCostTracking">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="CHRG"/>
 *     &lt;enumeration value="COST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActRelationshipCostTracking")
@XmlEnum
public enum ActRelationshipCostTracking {

    CHRG,
    COST;

    public String value() {
        return name();
    }

    public static ActRelationshipCostTracking fromValue(String v) {
        return valueOf(v);
    }

}
