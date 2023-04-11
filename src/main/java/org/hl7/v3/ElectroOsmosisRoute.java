
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ElectroOsmosisRoute.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ElectroOsmosisRoute">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ELECTOSMOS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ElectroOsmosisRoute")
@XmlEnum
public enum ElectroOsmosisRoute {

    ELECTOSMOS;

    public String value() {
        return name();
    }

    public static ElectroOsmosisRoute fromValue(String v) {
        return valueOf(v);
    }

}
