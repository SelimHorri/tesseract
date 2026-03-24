package io.github.selimhorri.tesseract.async;

import io.github.selimhorri.tesseract.core.HttpClientProps;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.http.client.reactive.ClientHttpConnectorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;

@AutoConfiguration
class HttpClientsConfig {
	
	@Primary
	@Bean
	WebClient defaultWebClient(WebClient.Builder webClientBuilder, HttpClientProps clientProps) {
		return webClientBuilder
				.clientConnector(ClientHttpConnectorBuilder.jdk()
						.withHttpClientCustomizer(httpClientBuilder -> httpClientBuilder
								.version(clientProps.httpVersion() == 1
										? HttpClient.Version.HTTP_1_1
										: HttpClient.Version.HTTP_2)
								.build())
						.build(HttpClientSettings.defaults()
								.withConnectTimeout(clientProps.connectTimeout())
								.withReadTimeout(clientProps.readTimeout())))
				.build();
	}
	
	@Primary
	@Bean
	HttpServiceProxyFactory asyncProxyFactory(WebClient webClient) {
		return HttpServiceProxyFactory.builder()
				.exchangeAdapter(WebClientAdapter.create(webClient))
				.build();
	}
	
}

