
package oasis.names.tc.ebxml_regrep.xsd.rs._3;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the oasis.names.tc.ebxml_regrep.xsd.rs._3 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RegistryResponse_QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryResponse");
    private final static QName _RegistryErrorList_QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryErrorList");
    private final static QName _RegistryError_QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryError");
    private final static QName _RegistryRequest_QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: oasis.names.tc.ebxml_regrep.xsd.rs._3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RegistryErrorType }
     * 
     */
    public RegistryErrorType createRegistryErrorType() {
        return new RegistryErrorType();
    }

    /**
     * Create an instance of {@link RegistryErrorListType }
     * 
     */
    public RegistryErrorListType createRegistryErrorListType() {
        return new RegistryErrorListType();
    }

    /**
     * Create an instance of {@link RegistryRequestType }
     * 
     */
    public RegistryRequestType createRegistryRequestType() {
        return new RegistryRequestType();
    }

    /**
     * Create an instance of {@link RegistryResponseType }
     * 
     */
    public RegistryResponseType createRegistryResponseType() {
        return new RegistryResponseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", name = "RegistryResponse")
    public JAXBElement<RegistryResponseType> createRegistryResponse(RegistryResponseType value) {
        return new JAXBElement<RegistryResponseType>(_RegistryResponse_QNAME, RegistryResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryErrorListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", name = "RegistryErrorList")
    public JAXBElement<RegistryErrorListType> createRegistryErrorList(RegistryErrorListType value) {
        return new JAXBElement<RegistryErrorListType>(_RegistryErrorList_QNAME, RegistryErrorListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryErrorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", name = "RegistryError")
    public JAXBElement<RegistryErrorType> createRegistryError(RegistryErrorType value) {
        return new JAXBElement<RegistryErrorType>(_RegistryError_QNAME, RegistryErrorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegistryRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", name = "RegistryRequest")
    public JAXBElement<RegistryRequestType> createRegistryRequest(RegistryRequestType value) {
        return new JAXBElement<RegistryRequestType>(_RegistryRequest_QNAME, RegistryRequestType.class, null, value);
    }

}
