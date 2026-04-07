package io.github.selimhorri.tesseract.zitadel;

import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/oauth/v2")
public interface TokenRetrieverSyncHttpClient {
	
	@PostExchange(url = "/token", accept = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	ZitadelTokenResponse obtainToken();
	
}

