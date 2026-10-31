/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.bson.*;
import org.bson.types.Binary;
import com.mongodb.client.vault.*;
import com.mongodb.ClientEncryptionSettings;
import org.springframework.test.util.ReflectionTestUtils;
import it.finanze.sanita.fse2.ms.iniclient.config.*;
import it.finanze.sanita.fse2.ms.iniclient.config.mongo.*;
import it.finanze.sanita.fse2.ms.iniclient.enums.CloudProviderEnum;
import software.amazon.awssdk.auth.credentials.*;

@Tag("ISOLATION")
class MongoEncryptionIsolationTest {
    @ParameterizedTest @CsvSource({"AWS,true","AWS,false","AZURE,true","AZURE,false"})
    void initializesCloudKeyPolicyReusesOrCreatesKeyAndDecryptsDocuments(CloudProviderEnum cloud,boolean existing){
        var config=new MongoDatabaseCFG();var props=mock(MongoPropertiesCFG.class);var aws=new AwsCfg();var azure=new AzureCfg();
        aws.setRegion("region");aws.setMasterKeyArn("arn");azure.setTenantId("tenant");azure.setClientId("client");azure.setClientSecret("secret");azure.setMasterKeyName("key");azure.setKeyVaultEndpoint("vault");
        ReflectionTestUtils.setField(config,"mongoPropsCfg",props);ReflectionTestUtils.setField(config,"awsCfg",aws);ReflectionTestUtils.setField(config,"azureCfg",azure);
        ReflectionTestUtils.setField(config,"cloudProvider",cloud);ReflectionTestUtils.setField(config,"dataKeyIdName","alias");
        when(props.getUri()).thenReturn("mongodb://localhost:27017/test");when(props.isEncryptionEnabled()).thenReturn(true);
        var encryption=mock(ClientEncryption.class);var provider=mock(DefaultCredentialsProvider.class);var builder=mock(DefaultCredentialsProvider.Builder.class);
        when(builder.build()).thenReturn(provider);when(provider.resolveCredentials()).thenReturn(AwsSessionCredentials.create("access","secret","session"));
        var key=new BsonBinary(new byte[16]);
        when(encryption.getKeyByAltName("alias")).thenReturn(existing?new BsonDocument("_id",key):null);
        when(encryption.createDataKey(anyString(),any())).thenReturn(key);
        try(var credentials=mockStatic(DefaultCredentialsProvider.class);var factory=mockStatic(ClientEncryptions.class)){
            credentials.when(DefaultCredentialsProvider::builder).thenReturn(builder);
            factory.when(()->ClientEncryptions.create(any())).thenAnswer(call->{
                var settings=call.getArgument(0,ClientEncryptionSettings.class);
                assertEquals("encryption.__keyVault",settings.getKeyVaultNamespace());assertTrue(settings.getKmsProviders().containsKey(cloud.name().toLowerCase()));return encryption;
            });
            config.init();assertSame(key,config.getDatakeyId());
            if(existing)verify(encryption,never()).createDataKey(anyString(),any());
            else verify(encryption).createDataKey(eq(cloud.name().toLowerCase()),argThat(o->o.getKeyAltNames().equals(java.util.List.of("alias"))&&o.getMasterKey()!=null));
            config.configureKmsProviders(cloud);assertFalse(config.getKmsProviders().isEmpty());
            var master=config.configureMasterKeyProperties(cloud);
            assertEquals(cloud==CloudProviderEnum.AWS?"region":"azure",master.getString(cloud==CloudProviderEnum.AWS?"region":"provider").getValue());
            when(encryption.decrypt(any())).thenReturn(new BsonString("{\"secret\":\"cleartext\"}"));
            assertEquals("cleartext",config.decryptAsDocument(new Binary(new byte[]{1})).getString("secret"));
            if(cloud==CloudProviderEnum.AWS)verify(encryption).close();
            verify(encryption).decrypt(argThat(b->java.util.Arrays.equals(new byte[]{1},b.getData())));
        }
    }
}
