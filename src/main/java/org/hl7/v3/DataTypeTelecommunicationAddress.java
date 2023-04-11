
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DataTypeTelecommunicationAddress.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DataTypeTelecommunicationAddress">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="TEL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataTypeTelecommunicationAddress")
@XmlEnum
public enum DataTypeTelecommunicationAddress {

    TEL;

    public String value() {
        return name();
    }

    public static DataTypeTelecommunicationAddress fromValue(String v) {
        return valueOf(v);
    }

}
