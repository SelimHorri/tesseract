package io.github.selimhorri.tesseract.jps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SyncHttpClientsAutoConfigTest {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(JpsEnablingConfig.class, SyncHttpClientsAutoConfig.class));
	
	@Test
	void shouldNotRegisterJpsBeansWhenNoDefaultRestClientPresent() {
		contextRunner.run(context -> {
			assertThat(context).doesNotHaveBean("jpsRestClient");
			assertThat(context).doesNotHaveBean("jpsSyncProxyFactory");
		});
	}
	
	@Test
	void shouldNotRegisterHttpServiceProxyFactoryWhenNoDefaultRestClientPresent() {
		contextRunner.run(context -> assertThat(context).doesNotHaveBean(HttpServiceProxyFactory.class));
	}
	
	@Test
	void shouldRegisterJpsRestClientWhenDefaultRestClientBeanPresent() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsRestClient");
				});
	}
	
	@Test
	void shouldRegisterJpsSyncProxyFactoryWhenDefaultRestClientBeanPresent() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsSyncProxyFactory");
				});
	}
	
	@Test
	void jpsDefaultRestClientBeanShouldNotBeNull() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.run(context -> {
					RestClient jpsRestClient = context.getBean("jpsRestClient", RestClient.class);
					assertThat(jpsRestClient).isNotNull();
				});
	}
	
	@Test
	void jpsSyncProxyFactoryBeanShouldNotBeNull() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.run(context -> {
					HttpServiceProxyFactory factory = context.getBean("jpsSyncProxyFactory", HttpServiceProxyFactory.class);
					assertThat(factory).isNotNull();
				});
	}
	
	@Test
	void shouldConfigureJpsDefaultRestClientWithDefaultBaseUrl() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsRestClient");
					JpsClientProps props = context.getBean(JpsClientProps.class);
					assertThat(props.baseUrl()).isEqualTo("https://jsonplaceholder.typicode.com");
				});
	}
	
	@Test
	void shouldConfigureJpsDefaultRestClientWithCustomBaseUrl() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.withPropertyValues("tesseract.jps.base-url=https://custom.api.example.com")
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsRestClient");
					JpsClientProps props = context.getBean(JpsClientProps.class);
					assertThat(props.baseUrl()).isEqualTo("https://custom.api.example.com");
				});
	}
	
	@Test
	void shouldLoadFullContextWithDefaultRestClientAndDefaultProps() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(JpsClientProps.class);
					assertThat(context).hasBean("jpsRestClient");
					assertThat(context).hasBean("jpsSyncProxyFactory");
				});
	}
	
	
	@Test
	void shouldApplyDefaultBaseUrlToJpsRestClient() {
		var capturedRequest = new AtomicReference<HttpRequest>();
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.withBean(JpsRestClientCustomizer.class, () -> builder -> builder.requestInterceptor(capturing(capturedRequest)))
				.run(context -> {
					context.getBean("jpsRestClient", RestClient.class).get().uri("/posts/1").retrieve().toBodilessEntity();
					assertThat(capturedRequest.get().getURI()).hasToString("https://jsonplaceholder.typicode.com/posts/1");
				});
	}
	
	@Test
	void shouldApplyCustomBaseUrlToJpsRestClient() {
		var capturedRequest = new AtomicReference<HttpRequest>();
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.withBean(JpsRestClientCustomizer.class, () -> builder -> builder.requestInterceptor(capturing(capturedRequest)))
				.withPropertyValues("tesseract.jps.base-url=https://custom.api.example.com")
				.run(context -> {
					context.getBean("jpsRestClient", RestClient.class).get().uri("/posts/1").retrieve().toBodilessEntity();
					assertThat(capturedRequest.get().getURI()).hasToString("https://custom.api.example.com/posts/1");
				});
	}
	
	@Test
	void shouldApplyAllJpsRestClientCustomizers() {
		var capturedRequest = new AtomicReference<HttpRequest>();
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.withBean("interceptingCustomizer", JpsRestClientCustomizer.class,
						() -> builder -> builder.requestInterceptor(capturing(capturedRequest)))
				.withBean("headerCustomizer", JpsRestClientCustomizer.class,
						() -> builder -> builder.defaultHeader("X-Custom", "jps"))
				.run(context -> {
					context.getBean("jpsRestClient", RestClient.class).get().uri("/posts/1").retrieve().toBodilessEntity();
					assertThat(capturedRequest.get().getHeaders().getFirst("X-Custom")).isEqualTo("jps");
				});
	}
	
	@Test
	void shouldRegisterJpsBeansWithRealTesseractSyncAutoConfig() {
		new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(
						RestClientAutoConfiguration.class,
						classNamed("io.github.selimhorri.tesseract.core.HttpClientEnablingConfig"),
						classNamed("io.github.selimhorri.tesseract.sync.HttpClientsAutoConfig"),
						JpsEnablingConfig.class,
						SyncHttpClientsAutoConfig.class))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("defaultRestClient");
					assertThat(context).hasBean("jpsRestClient");
					assertThat(context).hasBean("jpsSyncProxyFactory");
				});
	}
	
	@Test
	void afterNameShouldReferenceExistingClasses() {
		var autoConfiguration = SyncHttpClientsAutoConfig.class.getAnnotation(AutoConfiguration.class);
		assertThat(autoConfiguration.afterName())
				.isNotEmpty()
				.allSatisfy(name -> assertThatCode(() -> Class.forName(name)).doesNotThrowAnyException());
	}
	
	private static ClientHttpRequestInterceptor capturing(AtomicReference<HttpRequest> capturedRequest) {
		return (request, body, execution) -> {
			capturedRequest.set(request);
			return new MockClientHttpResponse(new byte[0], HttpStatus.OK);
		};
	}
	
	private static Class<?> classNamed(String className) {
		try {
			return Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
