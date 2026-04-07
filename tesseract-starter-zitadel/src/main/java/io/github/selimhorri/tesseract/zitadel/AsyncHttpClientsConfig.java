package io.github.selimhorri.tesseract.zitadel;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@ConditionalOnBean(name = "defaultWebClient")
@AutoConfiguration
class AsyncHttpClientsConfig {
	
	@ConditionalOnMissingBean(name = "zitadelWebClient")
	@Bean
	WebClient zitadelWebClient(WebClient webClient, IdpClientProps clientProps, ObjectProvider<ZitadelWebClientCustomizer> webClientCustomizers) {
		var zitadelWebClientBuilder = webClient.mutate()
				.baseUrl(clientProps.baseUrl());
		webClientCustomizers.forEach(customizer -> customizer.customize(zitadelWebClientBuilder));
		return zitadelWebClientBuilder.build();
	}
	
	@ConditionalOnMissingBean(name = "zitadelProxyFactory")
	@Bean
	HttpServiceProxyFactory zitadelProxyFactory(@Qualifier("zitadelWebClient") WebClient webClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(WebClientAdapter.create(webClient))
				.build();
	}
	
	@ConditionalOnMissingBean(name = "tokenRetrieverAsyncClient")
	@Bean
	TokenRetrieverAsyncClient tokenRetrieverAsyncClient(IdpClientProps clientProps, @Qualifier("zitadelWebClient") WebClient webClient) {
		return new TokenRetrieverAsyncClient(clientProps, webClient);
	}
	
}

