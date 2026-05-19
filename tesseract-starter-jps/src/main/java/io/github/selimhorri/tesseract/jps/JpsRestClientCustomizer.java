package io.github.selimhorri.tesseract.jps;

import org.springframework.web.client.RestClient;

@FunctionalInterface
public interface JpsRestClientCustomizer {
	
	void customize(RestClient.Builder jpsRestClientBuilder);
	
}

