
package ihe.iti.xds_b._2010;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;


/**
 * <p>Classe Java per valueSetParser complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="valueSetParser">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:ihe:iti:xds-b:2010}stringMatcher">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "valueSetParser")
@XmlSeeAlso({
    ValueListType.class
})
public class ValueSetParser
    extends StringMatcher
{


}
