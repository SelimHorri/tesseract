package io.github.selimhorri.tesseract.jps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class JpsEnablingConfigTest {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(JpsEnablingConfig.class));
	
	@Test
	void shouldRegisterJpsPropsBean() {
		contextRunner.run(context -> assertThat(context).hasSingleBean(JpsClientProps.class));
	}
	
	@Test
	void jpsPropsBeanShouldNotBeNull() {
		contextRunner.run(context -> {
			JpsClientProps props = context.getBean(JpsClientProps.class);
			assertThat(props).isNotNull();
		});
	}
	
	@Test
	void shouldBindDefaultBaseUrl() {
		contextRunner.run(context -> {
			JpsClientProps props = context.getBean(JpsClientProps.class);
			assertThat(props.baseUrl()).isEqualTo("https://jsonplaceholder.typicode.com");
		});
	}
	
	@Test
	void shouldBindCustomBaseUrl() {
		contextRunner
				.withPropertyValues("tesseract.jps.base-url=https://custom.api.example.com")
				.run(context -> {
					JpsClientProps props = context.getBean(JpsClientProps.class);
					assertThat(props.baseUrl()).isEqualTo("https://custom.api.example.com");
				});
	}
	
	@Test
	void shouldLoadContextWithDefaultConfiguration() {
		contextRunner.run(context -> assertThat(context).hasNotFailed());
	}
	
	@Test
	void shouldLoadContextWithCustomBaseUrl() {
		contextRunner
				.withPropertyValues("tesseract.jps.base-url=https://api.test.org")
				.run(context -> assertThat(context).hasNotFailed());
	}
	
}
