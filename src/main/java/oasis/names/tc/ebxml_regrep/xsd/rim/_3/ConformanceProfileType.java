
package oasis.names.tc.ebxml_regrep.xsd.rim._3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConformanceProfileType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ConformanceProfileType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="registryFull"/>
 *     &lt;enumeration value="registryLite"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ConformanceProfileType")
@XmlEnum
public enum ConformanceProfileType {

    @XmlEnumValue("registryFull")
    REGISTRY_FULL("registryFull"),
    @XmlEnumValue("registryLite")
    REGISTRY_LITE("registryLite");
    private final String value;

    ConformanceProfileType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ConformanceProfileType fromValue(String v) {
        for (ConformanceProfileType c: ConformanceProfileType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
