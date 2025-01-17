
package org.hl7.v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per COCT_MT510000UV06.PolicyOrProgram complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="COCT_MT510000UV06.PolicyOrProgram">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element name="id" type="{urn:hl7-org:v3}II" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="code" type="{urn:hl7-org:v3}CD"/>
 *         &lt;element name="statusCode" type="{urn:hl7-org:v3}CS" minOccurs="0"/>
 *         &lt;element name="effectiveTime" type="{urn:hl7-org:v3}IVL_TS" minOccurs="0"/>
 *         &lt;element name="confidentialityCode" type="{urn:hl7-org:v3}CE" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="coveredParty" type="{urn:hl7-org:v3}COCT_MT510000UV06.CoveredParty2"/>
 *         &lt;element name="holder" type="{urn:hl7-org:v3}COCT_MT510000UV06.Holder" minOccurs="0"/>
 *         &lt;element name="responsibleParty" type="{urn:hl7-org:v3}COCT_MT510000UV06.ResponsibleParty2" minOccurs="0"/>
 *         &lt;element name="primaryPerformer" type="{urn:hl7-org:v3}COCT_MT510000UV06.PrimaryPerformer" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="author" type="{urn:hl7-org:v3}COCT_MT510000UV06.Author2" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="definition" type="{urn:hl7-org:v3}COCT_MT510000UV06.Definition3" minOccurs="0"/>
 *         &lt;element name="replacementOf" type="{urn:hl7-org:v3}COCT_MT510000UV06.ReplacementOf" minOccurs="0"/>
 *         &lt;element name="limitation1" type="{urn:hl7-org:v3}COCT_MT510000UV06.Limitation3" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="limitation2" type="{urn:hl7-org:v3}COCT_MT510000UV06.Limitation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="coverageOf" type="{urn:hl7-org:v3}COCT_MT510000UV06.Coverage2" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="nullFlavor" type="{urn:hl7-org:v3}NullFlavor" />
 *       &lt;attribute name="classCode" use="required" type="{urn:hl7-org:v3}ActClass" fixed="COV" />
 *       &lt;attribute name="moodCode" use="required" type="{urn:hl7-org:v3}ActMood" fixed="EVN" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "COCT_MT510000UV06.PolicyOrProgram", propOrder = {
    "realmCode",
    "typeId",
    "templateId",
    "id",
    "code",
    "statusCode",
    "effectiveTime",
    "confidentialityCode",
    "coveredParty",
    "holder",
    "responsibleParty",
    "primaryPerformer",
    "author",
    "definition",
    "replacementOf",
    "limitation1",
    "limitation2",
    "coverageOf"
})
public class COCTMT510000UV06PolicyOrProgram {

    protected List<CS> realmCode;
    protected II typeId;
    protected List<II> templateId;
    protected List<II> id;
    @XmlElement(required = true)
    protected CD code;
    protected CS statusCode;
    protected IVLTS effectiveTime;
    protected List<CE> confidentialityCode;
    @XmlElement(required = true, nillable = true)
    protected COCTMT510000UV06CoveredParty2 coveredParty;
    @XmlElementRef(name = "holder", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false)
    protected JAXBElement<COCTMT510000UV06Holder> holder;
    @XmlElementRef(name = "responsibleParty", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false)
    protected JAXBElement<COCTMT510000UV06ResponsibleParty2> responsibleParty;
    @XmlElement(nillable = true)
    protected List<COCTMT510000UV06PrimaryPerformer> primaryPerformer;
    @XmlElement(nillable = true)
    protected List<COCTMT510000UV06Author2> author;
    @XmlElementRef(name = "definition", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false)
    protected JAXBElement<COCTMT510000UV06Definition3> definition;
    @XmlElementRef(name = "replacementOf", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false)
    protected JAXBElement<COCTMT510000UV06ReplacementOf> replacementOf;
    @XmlElement(nillable = true)
    protected List<COCTMT510000UV06Limitation3> limitation1;
    @XmlElement(nillable = true)
    protected List<COCTMT510000UV06Limitation> limitation2;
    @XmlElement(nillable = true)
    protected List<COCTMT510000UV06Coverage2> coverageOf;
    @XmlAttribute(name = "nullFlavor")
    protected List<String> nullFlavor;
    @XmlAttribute(name = "classCode", required = true)
    protected List<String> classCode;
    @XmlAttribute(name = "moodCode", required = true)
    protected List<String> moodCode;

