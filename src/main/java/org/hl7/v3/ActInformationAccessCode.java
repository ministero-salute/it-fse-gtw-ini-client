
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActInformationAccessCode.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ActInformationAccessCode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ACADR"/>
 *     &lt;enumeration value="ACALLG"/>
 *     &lt;enumeration value="ACOBS"/>
 *     &lt;enumeration value="ACDEMO"/>
 *     &lt;enumeration value="ACIMMUN"/>
 *     &lt;enumeration value="ACLAB"/>
 *     &lt;enumeration value="ACMEDC"/>
 *     &lt;enumeration value="ACMED"/>
 *     &lt;enumeration value="ACPOLPRG"/>
 *     &lt;enumeration value="ACPSERV"/>
 *     &lt;enumeration value="ACPROV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActInformationAccessCode")
@XmlEnum
public enum ActInformationAccessCode {

    ACADR,
    ACALLG,
    ACOBS,
    ACDEMO,
    ACIMMUN,
    ACLAB,
    ACMEDC,
    ACMED,
    ACPOLPRG,
    ACPSERV,
    ACPROV;

    public String value() {
        return name();
    }

    public static ActInformationAccessCode fromValue(String v) {
        return valueOf(v);
    }

}
