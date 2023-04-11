
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per AudioMediaType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="AudioMediaType">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="audio/basic"/>
 *     &lt;enumeration value="audio/k32adpcm"/>
 *     &lt;enumeration value="audio/mpeg"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AudioMediaType")
@XmlEnum
public enum AudioMediaType {

    @XmlEnumValue("audio/basic")
    AUDIO_BASIC("audio/basic"),
    @XmlEnumValue("audio/k32adpcm")
    AUDIO_K_32_ADPCM("audio/k32adpcm"),
    @XmlEnumValue("audio/mpeg")
    AUDIO_MPEG("audio/mpeg");
    private final String value;

    AudioMediaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AudioMediaType fromValue(String v) {
        for (AudioMediaType c: AudioMediaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
