
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_ActClassCareProvisionProcedure.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_ActClassCareProvisionProcedure">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="PCPR"/>
 *     &lt;enumeration value="PROC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_ActClassCareProvisionProcedure")
@XmlEnum
public enum XActClassCareProvisionProcedure {

    PCPR,
    PROC;

    public String value() {
        return name();
    }

    public static XActClassCareProvisionProcedure fromValue(String v) {
        return valueOf(v);
    }

}
