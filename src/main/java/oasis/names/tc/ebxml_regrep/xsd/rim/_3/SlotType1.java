
package oasis.names.tc.ebxml_regrep.xsd.rim._3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SlotType1 complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SlotType1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ValueList"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}LongName" />
 *       &lt;attribute name="slotType" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SlotType1", propOrder = {
    "valueList"
})
public class SlotType1 {

    @XmlElement(name = "ValueList", required = true)
    protected ValueListType valueList;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "slotType")
    protected String slotType;

    /**
     * Recupera il valore della proprietà valueList.
     * 
     * @return
     *     possible object is
     *     {@link ValueListType }
     *     
     */
    public ValueListType getValueList() {
        return valueList;
    }

    /**
     * Imposta il valore della proprietà valueList.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueListType }
     *     
     */
    public void setValueList(ValueListType value) {
        this.valueList = value;
    }

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietà slotType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSlotType() {
        return slotType;
    }

    /**
     * Imposta il valore della proprietà slotType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSlotType(String value) {
        this.slotType = value;
    }

}
