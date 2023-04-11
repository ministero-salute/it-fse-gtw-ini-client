
package org.hl7.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per PRPA_MT201302UV02.Patient.statusCode complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="PRPA_MT201302UV02.Patient.statusCode">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}CS">
 *       &lt;attribute name="updateMode" type="{urn:hl7-org:v3}PRPA_MT201302UV02.Patient.statusCode.updateMode" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PRPA_MT201302UV02.Patient.statusCode")
public class PRPAMT201302UV02PatientStatusCode
    extends CS
{

    @XmlAttribute(name = "updateMode")
    protected PRPAMT201302UV02PatientStatusCodeUpdateMode updateMode;

    /**
     * Recupera il valore della proprietà updateMode.
     * 
     * @return
     *     possible object is
     *     {@link PRPAMT201302UV02PatientStatusCodeUpdateMode }
     *     
     */
    public PRPAMT201302UV02PatientStatusCodeUpdateMode getUpdateMode() {
        return updateMode;
    }

    /**
     * Imposta il valore della proprietà updateMode.
     * 
     * @param value
     *     allowed object is
     *     {@link PRPAMT201302UV02PatientStatusCodeUpdateMode }
     *     
     */
    public void setUpdateMode(PRPAMT201302UV02PatientStatusCodeUpdateMode value) {
        this.updateMode = value;
    }

}
