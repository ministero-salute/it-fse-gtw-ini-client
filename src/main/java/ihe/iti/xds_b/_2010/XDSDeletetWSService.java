
package ihe.iti.xds_b._2010;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "XDSDeletetWSService", targetNamespace = "urn:ihe:iti:xds-b:2010", wsdlLocation = "classpath:/wsdl/XDSDeletetWS.wsdl")
public class XDSDeletetWSService
    extends Service
{

    private final static URL XDSDELETETWSSERVICE_WSDL_LOCATION;
    private final static WebServiceException XDSDELETETWSSERVICE_EXCEPTION;
    private final static QName XDSDELETETWSSERVICE_QNAME = new QName("urn:ihe:iti:xds-b:2010", "XDSDeletetWSService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("classpath:/wsdl/XDSDeletetWS.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        XDSDELETETWSSERVICE_WSDL_LOCATION = url;
        XDSDELETETWSSERVICE_EXCEPTION = e;
    }

    public XDSDeletetWSService() {
        super(__getWsdlLocation(), XDSDELETETWSSERVICE_QNAME);
    }

    public XDSDeletetWSService(WebServiceFeature... features) {
        super(__getWsdlLocation(), XDSDELETETWSSERVICE_QNAME, features);
    }

    public XDSDeletetWSService(URL wsdlLocation) {
        super(wsdlLocation, XDSDELETETWSSERVICE_QNAME);
    }

    public XDSDeletetWSService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, XDSDELETETWSSERVICE_QNAME, features);
    }

    public XDSDeletetWSService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public XDSDeletetWSService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns XDSDeletetWS
     */
    @WebEndpoint(name = "XDSDeletetWSSPort")
    public XDSDeletetWS getXDSDeletetWSSPort() {
        return super.getPort(new QName("urn:ihe:iti:xds-b:2010", "XDSDeletetWSSPort"), XDSDeletetWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns XDSDeletetWS
     */
    @WebEndpoint(name = "XDSDeletetWSSPort")
    public XDSDeletetWS getXDSDeletetWSSPort(WebServiceFeature... features) {
        return super.getPort(new QName("urn:ihe:iti:xds-b:2010", "XDSDeletetWSSPort"), XDSDeletetWS.class, features);
    }

    private static URL __getWsdlLocation() {
        if (XDSDELETETWSSERVICE_EXCEPTION!= null) {
            throw XDSDELETETWSSERVICE_EXCEPTION;
        }
        return XDSDELETETWSSERVICE_WSDL_LOCATION;
    }

}
