
package org.hl7.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per PPD_PQ complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="PPD_PQ">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}PQ">
 *       &lt;sequence>
 *         &lt;element name="standardDeviation" type="{urn:hl7-org:v3}PQ" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="distributionType" type="{urn:hl7-org:v3}ProbabilityDistributionType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PPD_PQ", propOrder = {
    "standardDeviation"
})
@XmlSeeAlso({
    IVXBPPDPQ.class,
    SXCMPPDPQ.class
})
public class PPDPQ
    extends PQ
{

    protected PQ standardDeviation;
    @XmlAttribute(name = "distributionType")
    protected ProbabilityDistributionType distributionType;

    /**
     * Recupera il valore della proprietà standardDeviation.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * Imposta il valore della proprietà standardDeviation.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setStandardDeviation(PQ value) {
        this.standardDeviation = value;
    }

    /**
     * Recupera il valore della proprietà distributionType.
     * 
     * @return
     *     possible object is
     *     {@link ProbabilityDistributionType }
     *     
     */
    public ProbabilityDistributionType getDistributionType() {
        return distributionType;
    }

    /**
     * Imposta il valore della proprietà distributionType.
     * 
     * @param value
     *     allowed object is
     *     {@link ProbabilityDistributionType }
     *     
     */
    public void setDistributionType(ProbabilityDistributionType value) {
        this.distributionType = value;
    }

}
