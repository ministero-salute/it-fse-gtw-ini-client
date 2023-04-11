
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeCodedSimpleValue.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeCodedSimpleValue">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="CS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeCodedSimpleValue")
@XmlEnum
public enum DataTypeCodedSimpleValue {

    CS;

    public String value() {
        return name();
    }

    public static DataTypeCodedSimpleValue fromValue(String v) {
        return valueOf(v);
    }

}
