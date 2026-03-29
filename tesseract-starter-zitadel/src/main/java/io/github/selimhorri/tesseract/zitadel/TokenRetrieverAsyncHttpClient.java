package io.github.selimhorri.tesseract.zitadel;

import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@HttpExchange("/v2/oauth")
public interface TokenRetrieverAsyncHttpClient {
	
	@PostExchange(url = "/token", accept = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	Mono<ZitadelTokenResponse> obtainToken();
	
}

