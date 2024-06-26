
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per IntracisternalRoute.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="IntracisternalRoute">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ICISTERNINJ"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "IntracisternalRoute")
@XmlEnum
public enum IntracisternalRoute {

    ICISTERNINJ;

    public String value() {
        return name();
    }

    public static IntracisternalRoute fromValue(String v) {
        return valueOf(v);
    }

}
