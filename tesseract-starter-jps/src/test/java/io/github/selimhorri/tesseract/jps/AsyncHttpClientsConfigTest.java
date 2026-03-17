package io.github.selimhorri.tesseract.jps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncHttpClientsConfigTest {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(JpsEnablingConfig.class, AsyncHttpClientsConfig.class));
	
	@Test
	void shouldNotRegisterJpsBeansWhenNoWebClientPresent() {
		contextRunner.run(context -> {
			assertThat(context).doesNotHaveBean("jpsWebClient");
			assertThat(context).doesNotHaveBean("jpsProxyFactory");
		});
	}
	
	@Test
	void shouldNotRegisterHttpServiceProxyFactoryWhenNoWebClientPresent() {
		contextRunner.run(context -> assertThat(context).doesNotHaveBean(HttpServiceProxyFactory.class));
	}
	
	@Test
	void shouldRegisterJpsWebClientWhenWebClientBeanPresent() {
		contextRunner
				.withBean("webClient", WebClient.class, WebClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsWebClient");
				});
	}
	
	@Test
	void shouldRegisterJpsProxyFactoryWhenWebClientBeanPresent() {
		contextRunner
				.withBean("webClient", WebClient.class, WebClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsProxyFactory");
				});
	}
	
	@Test
	void jpsWebClientBeanShouldNotBeNull() {
		contextRunner
				.withBean("webClient", WebClient.class, WebClient::create)
				.run(context -> {
					WebClient jpsWebClient = context.getBean("jpsWebClient", WebClient.class);
					assertThat(jpsWebClient).isNotNull();
				});
	}
	
	@Test
	void jpsProxyFactoryBeanShouldNotBeNull() {
		contextRunner
				.withBean("webClient", WebClient.class, WebClient::create)
				.run(context -> {
					HttpServiceProxyFactory factory = context.getBean("jpsProxyFactory", HttpServiceProxyFactory.class);
					assertThat(factory).isNotNull();
				});
	}
	
	@Test
	void shouldConfigureJpsWebClientWithDefaultBaseUrl() {
		contextRunner
				.withBean("webClient", WebClient.class, WebClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsWebClient");
					JpsProps props = context.getBean(JpsProps.class);
					assertThat(props.baseUrl()).isEqualTo("https://jsonplaceholder.typicode.com");
				});
	}
	
	@Test
	void shouldConfigureJpsWebClientWithCustomBaseUrl() {
		contextRunner
				.withBean("webClient", WebClient.class, WebClient::create)
				.withPropertyValues("tesseract.jps.base-url=https://custom.api.example.com")
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsWebClient");
					JpsProps props = context.getBean(JpsProps.class);
					assertThat(props.baseUrl()).isEqualTo("https://custom.api.example.com");
				});
	}
	
	@Test
	void shouldLoadFullContextWithWebClientAndDefaultProps() {
		contextRunner
				.withBean("webClient", WebClient.class, WebClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(JpsProps.class);
					assertThat(context).hasBean("jpsWebClient");
					assertThat(context).hasBean("jpsProxyFactory");
				});
	}
	
}
