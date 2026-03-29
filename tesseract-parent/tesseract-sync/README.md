# Tesseract Sync (`tesseract-sync`)

Tesseract Sync is a specialized module for synchronous HTTP communication. It provides a pre-configured `RestClient` bean and an `HttpServiceProxyFactory` for easily creating synchronous HTTP interface clients.

## Integration

To use Tesseract Sync in your Maven project, add the following dependency:

```xml
<dependency>
    <groupId>io.github.selimhorri</groupId>
    <artifactId>tesseract-sync</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Features

- **Auto-Configured `RestClient`**: Automatically sets up a `@Primary` `RestClient` bean using settings from Tesseract Core.
- **Synchronous Proxy Support**: Configures an `HttpServiceProxyFactory` to facilitate the creation of synchronous HTTP interface-based clients.
- **JDK HttpClient Backend**: Utilizes the modern Java 11+ `java.net.http.HttpClient` for improved performance and configuration consistency.

## Exposed Beans

The following beans are automatically configured and available for injection:

1.  **`RestClient`**: A primary bean configured with custom timeouts, HTTP version, and SSL settings.
2.  **`HttpServiceProxyFactory`**: Configured with a `RestClientAdapter` for creating synchronous HTTP interface proxies.

## Infrastructure and Configuration

### `RestClient` Configuration
The library configures the `RestClient` using the standard Spring Boot `RestClient.Builder` and the JDK-based request factory:

```java
@Primary
@Bean
RestClient defaultRestClient(RestClient.Builder restClientBuilder, HttpClientProps clientProps) {
    return restClientBuilder
            .requestFactory(ClientHttpRequestFactoryBuilder.jdk()
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

### Sync Proxy Factory
A proxy factory is provided to enable declarative HTTP clients:

```java
@Primary
@Bean
HttpServiceProxyFactory syncProxyFactory(RestClient restClient) {
    return HttpServiceProxyFactory.builder()
            .exchangeAdapter(RestClientAdapter.create(restClient))
            .build();
}
```

## Usage Example

### 1. Define an Interface

```java
@HttpExchange("/posts")
interface PostHttpClient {
	
    @GetExchange("/{id}")
    Post findById(@PathVariable long id);

}
```

### 2. Register the Bean
```java
@Configuration(proxyBeanMethods = false)
class DeclarativeHttpClientsConfig {
	
    @Bean
    PostHttpClient postHttpClient(HttpServiceProxyFactory proxyFactory) {
        return proxyFactory.createClient(PostHttpClient.class);
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
    
    public Post fetchPost(long id) {
        return this.postClient.findById(id);
    }
	
}
```
