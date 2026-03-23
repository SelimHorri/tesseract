package io.github.selimhorri.tesseract.zitadel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

class DefaultClientHttpRequestInterceptor<T> implements ClientHttpRequestInterceptor {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultClientHttpRequestInterceptor.class);
	private final ZitadelProps idpProps;
	private final Consumer<T> interceptorLogic;
	
	DefaultClientHttpRequestInterceptor(ZitadelProps idpProps, Consumer<T> interceptorLogic) {
		this.idpProps = idpProps;
		this.interceptorLogic = interceptorLogic;
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		if (false) { // TODO: check if actual request URI is part of `this.idpProps.publicUris()`
			LOG.info("Skip intercepting request to {} as it is not a public URI", request.getURI());
		}
		
		LOG.debug("Intercepting request {} {} to ZITADEL", request.getMethod(), request.getURI());
		this.interceptorLogic.accept(null);
		return execution.execute(request, body);
	}
	
}

