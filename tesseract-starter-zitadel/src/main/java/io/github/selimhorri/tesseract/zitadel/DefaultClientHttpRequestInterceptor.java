package io.github.selimhorri.tesseract.zitadel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.function.Consumer;

class DefaultClientHttpRequestInterceptor<T> implements ClientHttpRequestInterceptor, ExchangeFilterFunction {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultClientHttpRequestInterceptor.class);
	private final ZitadelClientProps idpProps;
	private final Consumer<T> interceptorLogic;
	
	DefaultClientHttpRequestInterceptor(ZitadelClientProps idpProps, Consumer<T> interceptorLogic) {
		this.idpProps = idpProps;
		this.interceptorLogic = interceptorLogic;
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		if (this.idpProps.publicUris().stream()
				.anyMatch(request.getURI().normalize().getPath()::startsWith)) {
			LOG.info("Skip intercepting request to {} as it is not a public URI", request.getURI());
			return execution.execute(request, body);
		}
		
		LOG.debug("Intercepting request {} {} to ZITADEL", request.getMethod(), request.getURI());
		this.interceptorLogic.accept(null);
		return execution.execute(request, body);
	}
	
	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		if (this.idpProps.publicUris().stream()
				.anyMatch(request.url().normalize().getPath()::startsWith)) {
			LOG.info("Skip intercepting request to {} as it is not a public URI", request.url());
			return next.exchange(request);
		}
		
		LOG.debug("Intercepting request {} {} to ZITADEL", request.method(), request.url());
		this.interceptorLogic.accept(null);
		return next.exchange(request);
	}
	
}

