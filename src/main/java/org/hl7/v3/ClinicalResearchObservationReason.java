
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ClinicalResearchObservationReason.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ClinicalResearchObservationReason">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="NPT"/>
 *     &lt;enumeration value="UPT"/>
 *     &lt;enumeration value="PPT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClinicalResearchObservationReason")
@XmlEnum
public enum ClinicalResearchObservationReason {

    NPT,
    UPT,
    PPT;

    public String value() {
        return name();
    }

    public static ClinicalResearchObservationReason fromValue(String v) {
        return valueOf(v);
    }

}