    /**
     * Gets the value of the realmCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the realmCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRealmCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CS }
     * 
     * 
     */
    public List<CS> getRealmCode() {
        if (realmCode == null) {
            realmCode = new ArrayList<CS>();
        }
        return this.realmCode;
    }

    /**
     * Recupera il valore della proprietà typeId.
     * 
     * @return
     *     possible object is
     *     {@link II }
     *     
     */
    public II getTypeId() {
        return typeId;
    }

    /**
     * Imposta il valore della proprietà typeId.
     * 
     * @param value
     *     allowed object is
     *     {@link II }
     *     
     */
    public void setTypeId(II value) {
        this.typeId = value;
    }

    /**
     * Gets the value of the templateId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the templateId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTemplateId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link II }
     * 
     * 
     */
    public List<II> getTemplateId() {
        if (templateId == null) {
            templateId = new ArrayList<II>();
        }
        return this.templateId;
    }

    /**
     * Gets the value of the id property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the id property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link II }
     * 
     * 
     */
    public List<II> getId() {
        if (id == null) {
            id = new ArrayList<II>();
        }
        return this.id;
    }

    /**
     * Recupera il valore della proprietà code.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCode() {
        return code;
    }

    /**
     * Imposta il valore della proprietà code.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCode(CD value) {
        this.code = value;
    }

    /**
     * Recupera il valore della proprietà statusCode.
     * 
     * @return
     *     possible object is
     *     {@link CS }
     *     
     */
    public CS getStatusCode() {
        return statusCode;
    }

    /**
     * Imposta il valore della proprietà statusCode.
     * 
     * @param value
     *     allowed object is
     *     {@link CS }
     *     
     */
    public void setStatusCode(CS value) {
        this.statusCode = value;
    }

