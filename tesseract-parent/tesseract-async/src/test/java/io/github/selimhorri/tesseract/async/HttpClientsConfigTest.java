package io.github.selimhorri.tesseract.async;

import io.github.selimhorri.tesseract.core.HttpClientProps;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.reactive.function.client.WebClient;
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
			.withBean(WebClient.Builder.class, WebClient::builder)
			.withConfiguration(AutoConfigurations.of(HttpClientsConfig.class));
	
	private ApplicationContextRunner withDefaultProps() {
		return contextRunner.withBean(HttpClientProps.class, HttpClientsConfigTest::propsWithDefaults);
	}
	
	@Test
	void shouldRegisterDefaultWebClientBean() {
		withDefaultProps().run(context -> assertThat(context).hasSingleBean(WebClient.class));
	}
	
	@Test
	void shouldRegisterHttpServiceProxyFactoryBean() {
		withDefaultProps().run(context -> assertThat(context).hasSingleBean(HttpServiceProxyFactory.class));
	}
	
	@Test
	void defaultWebClientBeanShouldNotBeNull() {
		withDefaultProps().run(context -> {
			WebClient webClient = context.getBean(WebClient.class);
			assertThat(webClient).isNotNull();
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
					assertThat(context).hasSingleBean(WebClient.class);
				});
	}
	
	@Test
	void shouldLoadContextWithHttp2() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithHttpVersion(2))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(WebClient.class);
				});
	}
	
	@Test
	void shouldLoadContextWithHttp3() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithHttpVersion(3))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(WebClient.class);
				});
	}
	
	@Test
	void shouldApplyCustomConnectTimeout() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithTimeouts(Duration.ofSeconds(3), Duration.ofSeconds(5)))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(WebClient.class);
				});
	}
	
	@Test
	void shouldApplyCustomReadTimeout() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithTimeouts(Duration.ofSeconds(5), Duration.ofSeconds(7)))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(WebClient.class);
				});
	}
	
	@Test
	void shouldApplyAllCustomProperties() {
		contextRunner
				.withBean(HttpClientProps.class, () -> propsWithTimeouts(Duration.ofSeconds(3), Duration.ofSeconds(7)))
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(WebClient.class);
					assertThat(context).hasSingleBean(HttpServiceProxyFactory.class);
				});
	}
	
	@Test
	void webClientBeanNameShouldBeDefaultWebClient() {
		withDefaultProps().run(context -> assertThat(context).hasBean("defaultWebClient"));
	}
	
	@Test
	void httpServiceProxyFactoryBeanNameShouldBeAsyncProxyFactory() {
		withDefaultProps().run(context -> assertThat(context).hasBean("asyncProxyFactory"));
	}
	
}
