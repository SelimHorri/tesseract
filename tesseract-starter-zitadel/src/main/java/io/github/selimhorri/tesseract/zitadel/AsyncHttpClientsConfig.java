package io.github.selimhorri.tesseract.zitadel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@ConditionalOnBean(WebClient.class)
@AutoConfiguration
class AsyncHttpClientsConfig {
	
	@ConditionalOnBean(name = "defaultWebClient")
	@Bean
	WebClient zitadelWebClient(WebClient webClient, ZitadelClientProps clientProps) {
		return webClient.mutate()
				.baseUrl(clientProps.baseUrl())
				//.filter(new ZitadelDefaultExchangeRequestInterceptor(clientProps))
				.build();
	}
	
	@Bean
	HttpServiceProxyFactory zitadelProxyFactory(@Qualifier("zitadelWebClient") WebClient webClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(WebClientAdapter.create(webClient))
				.build();
	}
	
}

