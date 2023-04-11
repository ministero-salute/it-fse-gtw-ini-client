
package oasis.names.tc.ebxml_regrep.xsd.rim._3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * TelephoneNumber is the mapping of the same named interface in ebRIM.
 * 
 * <p>Classe Java per TelephoneNumberType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="TelephoneNumberType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="areaCode" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}String8" />
 *       &lt;attribute name="countryCode" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}String8" />
 *       &lt;attribute name="extension" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}String8" />
 *       &lt;attribute name="number" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}String16" />
 *       &lt;attribute name="phoneType" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}String32" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TelephoneNumberType")
public class TelephoneNumberType {

    @XmlAttribute(name = "areaCode")
    protected String areaCode;
    @XmlAttribute(name = "countryCode")
    protected String countryCode;
    @XmlAttribute(name = "extension")
    protected String extension;
    @XmlAttribute(name = "number")
    protected String number;
    @XmlAttribute(name = "phoneType")
    protected String phoneType;

    /**
     * Recupera il valore della proprietà areaCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Imposta il valore della proprietà areaCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAreaCode(String value) {
        this.areaCode = value;
    }

    /**
     * Recupera il valore della proprietà countryCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Imposta il valore della proprietà countryCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Recupera il valore della proprietà extension.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Imposta il valore della proprietà extension.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Recupera il valore della proprietà number.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Imposta il valore della proprietà number.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Recupera il valore della proprietà phoneType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneType() {
        return phoneType;
    }

    /**
     * Imposta il valore della proprietà phoneType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneType(String value) {
        this.phoneType = value;
    }

}
