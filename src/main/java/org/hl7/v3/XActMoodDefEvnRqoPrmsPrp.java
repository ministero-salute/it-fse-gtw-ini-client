
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_ActMoodDefEvnRqoPrmsPrp.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_ActMoodDefEvnRqoPrmsPrp">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="DEF"/>
 *     &lt;enumeration value="EVN"/>
 *     &lt;enumeration value="PRMS"/>
 *     &lt;enumeration value="PRP"/>
 *     &lt;enumeration value="RQO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_ActMoodDefEvnRqoPrmsPrp")
@XmlEnum
public enum XActMoodDefEvnRqoPrmsPrp {

    DEF,
    EVN,
    PRMS,
    PRP,
    RQO;

    public String value() {
        return name();
    }

    public static XActMoodDefEvnRqoPrmsPrp fromValue(String v) {
        return valueOf(v);
    }

}
