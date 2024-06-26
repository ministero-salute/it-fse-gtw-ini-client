
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DocumentCompletion.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentCompletion">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="AU"/>
 *     &lt;enumeration value="DI"/>
 *     &lt;enumeration value="DO"/>
 *     &lt;enumeration value="IP"/>
 *     &lt;enumeration value="IN"/>
 *     &lt;enumeration value="LA"/>
 *     &lt;enumeration value="PA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DocumentCompletion")
@XmlEnum
public enum DocumentCompletion {

    AU,
    DI,
    DO,
    IP,
    IN,
    LA,
    PA;

    public String value() {
        return name();
    }

    public static DocumentCompletion fromValue(String v) {
        return valueOf(v);
    }

}
