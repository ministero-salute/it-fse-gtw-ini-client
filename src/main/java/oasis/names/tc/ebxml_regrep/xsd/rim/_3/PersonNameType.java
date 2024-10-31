
package oasis.names.tc.ebxml_regrep.xsd.rim._3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Mapping of the same named interface in ebRIM.
 * 
 * <p>Classe Java per PersonNameType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="PersonNameType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="firstName" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *       &lt;attribute name="middleName" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *       &lt;attribute name="lastName" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonNameType")
public class PersonNameType {

    @XmlAttribute(name = "firstName")
    protected String firstName;
    @XmlAttribute(name = "middleName")
    protected String middleName;
    @XmlAttribute(name = "lastName")
    protected String lastName;

    /**
     * Recupera il valore della proprietà firstName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Imposta il valore della proprietà firstName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Recupera il valore della proprietà middleName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Imposta il valore della proprietà middleName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Recupera il valore della proprietà lastName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Imposta il valore della proprietà lastName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

}
