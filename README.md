# Tesseract

Tesseract is a modular ecosystem of Spring Boot libraries designed to simplify and standardize HTTP communication. By providing modern, autoconfigured HTTP clients, Tesseract allows developers to integrate with external REST services with minimal boilerplate and maximum consistency.

## Overview

At its core, Tesseract leverages Spring Boot's autoconfiguration to expose pre-configured `RestClient` (synchronous) and `WebClient` (asynchronous) beans. These beans are tailored with sensible defaults and are easily customizable through standard Spring properties.

## Architecture

The project is structured into a core set of modules (found in `tesseract-parent`) and a series of specialized starters for different external systems.

### Core Modules (`tesseract-parent`)

*   **`tesseract-core`**: The foundational library containing shared property definitions and common configurations.
    *   **Important:** This module is not intended for direct use in applications. It is internally used by `tesseract-sync` and `tesseract-async`.
*   **`tesseract-sync`**: Exposes a modern, autoconfigured **`RestClient`** bean for synchronous HTTP calls. It also sets up a `HttpServiceProxyFactory` for declarative REST interfaces.
*   **`tesseract-async`**: Exposes a modern, autoconfigured **`WebClient`** bean for non-blocking, asynchronous HTTP calls. It includes a reactive-compatible `HttpServiceProxyFactory`.

### Tesseract Starters

Tesseract facilitates the integration with specific third-party systems through dedicated starters. Each starter provides specialized autoconfiguration (base URLs, interceptors, default headers) for a particular system.

*   **`tesseract-starter-jps`**: A starter for the [JSONPlaceholder](https://jsonplaceholder.typicode.com/) (JPS) APIs.
*   **Future Starters**: If there is a community need to communicate with other services like **ZITADEL IDP**, Tesseract will provide a `tesseract-starter-zitadel`. This would expose a `RestClient` or `WebClient` bean specifically for ZITADEL, pre-configured with the necessary security interceptors and headers.

## Integration Guide

### Requirements
*   **Java 17** or higher (Java 21/25 recommended).
*   **Spring Boot 3.2** or higher.

### How to Integrate

To use a Tesseract starter in your application, you must include two types of dependencies:

1.  **The System Starter**: The specific library for the service you want to call.
2.  **The Communication Style**: Either the synchronous or asynchronous library.

This dual-dependency approach allows Tesseract to remain flexible: the starter detects whether you want to make calls synchronously or asynchronously based on which Tesseract library is present on your classpath.

#### Example: Integrating with JSONPlaceholder

If you want to use the JSONPlaceholder starter synchronously:

```xml
<dependencies>
    <!-- 1. The Starter for JPS -->
    <dependency>
        <groupId>io.github.selimhorri</groupId>
        <artifactId>tesseract-starter-jps</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- 2. The Communication Style (Synchronous) -->
    <dependency>
        <groupId>io.github.selimhorri</groupId>
        <artifactId>tesseract-sync</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

If you prefer asynchronous communication, replace `tesseract-sync` with `tesseract-async`. The `tesseract-starter-jps` will automatically switch to exposing a `WebClient` bean instead of a `RestClient` bean.

## Professional Configuration

Tesseract is designed for enterprise-grade applications. You can control connection settings globally via your application configuration:

```yaml
tesseract:
  web:
    http:
      client:
        connect-timeout: 5s
        read-timeout: 10s
        http-version: 2
  jps:
    base-url: https://jsonplaceholder.typicode.com
```

---
*Developed to provide a unified and robust HTTP communication layer for modern Spring Boot microservices.*
