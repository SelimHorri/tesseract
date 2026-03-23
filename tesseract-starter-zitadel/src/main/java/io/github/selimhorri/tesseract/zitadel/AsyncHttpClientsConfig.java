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
	
	@ConditionalOnBean(name = "webClient")
	@Bean
	WebClient zitadelWebClient(WebClient webClient, ZitadelProps zitadelProps) {
		return webClient.mutate()
				.baseUrl(zitadelProps.baseUrl())
				//.requestInterceptor(new DefaultClientHttpRequestInterceptor<>(zitadelProps, null)) // TODO: implement interceptor for reactive
				.build();
	}
	
	@Bean
	HttpServiceProxyFactory zitadelProxyFactory(@Qualifier("zitadelWebClient") WebClient webClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(WebClientAdapter.create(webClient))
				.build();
	}
	
}

