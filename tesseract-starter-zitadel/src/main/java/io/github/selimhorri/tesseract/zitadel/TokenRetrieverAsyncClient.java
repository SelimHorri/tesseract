package io.github.selimhorri.tesseract.zitadel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TokenRetrieverAsyncClient {
	
	private final IdpClientProps clientProps;
	private final WebClient http;
	
	TokenRetrieverAsyncClient(IdpClientProps clientProps, @Qualifier("zitadelWebClient") WebClient http) {
		this.clientProps = clientProps;
		this.http = http;
	}
	
	public Mono<IdpTokenResponse> obtainToken() {
		return this.http.post()
				.uri("/oauth/v2/token", uriBuilder -> uriBuilder
						.queryParam("client_id", this.clientProps.clientId())
						.queryParam("client_secret", this.clientProps.clientSecret())
						.queryParam("grant_type", this.clientProps.grantType())
						.queryParam("scope", this.clientProps.scope())
						.build())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				.retrieve()
				.bodyToMono(IdpTokenResponse.class);
	}
	
}

