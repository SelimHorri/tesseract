package io.github.selimhorri.tesseract.zitadel;

import org.springframework.web.reactive.function.client.WebClient;

@FunctionalInterface
public interface ZitadelWebClientCustomizer {
	
	void customize(WebClient.Builder zitadelWebClientBuilder);
	
}

