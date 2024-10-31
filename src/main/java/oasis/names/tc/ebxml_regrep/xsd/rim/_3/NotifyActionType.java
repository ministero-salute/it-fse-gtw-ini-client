
package oasis.names.tc.ebxml_regrep.xsd.rim._3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Abstract Base type for all types of Notify Actions
 * 
 * <p>Classe Java per NotifyActionType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="NotifyActionType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ActionType">
 *       &lt;attribute name="notificationOption" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" default="urn:oasis:names:tc:ebxml-regrep:NotificationOptionType:ObjectRefs" />
 *       &lt;attribute name="endPoint" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotifyActionType")
public class NotifyActionType
    extends ActionType
{

    @XmlAttribute(name = "notificationOption")
    protected String notificationOption;
    @XmlAttribute(name = "endPoint", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String endPoint;

    /**
     * Recupera il valore della proprietà notificationOption.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationOption() {
        if (notificationOption == null) {
            return "urn:oasis:names:tc:ebxml-regrep:NotificationOptionType:ObjectRefs";
        } else {
            return notificationOption;
        }
    }

    /**
     * Imposta il valore della proprietà notificationOption.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationOption(String value) {
        this.notificationOption = value;
    }

    /**
     * Recupera il valore della proprietà endPoint.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Imposta il valore della proprietà endPoint.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndPoint(String value) {
        this.endPoint = value;
    }

}
