
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActClassExtract.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ActClassExtract">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="EXTRACT"/>
 *     &lt;enumeration value="EHR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActClassExtract")
@XmlEnum
public enum ActClassExtract {

    EXTRACT,
    EHR;

    public String value() {
        return name();
    }

    public static ActClassExtract fromValue(String v) {
        return valueOf(v);
    }

}
