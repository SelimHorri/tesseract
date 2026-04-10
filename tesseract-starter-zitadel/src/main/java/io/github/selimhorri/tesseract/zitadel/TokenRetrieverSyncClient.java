package io.github.selimhorri.tesseract.zitadel;

public interface TokenRetrieverSyncClient {
	
	IdpTokenResponse obtainToken(String clientId, String clientSecret, String grantType, String scope);
	
}

