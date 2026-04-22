# Tesseract Async (`tesseract-async`)

Tesseract Async is a specialized module for asynchronous and reactive HTTP communication. It provides a pre-configured `WebClient` bean and an `HttpServiceProxyFactory` for easily creating reactive HTTP interface clients.

## Integration

To use Tesseract Async in your Maven project, add the following dependency:

```xml

<dependency>
    <groupId>io.github.selimhorri</groupId>
    <artifactId>tesseract-async</artifactId>
    <version>1.0.2</version>
</dependency>
```

## Features

- **Auto-Configured `WebClient`**: Automatically sets up a `@Primary` `WebClient` bean using settings from Tesseract Core.
- **Reactive Proxy Support**: Configures an `HttpServiceProxyFactory` with a `WebClientAdapter` for creating asynchronous HTTP interface-based clients.
- **JDK HttpClient Backend**: Leverages the modern Java 11+ `java.net.http.HttpClient` through the reactive `ClientHttpConnector` to ensure consistency with the synchronous module.

## Exposed Beans

The following beans are automatically configured and available for injection:

1.  **`WebClient`**: A primary reactive bean configured with custom timeouts, HTTP version, and SSL settings.
2.  **`HttpServiceProxyFactory`**: Configured with a `WebClientAdapter` for creating asynchronous HTTP interface proxies.

## Infrastructure and Configuration

### `WebClient` Configuration
The library configures the `WebClient` using the standard Spring Boot `WebClient.Builder` and the JDK-based `ClientHttpConnector`:

```java
@Primary
@Bean
WebClient defaultWebClient(WebClient.Builder webClientBuilder, HttpClientProps clientProps) {
    return webClientBuilder
            .clientConnector(ClientHttpConnectorBuilder.jdk()
                    .withHttpClientCustomizer(httpClientBuilder -> httpClientBuilder
                            .version(clientProps.httpVersion() == 1
                                    ? HttpClient.Version.HTTP_1_1
                                    : HttpClient.Version.HTTP_2)
                            .build())
                    .build(HttpClientSettings.defaults()
                            .withConnectTimeout(clientProps.connectTimeout())
                            .withReadTimeout(clientProps.readTimeout())))
            .build();
}
```

### Async Proxy Factory
A reactive-capable proxy factory is provided:

```java
@Primary
@Bean
HttpServiceProxyFactory asyncProxyFactory(WebClient webClient) {
    return HttpServiceProxyFactory.builder()
            .exchangeAdapter(WebClientAdapter.create(webClient))
            .build();
}
```

## Usage Example

### 1. Define an Interface

```java
@HttpExchange("/posts")
interface PostClient {
	
    @GetExchange("/{id}")
    Mono<Post> getPost(@PathVariable long id);
    
}
```

### 2. Register the Bean
```java
@Configuration(proxyBeanMethods = false)
class ClientConfig {
    
    @Bean
    PostHttpClient postHttpClient(HttpServiceProxyFactory proxyFactory) {
        return proxyFactory.createClient(PostClient.class);
    }

}
```

### 3. Use the Bean
```java
@Service
public class PostService {
	
    private final PostHttpClient postClient;
    
    PostService(PostHttpClient postClient) {
        this.postClient = postClient;
    }
    
    public Mono<Post> fetchPost(Long id) {
        return this.postClient.getPost(id);
    }
	
}
```
