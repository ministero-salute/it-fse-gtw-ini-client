
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_ServiceEventPerformer.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_ServiceEventPerformer">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="PRF"/>
 *     &lt;enumeration value="SPRF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_ServiceEventPerformer")
@XmlEnum
public enum XServiceEventPerformer {

    PRF,
    SPRF;

    public String value() {
        return name();
    }

    public static XServiceEventPerformer fromValue(String v) {
        return valueOf(v);
    }

}
