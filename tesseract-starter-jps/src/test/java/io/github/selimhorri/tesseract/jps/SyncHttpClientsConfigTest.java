package io.github.selimhorri.tesseract.jps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;

class SyncHttpClientsConfigTest {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(JpsEnablingConfig.class, SyncHttpClientsConfig.class));
	
	@Test
	void shouldNotRegisterJpsBeansWhenNoRestClientPresent() {
		contextRunner.run(context -> {
			assertThat(context).doesNotHaveBean("jpsRestClient");
			assertThat(context).doesNotHaveBean("jpsProxyFactory");
		});
	}
	
	@Test
	void shouldNotRegisterHttpServiceProxyFactoryWhenNoRestClientPresent() {
		contextRunner.run(context -> assertThat(context).doesNotHaveBean(HttpServiceProxyFactory.class));
	}
	
	@Test
	void shouldRegisterJpsRestClientWhenRestClientBeanPresent() {
		contextRunner
				.withBean("restClient", RestClient.class, RestClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsRestClient");
				});
	}
	
	@Test
	void shouldRegisterJpsProxyFactoryWhenRestClientBeanPresent() {
		contextRunner
				.withBean("restClient", RestClient.class, RestClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsProxyFactory");
				});
	}
	
	@Test
	void jpsRestClientBeanShouldNotBeNull() {
		contextRunner
				.withBean("restClient", RestClient.class, RestClient::create)
				.run(context -> {
					RestClient jpsRestClient = context.getBean("jpsRestClient", RestClient.class);
					assertThat(jpsRestClient).isNotNull();
				});
	}
	
	@Test
	void jpsProxyFactoryBeanShouldNotBeNull() {
		contextRunner
				.withBean("restClient", RestClient.class, RestClient::create)
				.run(context -> {
					HttpServiceProxyFactory factory = context.getBean("jpsProxyFactory", HttpServiceProxyFactory.class);
					assertThat(factory).isNotNull();
				});
	}
	
	@Test
	void shouldConfigureJpsRestClientWithDefaultBaseUrl() {
		contextRunner
				.withBean("restClient", RestClient.class, RestClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsRestClient");
					JpsProps props = context.getBean(JpsProps.class);
					assertThat(props.baseUrl()).isEqualTo("https://jsonplaceholder.typicode.com");
				});
	}
	
	@Test
	void shouldConfigureJpsRestClientWithCustomBaseUrl() {
		contextRunner
				.withBean("restClient", RestClient.class, RestClient::create)
				.withPropertyValues("tesseract.jps.base-url=https://custom.api.example.com")
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsRestClient");
					JpsProps props = context.getBean(JpsProps.class);
					assertThat(props.baseUrl()).isEqualTo("https://custom.api.example.com");
				});
	}
	
	@Test
	void shouldLoadFullContextWithRestClientAndDefaultProps() {
		contextRunner
				.withBean("restClient", RestClient.class, RestClient::create)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(JpsProps.class);
					assertThat(context).hasBean("jpsRestClient");
					assertThat(context).hasBean("jpsProxyFactory");
				});
	}
	
}
