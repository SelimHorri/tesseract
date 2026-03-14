package dev.selimhorri.tesseract.core;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "tesseract.web.http.client")
@Validated
record DefaultHttpClientProps(@DefaultValue("5s") Duration connectTimeout,
							  @DefaultValue("5s") Duration readTimeout,
							  @DefaultValue("") String sslBundleName,
							  @DefaultValue("2") @Min(1) @Max(3) int httpVersion) implements HttpClientProps {}

