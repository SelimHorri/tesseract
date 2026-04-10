package io.github.selimhorri.tesseract.zitadel;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

class DefaultTokenRetrieverSyncClient implements TokenRetrieverSyncClient {
	
	private final RestClient http;
	
	DefaultTokenRetrieverSyncClient(RestClient http) {
		this.http = http;
	}
	
	@Override
	public IdpTokenResponse obtainToken(String clientId, String clientSecret, String grantType, String scope) {
		return this.http.post()
				.uri("/oauth/v2/token", uriBuilder -> uriBuilder
						.queryParam("client_id", clientId)
						.queryParam("client_secret", clientSecret)
						.queryParam("grant_type", grantType)
						.queryParam("scope", scope)
						.build())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				.retrieve()
				.body(IdpTokenResponse.class);
	}
	
}

