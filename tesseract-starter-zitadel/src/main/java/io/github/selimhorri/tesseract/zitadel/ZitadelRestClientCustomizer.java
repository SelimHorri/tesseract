package io.github.selimhorri.tesseract.zitadel;

import org.springframework.web.client.RestClient;

@FunctionalInterface
public interface ZitadelRestClientCustomizer {
	
	void customize(RestClient.Builder zitadelRestClientBuilder);
	
}

