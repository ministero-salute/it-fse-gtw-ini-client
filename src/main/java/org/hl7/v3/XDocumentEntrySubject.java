
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per x_DocumentEntrySubject.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="x_DocumentEntrySubject">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="PAT"/>
 *     &lt;enumeration value="PRS"/>
 *     &lt;enumeration value="SPEC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "x_DocumentEntrySubject")
@XmlEnum
public enum XDocumentEntrySubject {

    PAT,
    PRS,
    SPEC;

    public String value() {
        return name();
    }

    public static XDocumentEntrySubject fromValue(String v) {
        return valueOf(v);
    }

}
