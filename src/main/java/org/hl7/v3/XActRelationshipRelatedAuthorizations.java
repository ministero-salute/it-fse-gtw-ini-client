
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_ActRelationshipRelatedAuthorizations.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_ActRelationshipRelatedAuthorizations">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="AUTH"/>
 *     &lt;enumeration value="REFR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_ActRelationshipRelatedAuthorizations")
@XmlEnum
public enum XActRelationshipRelatedAuthorizations {

    AUTH,
    REFR;

    public String value() {
        return name();
    }

    public static XActRelationshipRelatedAuthorizations fromValue(String v) {
        return valueOf(v);
    }

}
