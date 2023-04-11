
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeObjectIdentifier.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeObjectIdentifier">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="OID"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeObjectIdentifier")
@XmlEnum
public enum DataTypeObjectIdentifier {

    OID;

    public String value() {
        return name();
    }

    public static DataTypeObjectIdentifier fromValue(String v) {
        return valueOf(v);
    }

}
