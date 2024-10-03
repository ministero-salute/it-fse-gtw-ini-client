
package org.hl7.v3;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per GLIST_TS complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="GLIST_TS">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}ANY">
 *       &lt;sequence>
 *         &lt;element name="head" type="{urn:hl7-org:v3}TS"/>
 *         &lt;element name="increment" type="{urn:hl7-org:v3}PQ"/>
 *       &lt;/sequence>
 *       &lt;attribute name="period" type="{urn:hl7-org:v3}int" />
 *       &lt;attribute name="denominator" type="{urn:hl7-org:v3}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GLIST_TS", propOrder = {
    "head",
    "increment"
})
public class GLISTTS
    extends ANY
{

    @XmlElement(required = true)
    protected TS head;
    @XmlElement(required = true)
    protected PQ increment;
    @XmlAttribute(name = "period")
    protected BigInteger period;
    @XmlAttribute(name = "denominator")
    protected BigInteger denominator;

    /**
     * Recupera il valore della proprietà head.
     * 
     * @return
     *     possible object is
     *     {@link TS }
     *     
     */
    public TS getHead() {
        return head;
    }

    /**
     * Imposta il valore della proprietà head.
     * 
     * @param value
     *     allowed object is
     *     {@link TS }
     *     
     */
    public void setHead(TS value) {
        this.head = value;
    }

    /**
     * Recupera il valore della proprietà increment.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getIncrement() {
        return increment;
    }

    /**
     * Imposta il valore della proprietà increment.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setIncrement(PQ value) {
        this.increment = value;
    }

    /**
     * Recupera il valore della proprietà period.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPeriod() {
        return period;
    }

    /**
     * Imposta il valore della proprietà period.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPeriod(BigInteger value) {
        this.period = value;
    }

    /**
     * Recupera il valore della proprietà denominator.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDenominator() {
        return denominator;
    }

    /**
     * Imposta il valore della proprietà denominator.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDenominator(BigInteger value) {
        this.denominator = value;
    }

}
