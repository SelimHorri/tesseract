package io.github.selimhorri.tesseract.jps;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "tesseract.jps")
@Validated
public record JpsProps(@DefaultValue("https://jsonplaceholder.typicode.com") String baseUrl) {}

