package dev.selimhorri.tesseract.jps;

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
	
	@ConditionalOnBean(name = "restClient")
	@Bean
	RestClient jpsRestClient(RestClient restClient, JpsProps jpsProps) {
		return restClient.mutate()
				.baseUrl(jpsProps.baseUrl())
				.build();
	}
	
	@Bean
	HttpServiceProxyFactory jpsProxyFactory(@Qualifier("jpsRestClient") RestClient restClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(RestClientAdapter.create(restClient))
				.build();
	}
	
}

