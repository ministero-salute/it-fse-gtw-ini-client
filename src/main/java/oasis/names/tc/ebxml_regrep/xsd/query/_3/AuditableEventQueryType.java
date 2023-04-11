
package oasis.names.tc.ebxml_regrep.xsd.query._3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per AuditableEventQueryType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="AuditableEventQueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0}RegistryObjectQueryType">
 *       &lt;sequence>
 *         &lt;element name="AffectedObjectQuery" type="{urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0}RegistryObjectQueryType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="EventTypeQuery" type="{urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0}ClassificationNodeQueryType" minOccurs="0"/>
 *         &lt;element name="UserQuery" type="{urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0}UserQueryType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditableEventQueryType", propOrder = {
    "affectedObjectQuery",
    "eventTypeQuery",
    "userQuery"
})
public class AuditableEventQueryType
    extends RegistryObjectQueryType
{

    @XmlElement(name = "AffectedObjectQuery")
    protected List<RegistryObjectQueryType> affectedObjectQuery;
    @XmlElement(name = "EventTypeQuery")
    protected ClassificationNodeQueryType eventTypeQuery;
    @XmlElement(name = "UserQuery")
    protected UserQueryType userQuery;

    /**
     * Gets the value of the affectedObjectQuery property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the affectedObjectQuery property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAffectedObjectQuery().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistryObjectQueryType }
     * 
     * 
     */
    public List<RegistryObjectQueryType> getAffectedObjectQuery() {
        if (affectedObjectQuery == null) {
            affectedObjectQuery = new ArrayList<RegistryObjectQueryType>();
        }
        return this.affectedObjectQuery;
    }

    /**
     * Recupera il valore della proprietà eventTypeQuery.
     * 
     * @return
     *     possible object is
     *     {@link ClassificationNodeQueryType }
     *     
     */
    public ClassificationNodeQueryType getEventTypeQuery() {
        return eventTypeQuery;
    }

    /**
     * Imposta il valore della proprietà eventTypeQuery.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassificationNodeQueryType }
     *     
     */
    public void setEventTypeQuery(ClassificationNodeQueryType value) {
        this.eventTypeQuery = value;
    }

    /**
     * Recupera il valore della proprietà userQuery.
     * 
     * @return
     *     possible object is
     *     {@link UserQueryType }
     *     
     */
    public UserQueryType getUserQuery() {
        return userQuery;
    }

    /**
     * Imposta il valore della proprietà userQuery.
     * 
     * @param value
     *     allowed object is
     *     {@link UserQueryType }
     *     
     */
    public void setUserQuery(UserQueryType value) {
        this.userQuery = value;
    }

}
