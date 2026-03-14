# Tesseract Parent

Tesseract is a modular Spring Boot library designed to streamline the configuration and creation of HTTP clients. It provides a consistent and unified approach to managing HTTP communication settings such as timeouts, HTTP versions, and SSL configurations across both synchronous and asynchronous clients.

## Sub-modules

The Tesseract project is divided into specialized modules to provide flexibility and maintainability:

### 1. Tesseract Core (`tesseract-core`)
- **Purpose**: Acts as the foundational module containing shared configurations and property definitions.
- **Key Responsibilities**:
    - Defines the `HttpClientProps` interface, which serves as a contract for HTTP client settings (connection timeout, read timeout, HTTP version, and SSL bundle).
    - Provides `DefaultHttpClientProps`, a Spring `@ConfigurationProperties` record that binds to the `tesseract.web.http.client` prefix.
    - Includes `HttpClientEnablingConfig`, which automatically enables the configuration properties for use in other modules.

### 2. Tesseract Sync (`tesseract-sync`)
- **Purpose**: Offers pre-configured synchronous HTTP client capabilities.
- **Key Responsibilities**:
    - Automatically configures a `@Primary` `RestClient` bean based on the settings defined in `Tesseract Core`.
    - Sets up a `HttpServiceProxyFactory` using a `RestClientAdapter`. This allows developers to easily create synchronous HTTP interface clients (proxies) by simply defining an interface.
    - Utilizes the modern JDK-based `HttpClient` as the underlying request factory.

### 3. Tesseract Async (`tesseract-async`)
- **Purpose**: Provides pre-configured asynchronous (reactive) HTTP client capabilities.
- **Key Responsibilities**:
    - Automatically configures a `@Primary` `WebClient` bean using the common properties from `Tesseract Core`.
    - Sets up a `HttpServiceProxyFactory` using a `WebClientAdapter`, enabling the creation of reactive HTTP interface clients.
    - Leverages the JDK-based `HttpClient` within the reactive `ClientHttpConnector` to ensure consistency with the synchronous module.

## Configuration Properties

Tesseract can be easily customized via your application's configuration files (`application.yaml` or `application.properties`):

```yaml
tesseract:
  web:
    http:
      client:
        connect-timeout: 5s   # Duration for a connection establishment
        read-timeout: 5s      # Duration to wait for data after connection
        http-version: 2       # Desired HTTP version (e.g., 1 for HTTP/1.1, 2 for HTTP/2)
        ssl-bundle-name: ""   # Optional name of an SSL bundle for secure connections
```

## Getting Started

To use Tesseract in your Spring Boot application, add the dependency for the desired communication style to your `pom.xml`. Once the dependency is included, the corresponding `RestClient` or `WebClient` will be automatically configured and available for injection.
