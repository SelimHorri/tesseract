package io.github.selimhorri.tesseract.jps;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@ConditionalOnBean(name = "defaultWebClient")
@AutoConfiguration(afterName = "io.github.selimhorri.tesseract.async.HttpClientsConfig")
class AsyncHttpClientsConfig {
	
	@ConditionalOnMissingBean
	@ConditionalOnBean(name = "defaultWebClient")
	@Bean
	WebClient jpsWebClient(WebClient webClient, JpsClientProps clientProps) {
		return webClient.mutate()
				.baseUrl(clientProps.baseUrl())
				.build();
	}
	
	@ConditionalOnMissingBean(name = "jpsAsyncProxyFactory")
	@Bean
	HttpServiceProxyFactory jpsAsyncProxyFactory(@Qualifier("jpsWebClient") WebClient webClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(WebClientAdapter.create(webClient))
				.build();
	}
	
}

