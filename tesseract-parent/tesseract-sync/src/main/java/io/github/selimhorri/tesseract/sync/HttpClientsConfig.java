package io.github.selimhorri.tesseract.sync;

import io.github.selimhorri.tesseract.core.HttpClientProps;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;

@AutoConfiguration
class HttpClientsConfig {
	
	@Primary
	@Bean
	RestClient defaultRestClient(RestClient.Builder restClientBuilder, HttpClientProps clientProps) {
		return restClientBuilder
				.requestFactory(ClientHttpRequestFactoryBuilder.jdk()
						.withHttpClientCustomizer(httpClientBuilder -> httpClientBuilder
								.version(clientProps.httpVersion() == 1
										? HttpClient.Version.HTTP_1_1
										: HttpClient.Version.HTTP_2)
								.build())
						.build(ClientHttpRequestFactorySettings.defaults()
								.withConnectTimeout(clientProps.connectTimeout())
								.withReadTimeout(clientProps.readTimeout())))
				.build();
	}
	
	@Primary
	@Bean
	HttpServiceProxyFactory syncProxyFactory(RestClient restClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(RestClientAdapter.create(restClient))
				.build();
	}
	
}

