
package oasis.names.tc.ebxml_regrep.xsd.rim._3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


/**
 * Mapping of the same named interface in ebRIM.
 * 
 * <p>Classe Java per FederationType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="FederationType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;attribute name="replicationSyncLatency" type="{http://www.w3.org/2001/XMLSchema}duration" default="P1D" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FederationType")
public class FederationType
    extends RegistryObjectType
{

    @XmlAttribute(name = "replicationSyncLatency")
    protected Duration replicationSyncLatency;

    /**
     * Recupera il valore della proprietà replicationSyncLatency.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getReplicationSyncLatency() {
        return replicationSyncLatency;
    }

    /**
     * Imposta il valore della proprietà replicationSyncLatency.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setReplicationSyncLatency(Duration value) {
        this.replicationSyncLatency = value;
    }

}
