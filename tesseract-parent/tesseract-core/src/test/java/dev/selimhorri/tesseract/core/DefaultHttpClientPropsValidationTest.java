package dev.selimhorri.tesseract.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultHttpClientPropsValidationTest {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(HttpClientEnablingConfig.class));
	
	@Test
	void shouldSucceedWithHttpVersion1() {
		contextRunner
				.withPropertyValues("tesseract.web.http.client.http-version=1")
				.run(context -> assertThat(context).hasNotFailed());
	}
	
	@Test
	void shouldSucceedWithHttpVersion2() {
		contextRunner
				.withPropertyValues("tesseract.web.http.client.http-version=2")
				.run(context -> assertThat(context).hasNotFailed());
	}
	
	@Test
	void shouldSucceedWithHttpVersion3() {
		contextRunner
				.withPropertyValues("tesseract.web.http.client.http-version=3")
				.run(context -> assertThat(context).hasNotFailed());
	}
	
	@Test
	void shouldFailWhenHttpVersionIsBelowMinimum() {
		contextRunner
				.withPropertyValues("tesseract.web.http.client.http-version=0")
				.run(context -> assertThat(context).hasFailed());
	}
	
	@Test
	void shouldFailWhenHttpVersionIsAboveMaximum() {
		contextRunner
				.withPropertyValues("tesseract.web.http.client.http-version=4")
				.run(context -> assertThat(context).hasFailed());
	}
	
	@Test
	void shouldSucceedWithValidConnectTimeout() {
		contextRunner
				.withPropertyValues("tesseract.web.http.client.connect-timeout=30s")
				.run(context -> assertThat(context).hasNotFailed());
	}
	
	@Test
	void shouldSucceedWithValidReadTimeout() {
		contextRunner
				.withPropertyValues("tesseract.web.http.client.read-timeout=30s")
				.run(context -> assertThat(context).hasNotFailed());
	}
	
}
