# Tesseract Core (`tesseract-core`)

Tesseract Core is the foundational module for the Tesseract ecosystem. It provides the core abstractions, shared configurations, and property definitions used by both the synchronous and asynchronous HTTP client libraries.

> [!IMPORTANT]
> This module is **not intended for direct use** in applications. It is automatically integrated when you include either `tesseract-sync` or `tesseract-async`.

## Features

- **Property Definitions**: Defines `HttpClientProps` and `DefaultHttpClientProps` for consistent configuration across modules.
- **Validation**: Includes Spring Boot validation for HTTP client settings (e.g., timeouts, version constraints).
- **Auto-Enabling**: Automatically enables `@ConfigurationProperties` for a seamless integration.

## Configuration Properties

Tesseract Core binds properties from the `tesseract.main.client` prefix:

| Property | Default | Description |
|----------|---------|-------------|
| `tesseract.main.client.connect-timeout` | `5s` | Duration for connection establishment. |
| `tesseract.main.client.read-timeout` | `5s` | Duration to wait for data after connection. |
| `tesseract.main.client.http-version` | `2` | HTTP version to use (1 for HTTP/1.1, 2 for HTTP/2). |
| `tesseract.main.client.ssl-bundle-name` | `""` | Name of the SSL bundle for secure connections. |

## Core Infrastructure

### `HttpClientProps` Interface
This interface serves as the contract for HTTP client settings:
```java
public interface HttpClientProps {
    Duration connectTimeout();
    Duration readTimeout();
    int httpVersion();
    String sslBundleName();
}
```

### `DefaultHttpClientProps` Record
A validated `@ConfigurationProperties` implementation:
```java
@ConfigurationProperties(prefix = "tesseract.main.client")
@Validated
record DefaultHttpClientProps(
    @DefaultValue("5s") Duration connectTimeout,
    @DefaultValue("5s") Duration readTimeout,
    @DefaultValue("") String sslBundleName,
    @DefaultValue("2") @Min(1) @Max(3) int httpVersion
) implements HttpClientProps {}
```
