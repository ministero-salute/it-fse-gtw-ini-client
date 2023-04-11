
package ihe.iti.xds_b._2007;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per DocumentResponse_._type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DocumentResponse_._type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HomeCommunityId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RepositoryUniqueId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DocumentUniqueId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NewRepositoryUniqueId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NewDocumentUniqueId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mimeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Document" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentResponse_._type", propOrder = {
    "homeCommunityId",
    "repositoryUniqueId",
    "documentUniqueId",
    "newRepositoryUniqueId",
    "newDocumentUniqueId",
    "mimeType",
    "document"
})
public class DocumentResponseType {

    @XmlElement(name = "HomeCommunityId")
    protected String homeCommunityId;
    @XmlElement(name = "RepositoryUniqueId", required = true)
    protected String repositoryUniqueId;
    @XmlElement(name = "DocumentUniqueId", required = true)
    protected String documentUniqueId;
    @XmlElement(name = "NewRepositoryUniqueId")
    protected String newRepositoryUniqueId;
    @XmlElement(name = "NewDocumentUniqueId")
    protected String newDocumentUniqueId;
    @XmlElement(required = true)
    protected String mimeType;
    @XmlElement(name = "Document", required = true)
    @XmlMimeType("application/octet-stream")
    protected DataHandler document;

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

    /**
     * Recupera il valore della proprietà newRepositoryUniqueId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewRepositoryUniqueId() {
        return newRepositoryUniqueId;
    }

    /**
     * Imposta il valore della proprietà newRepositoryUniqueId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewRepositoryUniqueId(String value) {
        this.newRepositoryUniqueId = value;
    }

    /**
     * Recupera il valore della proprietà newDocumentUniqueId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewDocumentUniqueId() {
        return newDocumentUniqueId;
    }

    /**
     * Imposta il valore della proprietà newDocumentUniqueId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewDocumentUniqueId(String value) {
        this.newDocumentUniqueId = value;
    }

    /**
     * Recupera il valore della proprietà mimeType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Imposta il valore della proprietà mimeType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Recupera il valore della proprietà document.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    public DataHandler getDocument() {
        return document;
    }

    /**
     * Imposta il valore della proprietà document.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    public void setDocument(DataHandler value) {
        this.document = value;
    }

}
