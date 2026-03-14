package dev.selimhorri.tesseract.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(DefaultHttpClientProps.class)
class HttpClientEnablingConfig {}

