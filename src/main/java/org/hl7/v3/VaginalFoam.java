
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per VaginalFoam.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="VaginalFoam">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="VAGFOAM"/>
 *     &lt;enumeration value="VAGFOAMAPL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "VaginalFoam")
@XmlEnum
public enum VaginalFoam {

    VAGFOAM,
    VAGFOAMAPL;

    public String value() {
        return name();
    }

    public static VaginalFoam fromValue(String v) {
        return valueOf(v);
    }

}
