package io.github.selimhorri.tesseract.jps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class AsyncHttpClientsAutoConfigTest {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(JpsEnablingConfig.class, AsyncHttpClientsAutoConfig.class));
	
	@Test
	void shouldNotRegisterJpsBeansWhenNoDefaultWebClientPresent() {
		contextRunner.run(context -> {
			assertThat(context).doesNotHaveBean("jpsWebClient");
			assertThat(context).doesNotHaveBean("jpsAsyncProxyFactory");
		});
	}
	
	@Test
	void shouldNotRegisterHttpServiceProxyFactoryWhenNoDefaultWebClientPresent() {
		contextRunner.run(context -> assertThat(context).doesNotHaveBean(HttpServiceProxyFactory.class));
	}
	
	@Test
	void shouldRegisterJpsWebClientWhenDefaultWebClientBeanPresent() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsWebClient");
				});
	}
	
	@Test
	void shouldRegisterJpsAsyncProxyFactoryWhenDefaultWebClientBeanPresent() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsAsyncProxyFactory");
				});
	}
	
	@Test
	void jpsDefaultWebClientBeanShouldNotBeNull() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.run(context -> {
					WebClient jpsWebClient = context.getBean("jpsWebClient", WebClient.class);
					assertThat(jpsWebClient).isNotNull();
				});
	}
	
	@Test
	void jpsAsyncProxyFactoryBeanShouldNotBeNull() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.run(context -> {
					HttpServiceProxyFactory factory = context.getBean("jpsAsyncProxyFactory", HttpServiceProxyFactory.class);
					assertThat(factory).isNotNull();
				});
	}
	
	@Test
	void shouldConfigureJpsDefaultWebClientWithDefaultBaseUrl() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsWebClient");
					JpsClientProps props = context.getBean(JpsClientProps.class);
					assertThat(props.baseUrl()).isEqualTo("https://jsonplaceholder.typicode.com");
				});
	}
	
	@Test
	void shouldConfigureJpsDefaultWebClientWithCustomBaseUrl() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.withPropertyValues("tesseract.jps.base-url=https://custom.api.example.com")
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsWebClient");
					JpsClientProps props = context.getBean(JpsClientProps.class);
					assertThat(props.baseUrl()).isEqualTo("https://custom.api.example.com");
				});
	}
	
	@Test
	void shouldLoadFullContextWithDefaultWebClientAndDefaultProps() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(JpsClientProps.class);
					assertThat(context).hasBean("jpsWebClient");
					assertThat(context).hasBean("jpsAsyncProxyFactory");
				});
	}
	
	
	@Test
	void shouldApplyDefaultBaseUrlToJpsWebClient() {
		var capturedRequest = new AtomicReference<ClientRequest>();
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.withBean(JpsWebClientCustomizer.class, () -> builder -> builder.exchangeFunction(capturing(capturedRequest)))
				.run(context -> {
					context.getBean("jpsWebClient", WebClient.class).get().uri("/posts/1").retrieve().toBodilessEntity().block();
					assertThat(capturedRequest.get().url()).hasToString("https://jsonplaceholder.typicode.com/posts/1");
				});
	}
	
	@Test
	void shouldApplyCustomBaseUrlToJpsWebClient() {
		var capturedRequest = new AtomicReference<ClientRequest>();
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.withBean(JpsWebClientCustomizer.class, () -> builder -> builder.exchangeFunction(capturing(capturedRequest)))
				.withPropertyValues("tesseract.jps.base-url=https://custom.api.example.com")
				.run(context -> {
					context.getBean("jpsWebClient", WebClient.class).get().uri("/posts/1").retrieve().toBodilessEntity().block();
					assertThat(capturedRequest.get().url()).hasToString("https://custom.api.example.com/posts/1");
				});
	}
	
	@Test
	void shouldApplyAllJpsWebClientCustomizers() {
		var capturedRequest = new AtomicReference<ClientRequest>();
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.withBean("exchangeCustomizer", JpsWebClientCustomizer.class,
						() -> builder -> builder.exchangeFunction(capturing(capturedRequest)))
				.withBean("headerCustomizer", JpsWebClientCustomizer.class,
						() -> builder -> builder.defaultHeader("X-Custom", "jps"))
				.run(context -> {
					context.getBean("jpsWebClient", WebClient.class).get().uri("/posts/1").retrieve().toBodilessEntity().block();
					assertThat(capturedRequest.get().headers().getFirst("X-Custom")).isEqualTo("jps");
				});
	}
	
	@Test
	void shouldRegisterJpsBeansWithRealTesseractAsyncAutoConfig() {
		new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(
						WebClientAutoConfiguration.class,
						classNamed("io.github.selimhorri.tesseract.core.HttpClientEnablingConfig"),
						classNamed("io.github.selimhorri.tesseract.async.HttpClientsAutoConfig"),
						JpsEnablingConfig.class,
						AsyncHttpClientsAutoConfig.class))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("defaultWebClient");
					assertThat(context).hasBean("jpsWebClient");
					assertThat(context).hasBean("jpsAsyncProxyFactory");
				});
	}
	
	@Test
	void afterNameShouldReferenceExistingClasses() {
		var autoConfiguration = AsyncHttpClientsAutoConfig.class.getAnnotation(AutoConfiguration.class);
		assertThat(autoConfiguration.afterName())
				.isNotEmpty()
				.allSatisfy(name -> assertThatCode(() -> Class.forName(name)).doesNotThrowAnyException());
	}
	
	private static ExchangeFunction capturing(AtomicReference<ClientRequest> capturedRequest) {
		return request -> {
			capturedRequest.set(request);
			return Mono.just(ClientResponse.create(HttpStatus.OK).build());
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
