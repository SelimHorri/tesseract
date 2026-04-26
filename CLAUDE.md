# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Tesseract is a modular Spring Boot library that standardizes HTTP client configuration. It provides autoconfigured `RestClient` (synchronous) and `WebClient` (asynchronous) beans that consumers activate by choosing which dependency to include on their classpath.

**Java 25** | **Spring Boot 3.5.13** | **Maven** | Published to Maven Central under `io.github.selimhorri`

## Build Commands

The project has **no parent relationship** between the root modules — each must be built independently:

```bash
# Build core modules (tesseract-core, tesseract-sync, tesseract-async)
mvn -B package --file tesseract-parent/pom.xml

# Build individual starters
mvn -B package --file tesseract-starter-jps/pom.xml
mvn -B package --file tesseract-starter-zitadel/pom.xml

# Run tests for a specific module
mvn test -f tesseract-parent/pom.xml
mvn test -f tesseract-starter-jps/pom.xml

# Run a single test class
mvn test -f tesseract-parent/tesseract-core/pom.xml -Dtest=DefaultHttpClientPropsValidationTest

# Run a single test method
mvn test -f tesseract-parent/tesseract-core/pom.xml -Dtest=DefaultHttpClientPropsValidationTest#shouldFailWhenHttpVersionIsBelowMinimum
```

## Code Formatting

Spotless with Eclipse formatter is configured **only in tesseract-parent** and runs automatically during `compile` phase. Starters do not have Spotless configured.

```bash
# Apply formatting (core modules only)
mvn spotless:apply -f tesseract-parent/pom.xml

# Check without applying
mvn spotless:check -f tesseract-parent/pom.xml
```

Formatter config: `tesseract-parent/spotless-eclipse-formatter.xml`

## Architecture

### Three-Tier Conditional Autoconfiguration

```
tesseract-core          Always loaded. Defines HttpClientProps interface and
                        DefaultHttpClientProps record bound to `tesseract.main.client.*`

tesseract-sync          @AutoConfiguration creates primary RestClient + HttpServiceProxyFactory
  OR                    using JDK HttpClient. Activated by classpath presence.
tesseract-async         Same for WebClient (reactive).

Starters                Detect RestClient or WebClient via @ConditionalOnBean and create
(jps, zitadel)          system-specific client instances by mutating the primary client.
```

Starters have **both** `tesseract-sync` and `tesseract-async` as `<optional>true</optional>` dependencies. The consumer chooses sync or async by including one on their classpath; the starter's `@ConditionalOnBean(RestClient.class)` or `@ConditionalOnBean(WebClient.class)` activates the matching config.

### Key Patterns

- **Configuration properties** are Java records with `@ConfigurationProperties` and `@Validated`
- **Bean overrides**: All beans use `@ConditionalOnMissingBean` so consumers can replace them
- **Autoconfiguration discovery**: Each module registers its config classes in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- **Testing**: Uses `ApplicationContextRunner` with `AutoConfigurations.of(...)` to validate context loading and property binding without starting a full Spring context
- **Customizer pattern** (ZITADEL starter): `ZitadelRestClientCustomizer` / `ZitadelWebClientCustomizer` interfaces injected via `ObjectProvider<T>` for extensibility

### Property Prefix Convention

```yaml
tesseract:
  main:
    client:                    # Core HTTP settings (DefaultHttpClientProps)
      connect-timeout: 5s
      read-timeout: 5s
      http-version: 2          # Validated @Min(1) @Max(3)
      ssl-bundle-name: ""
  jps:
    base-url: https://jsonplaceholder.typicode.com
  zitadel:
    base-url: http://127.0.0.1:8080
    client-id: ...
    client-secret: ...
```

### Bean Naming

Primary/default beans: `defaultRestClient`, `defaultWebClient`, `syncProxyFactory`, `asyncProxyFactory`
System-specific: `jpsRestClient`, `jpsProxyFactory`, `zitadelRestClient`, `zitadelWebClient`

## Adding a New Starter

1. Create `tesseract-starter-<name>/` at root with its own POM (not a child of tesseract-parent)
2. Declare `tesseract-sync` and `tesseract-async` as `<optional>true</optional>` dependencies
3. Create a `*ClientProps` record with `@ConfigurationProperties(prefix = "tesseract.<name>")`
4. Create `SyncHttpClientsConfig` with `@ConditionalOnBean(RestClient.class)` at class level
5. Create `AsyncHttpClientsConfig` with `@ConditionalOnBean(WebClient.class)` at class level
6. Create `*EnablingConfig` with `@AutoConfiguration` and `@EnableConfigurationProperties`
7. Register all config classes in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
8. Add `ApplicationContextRunner`-based tests

## CI

GitHub Actions (`.github/workflows/maven.yml`): builds all three module groups sequentially on push to master/develop and PRs to master. Java 25 Temurin.
