package io.github.selimhorri.tesseract.zitadel;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@ConditionalOnBean(name = "defaultRestClient")
@AutoConfiguration
class SyncHttpClientsConfig {
	
	@ConditionalOnMissingBean(name = "zitadelRestClient")
	@Bean
	RestClient zitadelRestClient(RestClient restClient, IdpClientProps clientProps, ObjectProvider<ZitadelRestClientCustomizer> restClientCustomizers) {
		var zitadelRestClientBuilder = restClient.mutate()
				.baseUrl(clientProps.baseUrl());
		restClientCustomizers.forEach(customizer -> customizer.customize(zitadelRestClientBuilder));
		return zitadelRestClientBuilder.build();
	}
	
	@ConditionalOnMissingBean(name = "zitadelProxyFactory")
	@Bean
	HttpServiceProxyFactory zitadelProxyFactory(@Qualifier("zitadelRestClient") RestClient restClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(RestClientAdapter.create(restClient))
				.build();
	}
	
	@ConditionalOnMissingBean(name = "tokenRetrieverSyncClient")
	@Bean
	TokenRetrieverSyncClient tokenRetrieverSyncClient(@Qualifier("zitadelRestClient") RestClient restClient) {
		return new DefaultTokenRetrieverSyncClient(restClient);
	}
	
}

