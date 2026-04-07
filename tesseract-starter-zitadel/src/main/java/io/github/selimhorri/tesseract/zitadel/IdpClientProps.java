package io.github.selimhorri.tesseract.zitadel;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Set;

@ConfigurationProperties(prefix = "tesseract.zitadel")
@Validated
public record IdpClientProps(@DefaultValue("http://127.0.0.1:8080") String baseUrl,
                             @NotBlank(message = "client-id MUST be specified") String clientId,
                             @NotBlank(message = "client-secret MUST be specified") String clientSecret,
                             @DefaultValue("client_credentials") String grantType,
                             @DefaultValue("openid") String scope,
                             Set<String> whitelistedPaths) {
	
	public IdpClientProps {
		whitelistedPaths = Set.copyOf(Objects.requireNonNullElseGet(whitelistedPaths, Set::of));
	}
	
}

