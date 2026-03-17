package io.github.selimhorri.tesseract.jps;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(JpsProps.class)
class JpsEnablingConfig {}

