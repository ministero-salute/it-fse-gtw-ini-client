
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per TransmissionRelationshipTypeCode.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="TransmissionRelationshipTypeCode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="SEQL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransmissionRelationshipTypeCode")
@XmlEnum
public enum TransmissionRelationshipTypeCode {

    SEQL;

    public String value() {
        return name();
    }

    public static TransmissionRelationshipTypeCode fromValue(String v) {
        return valueOf(v);
    }

}
