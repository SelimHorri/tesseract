package io.github.selimhorri.tesseract.zitadel;

import reactor.core.publisher.Mono;

public interface TokenRetrieverAsyncClient {
	
	Mono<IdpTokenResponse> obtainToken(String clientId,
	                                   String clientSecret,
	                                   String grantType,
	                                   String scope);
	
}

