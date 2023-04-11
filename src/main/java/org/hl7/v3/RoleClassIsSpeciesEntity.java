
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per RoleClassIsSpeciesEntity.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="RoleClassIsSpeciesEntity">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="GEN"/>
 *     &lt;enumeration value="GRIC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RoleClassIsSpeciesEntity")
@XmlEnum
public enum RoleClassIsSpeciesEntity {

    GEN,
    GRIC;

    public String value() {
        return name();
    }

    public static RoleClassIsSpeciesEntity fromValue(String v) {
        return valueOf(v);
    }

}