    /**
     * Recupera il valore della proprietà effectiveTime.
     * 
     * @return
     *     possible object is
     *     {@link IVLTS }
     *     
     */
    public IVLTS getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * Imposta il valore della proprietà effectiveTime.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTS }
     *     
     */
    public void setEffectiveTime(IVLTS value) {
        this.effectiveTime = value;
    }

    /**
     * Gets the value of the confidentialityCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the confidentialityCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfidentialityCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CE }
     * 
     * 
     */
    public List<CE> getConfidentialityCode() {
        if (confidentialityCode == null) {
            confidentialityCode = new ArrayList<CE>();
        }
        return this.confidentialityCode;
    }

    /**
     * Recupera il valore della proprietà coveredParty.
     * 
     * @return
     *     possible object is
     *     {@link COCTMT510000UV06CoveredParty2 }
     *     
     */
    public COCTMT510000UV06CoveredParty2 getCoveredParty() {
        return coveredParty;
    }

    /**
     * Imposta il valore della proprietà coveredParty.
     * 
     * @param value
     *     allowed object is
     *     {@link COCTMT510000UV06CoveredParty2 }
     *     
     */
    public void setCoveredParty(COCTMT510000UV06CoveredParty2 value) {
        this.coveredParty = value;
    }

    /**
     * Recupera il valore della proprietà holder.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT510000UV06Holder }{@code >}
     *     
     */
    public JAXBElement<COCTMT510000UV06Holder> getHolder() {
        return holder;
    }

    /**
     * Imposta il valore della proprietà holder.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT510000UV06Holder }{@code >}
     *     
     */
    public void setHolder(JAXBElement<COCTMT510000UV06Holder> value) {
        this.holder = value;
    }

    /**
     * Recupera il valore della proprietà responsibleParty.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT510000UV06ResponsibleParty2 }{@code >}
     *     
     */
    public JAXBElement<COCTMT510000UV06ResponsibleParty2> getResponsibleParty() {
        return responsibleParty;
    }

    /**
     * Imposta il valore della proprietà responsibleParty.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT510000UV06ResponsibleParty2 }{@code >}
     *     
     */
    public void setResponsibleParty(JAXBElement<COCTMT510000UV06ResponsibleParty2> value) {
        this.responsibleParty = value;
    }

    /**
     * Gets the value of the primaryPerformer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the primaryPerformer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrimaryPerformer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link COCTMT510000UV06PrimaryPerformer }
     * 
     * 
     */
    public List<COCTMT510000UV06PrimaryPerformer> getPrimaryPerformer() {
        if (primaryPerformer == null) {
            primaryPerformer = new ArrayList<COCTMT510000UV06PrimaryPerformer>();
        }
        return this.primaryPerformer;
    }

    /**
     * Gets the value of the author property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the author property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link COCTMT510000UV06Author2 }
     * 
     * 
     */
    public List<COCTMT510000UV06Author2> getAuthor() {
        if (author == null) {
            author = new ArrayList<COCTMT510000UV06Author2>();
        }
        return this.author;
    }

    /**
     * Recupera il valore della proprietà definition.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT510000UV06Definition3 }{@code >}
     *     
     */
    public JAXBElement<COCTMT510000UV06Definition3> getDefinition() {
        return definition;
    }

    /**
     * Imposta il valore della proprietà definition.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT510000UV06Definition3 }{@code >}
     *     
     */
    public void setDefinition(JAXBElement<COCTMT510000UV06Definition3> value) {
        this.definition = value;
    }

    /**
     * Recupera il valore della proprietà replacementOf.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT510000UV06ReplacementOf }{@code >}
     *     
     */
    public JAXBElement<COCTMT510000UV06ReplacementOf> getReplacementOf() {
        return replacementOf;
    }

    /**
     * Imposta il valore della proprietà replacementOf.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT510000UV06ReplacementOf }{@code >}
     *     
     */
    public void setReplacementOf(JAXBElement<COCTMT510000UV06ReplacementOf> value) {
        this.replacementOf = value;
    }

    /**
     * Gets the value of the limitation1 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the limitation1 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLimitation1().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link COCTMT510000UV06Limitation3 }
     * 
     * 
     */
    public List<COCTMT510000UV06Limitation3> getLimitation1() {
        if (limitation1 == null) {
            limitation1 = new ArrayList<COCTMT510000UV06Limitation3>();
        }
        return this.limitation1;
    }

    /**
     * Gets the value of the limitation2 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the limitation2 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLimitation2().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link COCTMT510000UV06Limitation }
     * 
     * 
     */
    public List<COCTMT510000UV06Limitation> getLimitation2() {
        if (limitation2 == null) {
            limitation2 = new ArrayList<COCTMT510000UV06Limitation>();
        }
        return this.limitation2;
    }

    /**
     * Gets the value of the coverageOf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coverageOf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoverageOf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link COCTMT510000UV06Coverage2 }
     * 
     * 
     */
    public List<COCTMT510000UV06Coverage2> getCoverageOf() {
        if (coverageOf == null) {
            coverageOf = new ArrayList<COCTMT510000UV06Coverage2>();
        }
        return this.coverageOf;
    }

    /**
     * Gets the value of the nullFlavor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nullFlavor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNullFlavor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNullFlavor() {
        if (nullFlavor == null) {
            nullFlavor = new ArrayList<String>();
        }
        return this.nullFlavor;
    }

    /**
     * Gets the value of the classCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getClassCode() {
        if (classCode == null) {
            classCode = new ArrayList<String>();
        }
        return this.classCode;
    }

    /**
     * Gets the value of the moodCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the moodCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMoodCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMoodCode() {
        if (moodCode == null) {
            moodCode = new ArrayList<String>();
        }
        return this.moodCode;
    }

}
