
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per MaterialDangerInflammable.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="MaterialDangerInflammable">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="IFL"/>
 *     &lt;enumeration value="EXP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MaterialDangerInflammable")
@XmlEnum
public enum MaterialDangerInflammable {

    IFL,
    EXP;

    public String value() {
        return name();
    }

    public static MaterialDangerInflammable fromValue(String v) {
        return valueOf(v);
    }

}
