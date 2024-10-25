
package oasis.names.tc.ebxml_regrep.xsd.rim._3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SpecificationLinkType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SpecificationLinkType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}UsageDescription" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}UsageParameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="serviceBinding" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="specificationObject" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpecificationLinkType", propOrder = {
    "usageDescription",
    "usageParameter"
})
public class SpecificationLinkType
    extends RegistryObjectType
{

    @XmlElement(name = "UsageDescription")
    protected InternationalStringType usageDescription;
    @XmlElement(name = "UsageParameter")
    protected List<String> usageParameter;
    @XmlAttribute(name = "serviceBinding", required = true)
    protected String serviceBinding;
    @XmlAttribute(name = "specificationObject", required = true)
    protected String specificationObject;

    /**
     * Recupera il valore della proprietà usageDescription.
     * 
     * @return
     *     possible object is
     *     {@link InternationalStringType }
     *     
     */
    public InternationalStringType getUsageDescription() {
        return usageDescription;
    }

    /**
     * Imposta il valore della proprietà usageDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link InternationalStringType }
     *     
     */
    public void setUsageDescription(InternationalStringType value) {
        this.usageDescription = value;
    }

    /**
     * Gets the value of the usageParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usageParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsageParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUsageParameter() {
        if (usageParameter == null) {
            usageParameter = new ArrayList<String>();
        }
        return this.usageParameter;
    }

    /**
     * Recupera il valore della proprietà serviceBinding.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceBinding() {
        return serviceBinding;
    }

    /**
     * Imposta il valore della proprietà serviceBinding.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceBinding(String value) {
        this.serviceBinding = value;
    }

    /**
     * Recupera il valore della proprietà specificationObject.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecificationObject() {
        return specificationObject;
    }

    /**
     * Imposta il valore della proprietà specificationObject.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecificationObject(String value) {
        this.specificationObject = value;
    }

}
