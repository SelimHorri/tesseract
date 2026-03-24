package io.github.selimhorri.tesseract.zitadel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@ConditionalOnBean(RestClient.class)
@AutoConfiguration
class SyncHttpClientsConfig {
	
	@ConditionalOnBean(name = "defaultRestClient")
	@Bean
	RestClient zitadelRestClient(RestClient restClient, ZitadelClientProps clientProps) {
		return restClient.mutate()
				.baseUrl(clientProps.baseUrl())
				.requestInterceptor(new DefaultClientHttpRequestInterceptor<>(clientProps, null)) // TODO: Add interceptor logic
				.build();
	}
	
	@Bean
	HttpServiceProxyFactory zitadelProxyFactory(@Qualifier("zitadelRestClient") RestClient restClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(RestClientAdapter.create(restClient))
				.build();
	}
	
}

