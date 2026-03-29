package io.github.selimhorri.tesseract.zitadel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@AutoConfiguration
class HttpClientProxiesConfig {
	
	//@ConditionalOnBean(name = "zitadelRestClient") //FIXME: if this is decommented => issue when exposing `tokenRetrieverSyncHttpClient` bean
	@Bean
	TokenRetrieverSyncHttpClient tokenRetrieverSyncHttpClient(@Qualifier("zitadelProxyFactory") HttpServiceProxyFactory proxyFactory) {
		return proxyFactory.createClient(TokenRetrieverSyncHttpClient.class);
	}
	
	@ConditionalOnBean(name = "zitadelWebClient")
	@Bean
	TokenRetrieverAsyncHttpClient tokenRetrieverAsyncHttpClient(@Qualifier("zitadelProxyFactory") HttpServiceProxyFactory proxyFactory) {
		return proxyFactory.createClient(TokenRetrieverAsyncHttpClient.class);
	}
	
}

