package io.github.selimhorri.tesseract.jps;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@ConditionalOnBean(RestClient.class)
@AutoConfiguration
class SyncHttpClientsConfig {
	
	@ConditionalOnMissingBean(name = "jpsRestClient")
	@Bean
	RestClient jpsRestClient(RestClient restClient, JpsClientProps clientProps) {
		return restClient.mutate()
				.baseUrl(clientProps.baseUrl())
				.build();
	}
	
	@ConditionalOnMissingBean(name = "jpsSyncProxyFactory")
	@Bean
	HttpServiceProxyFactory jpsSyncProxyFactory(@Qualifier("jpsRestClient") RestClient restClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(RestClientAdapter.create(restClient))
				.build();
	}
	
}

