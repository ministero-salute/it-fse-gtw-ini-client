
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per IDClinPracticeSetting.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="IDClinPracticeSetting">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="INFD"/>
 *     &lt;enumeration value="PEDID"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "IDClinPracticeSetting")
@XmlEnum
public enum IDClinPracticeSetting {

    INFD,
    PEDID;

    public String value() {
        return name();
    }

    public static IDClinPracticeSetting fromValue(String v) {
        return valueOf(v);
    }

}
