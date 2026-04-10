package io.github.selimhorri.tesseract.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class HttpClientEnablingConfigTest {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(HttpClientEnablingConfig.class));
	
	@Test
	void shouldRegisterDefaultHttpClientPropsBean() {
		contextRunner.run(context -> assertThat(context).hasSingleBean(DefaultHttpClientProps.class));
	}
	
	@Test
	void shouldExposeHttpClientPropsInterface() {
		contextRunner.run(context -> assertThat(context).hasSingleBean(HttpClientProps.class));
	}
	
	@Test
	void shouldBindDefaultConnectTimeout() {
		contextRunner.run(context -> {
			HttpClientProps props = context.getBean(HttpClientProps.class);
			assertThat(props.connectTimeout()).isEqualTo(Duration.ofSeconds(5));
		});
	}
	
	@Test
	void shouldBindDefaultReadTimeout() {
		contextRunner.run(context -> {
			HttpClientProps props = context.getBean(HttpClientProps.class);
			assertThat(props.readTimeout()).isEqualTo(Duration.ofSeconds(5));
		});
	}
	
	@Test
	void shouldBindDefaultHttpVersion() {
		contextRunner.run(context -> {
			HttpClientProps props = context.getBean(HttpClientProps.class);
			assertThat(props.httpVersion()).isEqualTo(2);
		});
	}
	
	@Test
	void shouldBindDefaultSslBundleName() {
		contextRunner.run(context -> {
			HttpClientProps props = context.getBean(HttpClientProps.class);
			assertThat(props.sslBundleName()).isEmpty();
		});
	}
	
	@Test
	void shouldBindCustomConnectTimeout() {
		contextRunner
				.withPropertyValues("tesseract.main.client.connect-timeout=10s")
				.run(context -> {
					HttpClientProps props = context.getBean(HttpClientProps.class);
					assertThat(props.connectTimeout()).isEqualTo(Duration.ofSeconds(10));
				});
	}
	
	@Test
	void shouldBindCustomReadTimeout() {
		contextRunner
				.withPropertyValues("tesseract.main.client.read-timeout=15s")
				.run(context -> {
					HttpClientProps props = context.getBean(HttpClientProps.class);
					assertThat(props.readTimeout()).isEqualTo(Duration.ofSeconds(15));
				});
	}
	
	@Test
	void shouldBindHttpVersion1() {
		contextRunner
				.withPropertyValues("tesseract.main.client.http-version=1")
				.run(context -> {
					HttpClientProps props = context.getBean(HttpClientProps.class);
					assertThat(props.httpVersion()).isEqualTo(1);
				});
	}
	
	@Test
	void shouldBindHttpVersion3() {
		contextRunner
				.withPropertyValues("tesseract.main.client.http-version=3")
				.run(context -> {
					HttpClientProps props = context.getBean(HttpClientProps.class);
					assertThat(props.httpVersion()).isEqualTo(3);
				});
	}
	
	@Test
	void shouldBindCustomSslBundleName() {
		contextRunner
				.withPropertyValues("tesseract.main.client.ssl-bundle-name=my-ssl-bundle")
				.run(context -> {
					HttpClientProps props = context.getBean(HttpClientProps.class);
					assertThat(props.sslBundleName()).isEqualTo("my-ssl-bundle");
				});
	}
	
	@Test
	void shouldBindAllCustomValues() {
		contextRunner
				.withPropertyValues(
						"tesseract.main.client.connect-timeout=10s",
						"tesseract.main.client.read-timeout=15s",
						"tesseract.main.client.http-version=1",
						"tesseract.main.client.ssl-bundle-name=my-bundle"
				)
				.run(context -> {
					HttpClientProps props = context.getBean(HttpClientProps.class);
					assertThat(props.connectTimeout()).isEqualTo(Duration.ofSeconds(10));
					assertThat(props.readTimeout()).isEqualTo(Duration.ofSeconds(15));
					assertThat(props.httpVersion()).isEqualTo(1);
					assertThat(props.sslBundleName()).isEqualTo("my-bundle");
				});
	}
	
}
