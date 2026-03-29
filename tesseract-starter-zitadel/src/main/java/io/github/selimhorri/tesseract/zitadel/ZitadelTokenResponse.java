package io.github.selimhorri.tesseract.zitadel;

import tools.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record ZitadelTokenResponse(String accessToken, String idToken, String refreshToken, Integer expiresIn, String tokenType, String scope) {}

