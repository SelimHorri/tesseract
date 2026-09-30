# Tesseract Sync (`tesseract-sync`)

Tesseract Sync is a specialized module for synchronous HTTP communication. It provides a pre-configured `RestClient` bean and an `HttpServiceProxyFactory` for easily creating synchronous HTTP interface clients.

## Integration

To use Tesseract Sync in your Maven project, add the following dependency:

```xml
<dependency>
    <groupId>io.github.selimhorri</groupId>
    <artifactId>tesseract-sync</artifactId>
    <version>2.0.0</version>
</dependency>
```

Use the latest version, currently `2.0.0` (Spring Boot 4). On Spring Boot 3.5, use the 1.x line instead. See
[Versions and Spring Boot compatibility](../../README.md#versions-and-spring-boot-compatibility).

## Features

- **Auto-Configured `RestClient`**: Automatically sets up a `@Primary` `RestClient` bean using settings from Tesseract Core.
- **Synchronous Proxy Support**: Configures an `HttpServiceProxyFactory` to facilitate the creation of synchronous HTTP interface-based clients.
- **JDK HttpClient Backend**: Utilizes the modern Java 11+ `java.net.http.HttpClient` for improved performance and configuration consistency.

## Exposed Beans

The following beans are automatically configured and available for injection:

1.  **`RestClient`**: A primary bean configured with custom timeouts and HTTP version.
2.  **`HttpServiceProxyFactory`**: Configured with a `RestClientAdapter` for creating synchronous HTTP interface proxies.

## Infrastructure and Configuration

The snippets below show the 1.x implementation (Spring Boot 3.5). These beans aren't guarded by
`@ConditionalOnMissingBean`. To customize `defaultRestClient`, declare a Spring Boot `RestClientCustomizer` bean. See
[Customizing the clients](../../README.md#customizing-the-clients).

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
                    .build(ClientHttpRequestFactorySettings.defaults()
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
HttpServiceProxyFactory defaultSyncProxyFactory(RestClient restClient) {
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
