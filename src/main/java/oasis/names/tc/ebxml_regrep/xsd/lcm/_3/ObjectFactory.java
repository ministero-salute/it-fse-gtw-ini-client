
package oasis.names.tc.ebxml_regrep.xsd.lcm._3;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the oasis.names.tc.ebxml_regrep.xsd.lcm._3 package. 
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
	
	private final static QName _RemoveObjectsRequest_QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "RemoveObjectsRequest");
    private final static QName _SubmitObjectsRequest_QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "SubmitObjectsRequest");


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: oasis.names.tc.ebxml_regrep.xsd.lcm._3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdateObjectsRequest }
     * 
     */
    public UpdateObjectsRequest createUpdateObjectsRequest() {
        return new UpdateObjectsRequest();
    }

    /**
     * Create an instance of {@link RelocateObjectsRequest }
     * 
     */
    public RelocateObjectsRequest createRelocateObjectsRequest() {
        return new RelocateObjectsRequest();
    }

    /**
     * Create an instance of {@link AcceptObjectsRequest }
     * 
     */
    public AcceptObjectsRequest createAcceptObjectsRequest() {
        return new AcceptObjectsRequest();
    }

    /**
     * Create an instance of {@link RemoveObjectsRequest }
     * 
     */
    public RemoveObjectsRequest createRemoveObjectsRequest() {
        return new RemoveObjectsRequest();
    }

    /**
     * Create an instance of {@link UndeprecateObjectsRequest }
     * 
     */
    public UndeprecateObjectsRequest createUndeprecateObjectsRequest() {
        return new UndeprecateObjectsRequest();
    }

    /**
     * Create an instance of {@link ApproveObjectsRequest }
     * 
     */
    public ApproveObjectsRequest createApproveObjectsRequest() {
        return new ApproveObjectsRequest();
    }

    /**
     * Create an instance of {@link DeprecateObjectsRequest }
     * 
     */
    public DeprecateObjectsRequest createDeprecateObjectsRequest() {
        return new DeprecateObjectsRequest();
    }

    /**
     * Create an instance of {@link SubmitObjectsRequest }
     * 
     */
    public SubmitObjectsRequest createSubmitObjectsRequest() {
        return new SubmitObjectsRequest();
    }

    /**
     * Create an instance of {@link RemoveObjectsRequestType }
     * 
     */
    public RemoveObjectsRequestType createRemoveObjectsRequestType() {
        return new RemoveObjectsRequestType();
    }

    /**
     * Create an instance of {@link SubmitObjectsRequestType }
     * 
     */
    public SubmitObjectsRequestType createSubmitObjectsRequestType() {
        return new SubmitObjectsRequestType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoveObjectsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", name = "RemoveObjectsRequest")
    public JAXBElement<RemoveObjectsRequestType> createRemoveObjectsRequest(RemoveObjectsRequestType value) {
        return new JAXBElement<RemoveObjectsRequestType>(_RemoveObjectsRequest_QNAME, RemoveObjectsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitObjectsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", name = "SubmitObjectsRequest")
    public JAXBElement<SubmitObjectsRequestType> createSubmitObjectsRequest(SubmitObjectsRequestType value) {
        return new JAXBElement<SubmitObjectsRequestType>(_SubmitObjectsRequest_QNAME, SubmitObjectsRequestType.class, null, value);
    }

    
    
}
