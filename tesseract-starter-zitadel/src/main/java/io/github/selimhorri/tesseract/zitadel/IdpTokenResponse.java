package io.github.selimhorri.tesseract.zitadel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record IdpTokenResponse(String accessToken, String idToken, String refreshToken, Integer expiresIn, String tokenType, String scope) {}

