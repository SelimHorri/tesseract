package io.github.selimhorri.tesseract.zitadel;

import org.springframework.beans.factory.ObjectProvider;
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
	RestClient zitadelRestClient(RestClient restClient, ZitadelClientProps clientProps, ObjectProvider<ZitadelRestClientCustomizer> restClientCustomizers) {
		var zitadelRestClientBuilder = restClient.mutate()
				.baseUrl(clientProps.baseUrl());
		restClientCustomizers.forEach(customizer -> customizer.customize(zitadelRestClientBuilder));
		return zitadelRestClientBuilder.build();
	}
	
	@Bean
	HttpServiceProxyFactory zitadelProxyFactory(@Qualifier("zitadelRestClient") RestClient restClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(RestClientAdapter.create(restClient))
				.build();
	}
	
}

