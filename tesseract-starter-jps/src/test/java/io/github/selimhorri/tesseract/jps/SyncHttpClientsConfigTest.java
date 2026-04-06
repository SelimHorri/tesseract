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
	void shouldNotRegisterJpsBeansWhenNoDefaultRestClientPresent() {
		contextRunner.run(context -> {
			assertThat(context).doesNotHaveBean("jpsRestClient");
			assertThat(context).doesNotHaveBean("jpsProxyFactory");
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
				.withBean("jpsRestClient", RestClient.class, () -> RestClient.create())
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsRestClient");
				});
	}
	
	@Test
	void shouldRegisterJpsProxyFactoryWhenDefaultRestClientBeanPresent() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.withBean("jpsRestClient", RestClient.class, () -> RestClient.create())
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasBean("jpsProxyFactory");
				});
	}
	
	@Test
	void jpsDefaultRestClientBeanShouldNotBeNull() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.withBean("jpsRestClient", RestClient.class, () -> RestClient.create())
				.run(context -> {
					RestClient jpsRestClient = context.getBean("jpsRestClient", RestClient.class);
					assertThat(jpsRestClient).isNotNull();
				});
	}
	
	@Test
	void jpsProxyFactoryBeanShouldNotBeNull() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.withBean("jpsRestClient", RestClient.class, () -> RestClient.create())
				.run(context -> {
					HttpServiceProxyFactory factory = context.getBean("jpsProxyFactory", HttpServiceProxyFactory.class);
					assertThat(factory).isNotNull();
				});
	}
	
	@Test
	void shouldConfigureJpsDefaultRestClientWithDefaultBaseUrl() {
		contextRunner
				.withBean("defaultRestClient", RestClient.class, RestClient::create)
				.withBean("jpsRestClient", RestClient.class, () -> RestClient.create())
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
				.withBean("jpsRestClient", RestClient.class, () -> RestClient.create())
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
				.withBean("jpsRestClient", RestClient.class, () -> RestClient.create())
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(JpsClientProps.class);
					assertThat(context).hasBean("jpsRestClient");
					assertThat(context).hasBean("jpsProxyFactory");
				});
	}
	
}
