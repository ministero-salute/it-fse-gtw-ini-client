
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DoseHighDetectedIssueCode.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DoseHighDetectedIssueCode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="DOSEH"/>
 *     &lt;enumeration value="DOSEHINDA"/>
 *     &lt;enumeration value="DOSEHINDSA"/>
 *     &lt;enumeration value="DOSEHIND"/>
 *     &lt;enumeration value="DOSEHINDW"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DoseHighDetectedIssueCode")
@XmlEnum
public enum DoseHighDetectedIssueCode {

    DOSEH,
    DOSEHINDA,
    DOSEHINDSA,
    DOSEHIND,
    DOSEHINDW;

    public String value() {
        return name();
    }

    public static DoseHighDetectedIssueCode fromValue(String v) {
        return valueOf(v);
    }

}
