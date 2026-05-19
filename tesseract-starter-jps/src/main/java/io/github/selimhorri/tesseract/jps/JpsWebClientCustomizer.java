package io.github.selimhorri.tesseract.jps;

import org.springframework.web.reactive.function.client.WebClient;

@FunctionalInterface
public interface JpsWebClientCustomizer {
	
	void customize(WebClient.Builder jpsWebClientBuilder);
	
}

