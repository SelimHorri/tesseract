package io.github.selimhorri.tesseract.zitadel;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(ZitadelProps.class)
class ZitadelEnablingConfig {}

