
package oasis.names.tc.ebxml_regrep.xsd.lcm._3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AdhocQueryType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectRefListType;
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
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}AdhocQuery" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ObjectRefList" minOccurs="0"/>
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
    "objectRefList"
})
@XmlRootElement(name = "UndeprecateObjectsRequest")
public class UndeprecateObjectsRequest
    extends RegistryRequestType
{

    @XmlElement(name = "AdhocQuery", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0")
    protected AdhocQueryType adhocQuery;
    @XmlElement(name = "ObjectRefList", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0")
    protected ObjectRefListType objectRefList;

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
     * Recupera il valore della proprietà objectRefList.
     * 
     * @return
     *     possible object is
     *     {@link ObjectRefListType }
     *     
     */
    public ObjectRefListType getObjectRefList() {
        return objectRefList;
    }

    /**
     * Imposta il valore della proprietà objectRefList.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectRefListType }
     *     
     */
    public void setObjectRefList(ObjectRefListType value) {
        this.objectRefList = value;
    }

}
