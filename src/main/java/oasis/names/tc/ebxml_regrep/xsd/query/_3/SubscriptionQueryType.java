
package oasis.names.tc.ebxml_regrep.xsd.query._3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SubscriptionQueryType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SubscriptionQueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0}RegistryObjectQueryType">
 *       &lt;sequence>
 *         &lt;element name="SelectorQuery" type="{urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0}AdhocQueryQueryType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionQueryType", propOrder = {
    "selectorQuery"
})
public class SubscriptionQueryType
    extends RegistryObjectQueryType
{

    @XmlElement(name = "SelectorQuery")
    protected AdhocQueryQueryType selectorQuery;

    /**
     * Recupera il valore della proprietà selectorQuery.
     * 
     * @return
     *     possible object is
     *     {@link AdhocQueryQueryType }
     *     
     */
    public AdhocQueryQueryType getSelectorQuery() {
        return selectorQuery;
    }

    /**
     * Imposta il valore della proprietà selectorQuery.
     * 
     * @param value
     *     allowed object is
     *     {@link AdhocQueryQueryType }
     *     
     */
    public void setSelectorQuery(AdhocQueryQueryType value) {
        this.selectorQuery = value;
    }

}
