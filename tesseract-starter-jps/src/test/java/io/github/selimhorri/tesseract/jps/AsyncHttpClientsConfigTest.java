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
	void shouldNotRegisterJpsBeansWhenNoDefaultWebClientPresent() {
		contextRunner.run(context -> {
			assertThat(context).doesNotHaveBean("jpsWebClient");
			assertThat(context).doesNotHaveBean("jpsProxyFactory");
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
				.withBean("jpsWebClient", WebClient.class, () -> WebClient.create())
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsWebClient");
				});
	}
	
	@Test
	void shouldRegisterJpsProxyFactoryWhenDefaultWebClientBeanPresent() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.withBean("jpsWebClient", WebClient.class, () -> WebClient.create())
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsProxyFactory");
				});
	}
	
	@Test
	void jpsDefaultWebClientBeanShouldNotBeNull() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.withBean("jpsWebClient", WebClient.class, () -> WebClient.create())
				.run(context -> {
					WebClient jpsWebClient = context.getBean("jpsWebClient", WebClient.class);
					assertThat(jpsWebClient).isNotNull();
				});
	}
	
	@Test
	void jpsProxyFactoryBeanShouldNotBeNull() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.withBean("jpsWebClient", WebClient.class, () -> WebClient.create())
				.run(context -> {
					HttpServiceProxyFactory factory = context.getBean("jpsProxyFactory", HttpServiceProxyFactory.class);
					assertThat(factory).isNotNull();
				});
	}
	
	@Test
	void shouldConfigureJpsDefaultWebClientWithDefaultBaseUrl() {
		contextRunner
				.withBean("defaultWebClient", WebClient.class, WebClient::create)
				.withBean("jpsWebClient", WebClient.class, () -> WebClient.create())
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
				.withBean("jpsWebClient", WebClient.class, () -> WebClient.create())
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
				.withBean("jpsWebClient", WebClient.class, () -> WebClient.create())
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(JpsClientProps.class);
					assertThat(context).hasBean("jpsWebClient");
					assertThat(context).hasBean("jpsProxyFactory");
				});
	}
	
}
