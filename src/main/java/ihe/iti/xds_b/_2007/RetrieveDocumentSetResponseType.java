
package ihe.iti.xds_b._2007;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;


/**
 * <p>Classe Java per RetrieveDocumentSetResponseType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="RetrieveDocumentSetResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0}RegistryResponse"/>
 *         &lt;element name="DocumentResponse" type="{urn:ihe:iti:xds-b:2007}DocumentResponse_._type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RetrieveDocumentSetResponseType", propOrder = {
    "registryResponse",
    "documentResponse"
})
public class RetrieveDocumentSetResponseType {

    @XmlElement(name = "RegistryResponse", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", required = true)
    protected RegistryResponseType registryResponse;
    @XmlElement(name = "DocumentResponse")
    protected List<DocumentResponseType> documentResponse;

    /**
     * Recupera il valore della proprietà registryResponse.
     * 
     * @return
     *     possible object is
     *     {@link RegistryResponseType }
     *     
     */
    public RegistryResponseType getRegistryResponse() {
        return registryResponse;
    }

    /**
     * Imposta il valore della proprietà registryResponse.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistryResponseType }
     *     
     */
    public void setRegistryResponse(RegistryResponseType value) {
        this.registryResponse = value;
    }

    /**
     * Gets the value of the documentResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documentResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocumentResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentResponseType }
     * 
     * 
     */
    public List<DocumentResponseType> getDocumentResponse() {
        if (documentResponse == null) {
            documentResponse = new ArrayList<DocumentResponseType>();
        }
        return this.documentResponse;
    }

}
