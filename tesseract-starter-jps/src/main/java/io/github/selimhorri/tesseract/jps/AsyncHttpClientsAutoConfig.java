package io.github.selimhorri.tesseract.jps;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@AutoConfiguration(afterName = "io.github.selimhorri.tesseract.async.HttpClientsAutoConfig")
@ConditionalOnBean(value = WebClient.class, name = "defaultWebClient")
class AsyncHttpClientsAutoConfig {
	
	@Bean
	WebClient jpsWebClient(WebClient webClient, JpsClientProps clientProps, ObjectProvider<JpsWebClientCustomizer> jpsWebClientCustomizers) {
		var jpsWebClientBuilder = webClient.mutate()
				.baseUrl(clientProps.baseUrl());
		jpsWebClientCustomizers.forEach(c -> c.customize(jpsWebClientBuilder));
		return jpsWebClientBuilder.build();
	}
	
	@Bean
	HttpServiceProxyFactory jpsAsyncProxyFactory(@Qualifier("jpsWebClient") WebClient webClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(WebClientAdapter.create(webClient))
				.build();
	}
	
}

