
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActAdjudicationResultActionCode.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ActAdjudicationResultActionCode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="DISPLAY"/>
 *     &lt;enumeration value="FORM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActAdjudicationResultActionCode")
@XmlEnum
public enum ActAdjudicationResultActionCode {

    DISPLAY,
    FORM;

    public String value() {
        return name();
    }

    public static ActAdjudicationResultActionCode fromValue(String v) {
        return valueOf(v);
    }

}
