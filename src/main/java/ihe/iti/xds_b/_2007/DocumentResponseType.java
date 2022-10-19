/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package ihe.iti.xds_b._2007;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentResponse_._type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the homeCommunityId property.
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
     * Sets the value of the homeCommunityId property.
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
     * Gets the value of the repositoryUniqueId property.
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
     * Sets the value of the repositoryUniqueId property.
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
     * Gets the value of the documentUniqueId property.
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
     * Sets the value of the documentUniqueId property.
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
     * Gets the value of the newRepositoryUniqueId property.
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
     * Sets the value of the newRepositoryUniqueId property.
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
     * Gets the value of the newDocumentUniqueId property.
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
     * Sets the value of the newDocumentUniqueId property.
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
     * Gets the value of the mimeType property.
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
     * Sets the value of the mimeType property.
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
     * Gets the value of the document property.
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
     * Sets the value of the document property.
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
