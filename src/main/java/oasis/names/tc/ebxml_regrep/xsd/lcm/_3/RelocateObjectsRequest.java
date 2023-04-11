
package oasis.names.tc.ebxml_regrep.xsd.lcm._3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AdhocQueryType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectRefType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryRequestType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0}RegistryRequestType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}AdhocQuery"/>
 *         &lt;element name="SourceRegistry" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ObjectRefType"/>
 *         &lt;element name="DestinationRegistry" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ObjectRefType"/>
 *         &lt;element name="OwnerAtSource" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ObjectRefType"/>
 *         &lt;element name="OwnerAtDestination" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ObjectRefType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "adhocQuery",
    "sourceRegistry",
    "destinationRegistry",
    "ownerAtSource",
    "ownerAtDestination"
})
@XmlRootElement(name = "RelocateObjectsRequest")
public class RelocateObjectsRequest
    extends RegistryRequestType
{

    @XmlElement(name = "AdhocQuery", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", required = true)
    protected AdhocQueryType adhocQuery;
    @XmlElement(name = "SourceRegistry", required = true)
    protected ObjectRefType sourceRegistry;
    @XmlElement(name = "DestinationRegistry", required = true)
    protected ObjectRefType destinationRegistry;
    @XmlElement(name = "OwnerAtSource", required = true)
    protected ObjectRefType ownerAtSource;
    @XmlElement(name = "OwnerAtDestination", required = true)
    protected ObjectRefType ownerAtDestination;

    /**
     * Recupera il valore della proprietà adhocQuery.
     * 
     * @return
     *     possible object is
     *     {@link AdhocQueryType }
     *     
     */
    public AdhocQueryType getAdhocQuery() {
        return adhocQuery;
    }

    /**
     * Imposta il valore della proprietà adhocQuery.
     * 
     * @param value
     *     allowed object is
     *     {@link AdhocQueryType }
     *     
     */
    public void setAdhocQuery(AdhocQueryType value) {
        this.adhocQuery = value;
    }

    /**
     * Recupera il valore della proprietà sourceRegistry.
     * 
     * @return
     *     possible object is
     *     {@link ObjectRefType }
     *     
     */
    public ObjectRefType getSourceRegistry() {
        return sourceRegistry;
    }

    /**
     * Imposta il valore della proprietà sourceRegistry.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectRefType }
     *     
     */
    public void setSourceRegistry(ObjectRefType value) {
        this.sourceRegistry = value;
    }

    /**
     * Recupera il valore della proprietà destinationRegistry.
     * 
     * @return
     *     possible object is
     *     {@link ObjectRefType }
     *     
     */
    public ObjectRefType getDestinationRegistry() {
        return destinationRegistry;
    }

    /**
     * Imposta il valore della proprietà destinationRegistry.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectRefType }
     *     
     */
    public void setDestinationRegistry(ObjectRefType value) {
        this.destinationRegistry = value;
    }

    /**
     * Recupera il valore della proprietà ownerAtSource.
     * 
     * @return
     *     possible object is
     *     {@link ObjectRefType }
     *     
     */
    public ObjectRefType getOwnerAtSource() {
        return ownerAtSource;
    }

    /**
     * Imposta il valore della proprietà ownerAtSource.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectRefType }
     *     
     */
    public void setOwnerAtSource(ObjectRefType value) {
        this.ownerAtSource = value;
    }

    /**
     * Recupera il valore della proprietà ownerAtDestination.
     * 
     * @return
     *     possible object is
     *     {@link ObjectRefType }
     *     
     */
    public ObjectRefType getOwnerAtDestination() {
        return ownerAtDestination;
    }

    /**
     * Imposta il valore della proprietà ownerAtDestination.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectRefType }
     *     
     */
    public void setOwnerAtDestination(ObjectRefType value) {
        this.ownerAtDestination = value;
    }

}
