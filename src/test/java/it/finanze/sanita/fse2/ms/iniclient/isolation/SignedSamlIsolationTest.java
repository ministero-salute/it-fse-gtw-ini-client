/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.*;
import javax.xml.soap.MessageFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.opensaml.Configuration;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.signature.SignatureValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.mockito.Mockito.*;
import it.finanze.sanita.fse2.ms.iniclient.TestUtility;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.enums.ActionEnumType;
import it.finanze.sanita.fse2.ms.iniclient.singleton.SigningCredentialSingleton;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;

@Tag("ISOLATION") @ActiveProfiles("ISOLATION")
@SpringJUnitConfig(SamlHeaderBuilderUtility.class)
class SignedSamlIsolationTest {
    @Autowired SamlHeaderBuilderUtility builder;
    @MockitoBean IniCFG config;
    @TempDir static Path directory;
    private static Path store;
    @BeforeAll static void key() throws Exception {
        store=directory.resolve("signing.jks");
        var process=new ProcessBuilder(Path.of(System.getProperty("java.home"),"bin","keytool").toString(),
                "-genkeypair","-alias","signing","-keyalg","RSA","-keysize","2048","-validity","2",
                "-dname","CN=Isolation","-storetype","JKS","-keystore",store.toString(),"-storepass","password","-keypass","password","-noprompt")
                .redirectErrorStream(true).start();
        String output=new String(process.getInputStream().readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(0,process.waitFor(),output);
    }
    @BeforeEach void setup(){
        when(config.getKeyStoreLocation()).thenReturn(store.toString());when(config.getKeyStorePassword()).thenReturn("password");
        when(config.getKeyStoreAlias()).thenReturn("signing");builder.initialize();
    }
    @ParameterizedTest @CsvSource({"CREATE,false,person","READ_METADATA,false,person","READ_REFERENCE,true,person","UPDATE_V2,false,-","REPLACE,false,''"})
    void realSignedHeadersPreserveIdentityAttributesAndVerifyCryptographically(ActionEnumType action,boolean author,String subject) throws Exception {
        var token=TestUtility.mockBasicToken();token.getPayload().setSub(subject);token.getPayload().setUse_subject_as_author(author);
        token.getPayload().setSubject_role("doctor");
        token.getPayload().setPatient_consent(true);
        var headers=builder.buildHeader(token,action);assertEquals(3,headers.size());
        var soap=MessageFactory.newInstance().createMessage();
        for(var header:headers)header.writeTo(soap);
        var dom=soap.getSOAPHeader();
        assertEquals(action.getHeaderAction(),dom.getElementsByTagNameNS("http://www.w3.org/2005/08/addressing","Action").item(0).getTextContent());
        assertFalse(dom.getElementsByTagNameNS("http://www.w3.org/2005/08/addressing","MessageID").item(0).getTextContent().isBlank());
        // Parse serialized SOAP into an ordinary DOM: OpenSAML 2 detaches nodes
        // while unmarshalling, which is incompatible with SAAJ's DOM wrapper.
        var bytes=new java.io.ByteArrayOutputStream();soap.writeTo(bytes);
        var parser=javax.xml.parsers.DocumentBuilderFactory.newInstance();parser.setNamespaceAware(true);
        var document=parser.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(bytes.toByteArray()));
        var assertionElement=(org.w3c.dom.Element)document.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion","Assertion").item(0);
        var assertion=(Assertion)Configuration.getUnmarshallerFactory().getUnmarshaller(assertionElement).unmarshall(assertionElement);
        assertEquals(token.getPayload().getSubject_organization_id(),assertion.getIssuer().getValue());
        assertTrue(assertion.getConditions().getNotOnOrAfter().isAfter(assertion.getConditions().getNotBefore()));
        var attrs=new HashMap<String,String>();
        assertion.getAttributeStatements().get(0).getAttributes().forEach(a->attrs.put(a.getName(),a.getAttributeValues().get(0).getDOM().getTextContent()));
        boolean read=action==ActionEnumType.READ_METADATA||action==ActionEnumType.READ_REFERENCE;
        assertEquals(read?"GTW":token.getPayload().getSubject_role(),attrs.get("urn:oasis:names:tc:xacml:2.0:subject:role"));
        assertEquals("true",attrs.get("urn:oasis:names:tc:xspa:1.0:resource:patient:consent"));
        if(read&&!author||subject==null||subject.isEmpty()||subject.equals("-"))assertFalse(attrs.containsKey("urn:oasis:names:tc:xacml:1.0:subject:subject-id"));
        else assertTrue(attrs.get("urn:oasis:names:tc:xacml:1.0:subject:subject-id").startsWith(subject+"^^^&"));
        var credential=SigningCredentialSingleton.getInstance(config);
        assertSame(credential,SigningCredentialSingleton.getInstance(config));
        new SignatureValidator(credential).validate(assertion.getSignature());
    }
    @Test void invalidTokenAndLocalityFailExplicitly(){
        assertThrows(BusinessException.class,()->builder.buildHeader(null,ActionEnumType.CREATE));
        assertThrows(BusinessException.class,()->builder.buildHeader(TestUtility.mockBasicToken(),null));
        assertTrue(builder.isValidLocalityXon("Hospital^^^^^&oid&ISO^^^^code",true));
        assertTrue(builder.isValidLocalityXon("^^^^^&oid&ISO^^^^code",false));
        assertFalse(builder.isValidLocalityXon("^^^^^&oid&ISO^^^^code",true));
        assertFalse(builder.isValidLocalityXon("bad",false));
    }
}
