package dev.selimhorri.tesseract.sync;

import dev.selimhorri.tesseract.core.HttpClientProps;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class HttpClientsConfigTest {
	
	private static HttpClientProps propsWithDefaults() {
		return new HttpClientProps() {
			
			@Override
			public Duration connectTimeout() {
				return Duration.ofSeconds(5);
			}
			
			@Override
			public Duration readTimeout() {
				return Duration.ofSeconds(5);
			}
			
			@Override
			public int httpVersion() {
				return 2;
			}
			
			@Override
			public String sslBundleName() {
				return "";
			}
			
		};
	}
	
	private static HttpClientProps propsWithHttpVersion(int version) {
		return new HttpClientProps() {
			
			@Override
			public Duration connectTimeout() {
				return Duration.ofSeconds(5);
			}
			
			@Override
			public Duration readTimeout() {
				return Duration.ofSeconds(5);
			}
			
			@Override
			public int httpVersion() {
				return version;
			}
			
			@Override
			public String sslBundleName() {
				return "";
			}
			
		};
	}
	
	private static HttpClientProps propsWithTimeouts(Duration connectTimeout, Duration readTimeout) {
		return new HttpClientProps() {
			
			@Override
			public Duration connectTimeout() {
				return connectTimeout;
			}
			
			@Override
			public Duration readTimeout() {
				return readTimeout;
			}
			
			@Override
			public int httpVersion() {
				return 2;
			}
			
			@Override
			public String sslBundleName() {
				return "";
			}
			
		};
	}
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withBean(RestClient.Builder.class, RestClient::builder)
			.withConfiguration(AutoConfigurations.of(HttpClientsConfig.class));
	
	private ApplicationContextRunner withDefaultProps() {
		return contextRunner.withBean(HttpClientProps.class, HttpClientsConfigTest::propsWithDefaults);
	}
	
	@Test
	void shouldRegisterRestClientBean() {
		withDefaultProps().run(context -> assertThat(context).hasSingleBean(RestClient.class));
	}
	
	@Test
	void shouldRegisterHttpServiceProxyFactoryBean() {
		withDefaultProps().run(context -> assertThat(context).hasSingleBean(HttpServiceProxyFactory.class));
	}
	
	@Test
	void restClientBeanShouldNotBeNull() {
		withDefaultProps().run(context -> {
			RestClient restClient = context.getBean(RestClient.class);
			assertThat(restClient).isNotNull();
		});
	}
	
	@Test
	void httpServiceProxyFactoryBeanShouldNotBeNull() {
		withDefaultProps().run(context -> {
			HttpServiceProxyFactory factory = context.getBean(HttpServiceProxyFactory.class);
			assertThat(factory).isNotNull();
		});
	}
	
	@Test
	void shouldLoadContextWithDefaultProperties() {
		withDefaultProps().run(context -> assertThat(context).hasNotFailed());
	}
	
	@Test
	void shouldLoadContextWithHttp1() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithHttpVersion(1))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(RestClient.class);
				});
	}
	
	@Test
	void shouldLoadContextWithHttp2() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithHttpVersion(2))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(RestClient.class);
				});
	}
	
	@Test
	void shouldLoadContextWithHttp3() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithHttpVersion(3))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(RestClient.class);
				});
	}
	
	@Test
	void shouldApplyCustomConnectTimeout() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithTimeouts(Duration.ofSeconds(3), Duration.ofSeconds(5)))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(RestClient.class);
				});
	}
	
	@Test
	void shouldApplyCustomReadTimeout() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithTimeouts(Duration.ofSeconds(5), Duration.ofSeconds(7)))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(RestClient.class);
				});
	}
	
	@Test
	void shouldApplyAllCustomProperties() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithTimeouts(Duration.ofSeconds(3), Duration.ofSeconds(7)))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(RestClient.class);
					assertThat(context).hasSingleBean(HttpServiceProxyFactory.class);
				});
	}
	
	@Test
	void restClientBeanNameShouldBeRestClient() {
		withDefaultProps().run(context -> assertThat(context).hasBean("restClient"));
	}
	
	@Test
	void httpServiceProxyFactoryBeanNameShouldBeSyncProxyFactory() {
		withDefaultProps().run(context -> assertThat(context).hasBean("syncProxyFactory"));
	}
	
}
