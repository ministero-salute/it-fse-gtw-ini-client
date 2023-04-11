
package ihe.iti.xds_b._2007;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DocumentRequest_._type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DocumentRequest_._type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HomeCommunityId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RepositoryUniqueId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DocumentUniqueId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentRequest_._type", propOrder = {
    "homeCommunityId",
    "repositoryUniqueId",
    "documentUniqueId"
})
public class DocumentRequestType {

    @XmlElement(name = "HomeCommunityId")
    protected String homeCommunityId;
    @XmlElement(name = "RepositoryUniqueId", required = true)
    protected String repositoryUniqueId;
    @XmlElement(name = "DocumentUniqueId", required = true)
    protected String documentUniqueId;

    /**
     * Recupera il valore della proprietà homeCommunityId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomeCommunityId() {
        return homeCommunityId;
    }

    /**
     * Imposta il valore della proprietà homeCommunityId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomeCommunityId(String value) {
        this.homeCommunityId = value;
    }

    /**
     * Recupera il valore della proprietà repositoryUniqueId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepositoryUniqueId() {
        return repositoryUniqueId;
    }

    /**
     * Imposta il valore della proprietà repositoryUniqueId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepositoryUniqueId(String value) {
        this.repositoryUniqueId = value;
    }

    /**
     * Recupera il valore della proprietà documentUniqueId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    /**
     * Imposta il valore della proprietà documentUniqueId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentUniqueId(String value) {
        this.documentUniqueId = value;
    }

}
