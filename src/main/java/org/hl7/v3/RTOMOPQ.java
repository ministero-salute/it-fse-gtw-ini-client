
package org.hl7.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per RTO_MO_PQ complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="RTO_MO_PQ">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}QTY">
 *       &lt;sequence>
 *         &lt;element name="numerator" type="{urn:hl7-org:v3}MO"/>
 *         &lt;element name="denominator" type="{urn:hl7-org:v3}PQ"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RTO_MO_PQ", propOrder = {
    "numerator",
    "denominator"
})
public class RTOMOPQ
    extends QTY
{

    @XmlElement(required = true)
    protected MO numerator;
    @XmlElement(required = true)
    protected PQ denominator;

    /**
     * Recupera il valore della proprietà numerator.
     * 
     * @return
     *     possible object is
     *     {@link MO }
     *     
     */
    public MO getNumerator() {
        return numerator;
    }

    /**
     * Imposta il valore della proprietà numerator.
     * 
     * @param value
     *     allowed object is
     *     {@link MO }
     *     
     */
    public void setNumerator(MO value) {
        this.numerator = value;
    }

    /**
     * Recupera il valore della proprietà denominator.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getDenominator() {
        return denominator;
    }

    /**
     * Imposta il valore della proprietà denominator.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setDenominator(PQ value) {
        this.denominator = value;
    }

}
