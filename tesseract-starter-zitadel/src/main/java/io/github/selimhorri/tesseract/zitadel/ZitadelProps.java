package io.github.selimhorri.tesseract.zitadel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Set;

@ConfigurationProperties(prefix = "tesseract.zitadel")
@Validated
public record ZitadelProps(@DefaultValue("http://127.0.0.1:8080") String baseUrl, Set<String> publicUris) {
	
	public ZitadelProps {
		publicUris = Set.copyOf(Objects.requireNonNullElseGet(publicUris, Set::of));
	}
	
}

