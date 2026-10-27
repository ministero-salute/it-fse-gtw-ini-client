package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import it.finanze.sanita.fse2.ms.iniclient.config.GovwayCfg;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.config.IniNetworkCFG;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;

/**
 * Pure unit tests (no full Spring context, no Mongo) for {@link IniNetworkCFG}.
 */
class IniNetworkCFGTest {

	private ProxySelector originalSelector;
	private SSLSocketFactory originalSocketFactory;

	private ISecuritySRV securitySRV;
	private SSLContext customContext;
	private IniCFG iniCFG;
	private GovwayCfg govwayCfg;

	private ApplicationContextRunner runner;

	@BeforeEach
	void setUp() throws Exception {
		originalSelector = ProxySelector.getDefault();
		originalSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();

		customContext = mock(SSLContext.class);
		when(customContext.getSocketFactory()).thenReturn(mock(SSLSocketFactory.class));
		securitySRV = mock(ISecuritySRV.class);
		when(securitySRV.createSslCustomContext()).thenReturn(customContext);

		iniCFG = new IniCFG();
		iniCFG.setUrlWsdlDocumentRegistryService("https://ini-a.example.org/publish");
		iniCFG.setUrlWsdlUpdateDocumentRegistryService("https://ini-b.example.org/update");
		iniCFG.setUrlWsdlDeletetService("https://ini-c.example.org/delete");
		iniCFG.setUrlWsdlRecuperoRiferimentoService("https://ini-d.example.org/ref");

		govwayCfg = new GovwayCfg();
		govwayCfg.setGovwayUrl("localhost");
		govwayCfg.setGovwayPort(8080);

		runner = new ApplicationContextRunner()
			.withUserConfiguration(IniNetworkCFG.class)
			.withInitializer(appCtx -> {
				appCtx.getBeanFactory().registerSingleton("securitySRV", securitySRV);
				appCtx.getBeanFactory().registerSingleton("iniCFG", iniCFG);
				appCtx.getBeanFactory().registerSingleton("govwayCfg", govwayCfg);
			});
	}

	@AfterEach
	void tearDown() {
		ProxySelector.setDefault(originalSelector);
		HttpsURLConnection.setDefaultSSLSocketFactory(originalSocketFactory);
	}

	@Test
	void openSamlBootstrapAlwaysPresent() {
		runner.run(ctx -> {
			assertTrue(ctx.containsBean(IniNetworkCFG.OPENSAML_BOOTSTRAP));
			assertEquals(Boolean.TRUE, ctx.getBean(IniNetworkCFG.OPENSAML_BOOTSTRAP, Boolean.class));
		});
	}

	@Test
	void sslAndProxyDisabledByDefault() {
		runner.withPropertyValues("govway-url=").run(ctx -> {
			assertFalse(ctx.containsBean("sslContext"));
			assertFalse(ctx.containsBean(IniNetworkCFG.PROXY_URIS));
			assertFalse(ctx.containsBean("proxySelector"));
			assertSame(originalSelector, ProxySelector.getDefault());
			assertSame(originalSocketFactory, HttpsURLConnection.getDefaultSSLSocketFactory());
		});
	}

	@Test
	void sslContextCreatedAndRegisteredAsDefault() {
		runner.withPropertyValues("ini.client.enable-ssl=true").run(ctx -> {
			assertSame(customContext, ctx.getBean(SSLContext.class));
			assertSame(customContext.getSocketFactory(), HttpsURLConnection.getDefaultSSLSocketFactory());
			assertNotNull(HttpsURLConnection.getDefaultSSLSocketFactory());
		});
	}

	@Test
	void sslContextNotCreatedWhenFlagFalse() {
		runner.withPropertyValues("ini.client.enable-ssl=false").run(ctx ->
			assertFalse(ctx.containsBean("sslContext")));
	}

	@Test
	@SuppressWarnings("unchecked")
	void proxyUrisBuiltFromIniCfg() {
		runner.withPropertyValues("govway-url=localhost").run(ctx -> {
			List<URI> uris = (List<URI>) ctx.getBean(IniNetworkCFG.PROXY_URIS);
			assertEquals(4, uris.size());
			assertEquals("ini-a.example.org", uris.get(0).getHost());
			assertEquals("ini-d.example.org", uris.get(3).getHost());
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	void proxyUrisSkipInvalidUrls() {
		iniCFG.setUrlWsdlDeletetService("not a valid uri");
		runner.withPropertyValues("govway-url=localhost").run(ctx -> {
			List<URI> uris = (List<URI>) ctx.getBean(IniNetworkCFG.PROXY_URIS);
			assertEquals(3, uris.size());
		});
	}

	@Test
	void proxySelectorRoutesOnlyIniHostsThroughGovway() {
		runner.withPropertyValues("govway-url=localhost").run(ctx -> {
			ProxySelector selector = ctx.getBean(ProxySelector.class);
			assertSame(selector, ProxySelector.getDefault());

			List<Proxy> viaProxy = selector.select(URI.create("https://ini-b.example.org/other/path"));
			assertEquals(1, viaProxy.size());
			assertEquals(Proxy.Type.HTTP, viaProxy.get(0).type());
			InetSocketAddress address = (InetSocketAddress) viaProxy.get(0).address();
			assertEquals(8080, address.getPort());

			List<Proxy> direct = selector.select(URI.create("https://unrelated.example.com/"));
			assertEquals(List.of(Proxy.NO_PROXY), direct);
		});
	}

	@Test
	void proxySelectorConnectFailedDoesNotThrow() {
		runner.withPropertyValues("govway-url=localhost").run(ctx -> {
			ProxySelector selector = ctx.getBean(ProxySelector.class);
			assertNotNull(selector);
			selector.connectFailed(URI.create("https://ini-a.example.org/"), new InetSocketAddress("localhost", 8080),
				new IOException("boom"));
		});
	}
}
