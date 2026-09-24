# AGENTS.md

This file gives guidance to LLM agents (Claude, Kimi, etc.) working on code in this repository.

## Project Overview

Tesseract is a modular Spring Boot library that standardizes HTTP client configuration. It provides autoconfigured
`RestClient` (synchronous) and `WebClient` (reactive) beans. Consumers turn them on by choosing which dependency to put
on their classpath.

**Java 25** | **Spring Boot 3.5.14** | **Maven** | Version **1.0.3** | Published to Maven Central under
`io.github.selimhorri`

## Repository Layout

```
tesseract-parent/              Multi-module POM (packaging pom); also acts as a BOM for core/sync/async
  tesseract-core/              HttpClientProps + DefaultHttpClientProps, HttpClientEnablingConfig
  tesseract-sync/              io.github.selimhorri.tesseract.sync.HttpClientsConfig (RestClient)
  tesseract-async/             io.github.selimhorri.tesseract.async.HttpClientsConfig (WebClient)
tesseract-starter-jps/         Standalone project; imports tesseract-parent as a BOM
tesseract-starter-zitadel/     Standalone project; WIP, listed in .gitignore, disabled in CI
.github/workflows/maven.yml    CI
```

## Build Commands

The root modules have **no parent relationship**. Build each one independently:

```bash
# Build core modules (tesseract-core, tesseract-sync, tesseract-async)
mvn -B package --file tesseract-parent/pom.xml

# Install core modules locally (needed before building a starter against an unreleased version)
mvn -B install --file tesseract-parent/pom.xml

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

Spotless (Eclipse formatter) is configured in **every** Tesseract project: the core modules and both starters. It runs
`apply` automatically during the `compile` phase, so a build reformats sources.

```bash
mvn spotless:check   # check without applying
mvn spotless:apply   # apply formatting
```

Formatter config:

- Core modules: `tesseract-parent/spotless-eclipse-formatter.xml` (referenced via `${project.parent.basedir}`)
- Starters: `tesseract-starter-<name>/spotless-eclipse-formatter.xml`

Style: tabs for indentation, and a blank tab-indented line after class declarations and between members.

## Architecture

### Three-Tier Conditional Autoconfiguration

```
tesseract-core          Always loaded. Defines the HttpClientProps interface and the
                        package-private DefaultHttpClientProps record bound to `tesseract.main.client.*`

tesseract-sync          @AutoConfiguration creates the @Primary defaultRestClient + defaultSyncProxyFactory,
  OR                    backed by the JDK HttpClient (ClientHttpRequestFactoryBuilder.jdk()).
tesseract-async         Same for WebClient: @Primary defaultWebClient + defaultAsyncProxyFactory
                        (ClientHttpConnectorBuilder.jdk()).

Starters                Detect the default client by bean name and create system-specific
(jps, zitadel)          clients by calling mutate() on it and setting the system's base URL.
```

Starters declare **both** `tesseract-sync` and `tesseract-async` as `<optional>true</optional>` dependencies. The
consumer picks sync or async by adding one of them. The starter's config classes then activate like this:

```java

@ConditionalOnBean(name = "defaultRestClient")
@AutoConfiguration(afterName = "io.github.selimhorri.tesseract.sync.HttpClientsConfig")
class SyncHttpClientsConfig { ...
}

@ConditionalOnBean(name = "defaultWebClient")
@AutoConfiguration(afterName = "io.github.selimhorri.tesseract.async.HttpClientsConfig")
class AsyncHttpClientsConfig { ...
}
```

Use `afterName` (a string), not `after = Class`. The sync/async config classes are package-private and may be missing
from the classpath.

### Key Patterns

- **Configuration properties** are Java records with `@ConfigurationProperties`, `@Validated` and `@DefaultValue`.
- **Bean overrides**: every bean uses `@ConditionalOnMissingBean(name = "<beanName>")`, so consumers can replace it by
  name.
- **Qualifiers**: starter beans that consume a system-specific client inject it with
  `@Qualifier("<system>RestClient")` / `@Qualifier("<system>WebClient")`. Unqualified injection resolves to the
  `@Primary` default client.
- **Visibility**: config classes and default implementations are package-private. Only props records, customizer
  interfaces and client interfaces are public.
- **Autoconfiguration discovery**: each module lists its config classes in
  `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
- **Customizer pattern**: `ZitadelRestClientCustomizer` / `ZitadelWebClientCustomizer` are `@FunctionalInterface`s
  injected as `ObjectProvider<T>` and applied to the mutated builder. `JpsRestClientCustomizer` /
  `JpsWebClientCustomizer` exist but are **not yet wired** into the JPS configs.
- **Testing**: `ApplicationContextRunner` with `AutoConfigurations.of(...)` checks context loading, bean presence and
  property binding without starting a full application.

### Property Prefix Convention

```yaml
tesseract:
  main:
    client: # DefaultHttpClientProps
      connect-timeout: 5s
      read-timeout: 5s
      http-version: 2          # @Min(1) @Max(3); 1 -> HTTP/1.1, anything else -> HTTP/2
      ssl-bundle-name: ""      # bound, but not yet applied by sync/async configs
  jps: # JpsClientProps
    base-url: https://jsonplaceholder.typicode.com
  zitadel: # IdpClientProps
    base-url: http://127.0.0.1:8080
    client-id: ...             # @NotBlank
    client-secret: ...         # @NotBlank
    grant-type: client_credentials
    scope: openid
    whitelisted-paths: [ ]
```

### Bean Naming

| Module  | Sync                                                                       | Async                                                                       |
|---------|----------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| core    | `defaultRestClient`, `defaultSyncProxyFactory`                             | `defaultWebClient`, `defaultAsyncProxyFactory`                              |
| jps     | `jpsRestClient`, `jpsSyncProxyFactory`                                     | `jpsWebClient`, `jpsAsyncProxyFactory`                                      |
| zitadel | `zitadelRestClient`, `zitadelSyncProxyFactory`, `tokenRetrieverSyncClient` | `zitadelWebClient`, `zitadelAsyncProxyFactory`, `tokenRetrieverAsyncClient` |

Pattern: `<system>RestClient` / `<system>WebClient`, `<system>SyncProxyFactory` / `<system>AsyncProxyFactory`.

## Adding a New Starter

1. Create `tesseract-starter-<name>/` at the root with its own POM (not a child of `tesseract-parent`). Import
   `tesseract-parent` as a BOM in `<dependencyManagement>`, as `tesseract-starter-jps` does.
2. Declare `tesseract-sync` and `tesseract-async` as `<optional>true</optional>` dependencies, and copy the Spotless
   setup and `spotless-eclipse-formatter.xml`.
3. Create a public `<Name>ClientProps` record with `@ConfigurationProperties(prefix = "tesseract.<name>")` and
   `@Validated`.
4. Create a package-private `SyncHttpClientsConfig` annotated with `@ConditionalOnBean(name = "defaultRestClient")` and
   `@AutoConfiguration(afterName = "io.github.selimhorri.tesseract.sync.HttpClientsConfig")`. It exposes
   `<name>RestClient` and `<name>SyncProxyFactory`.
5. Create a package-private `AsyncHttpClientsConfig` annotated with `@ConditionalOnBean(name = "defaultWebClient")` and
   `@AutoConfiguration(afterName = "io.github.selimhorri.tesseract.async.HttpClientsConfig")`. It exposes
   `<name>WebClient` and `<name>AsyncProxyFactory`.
6. Optionally add `<Name>RestClientCustomizer` / `<Name>WebClientCustomizer` interfaces and apply them through
   `ObjectProvider`.
7. Create `<Name>EnablingConfig` with `@AutoConfiguration` and
   `@EnableConfigurationProperties(<Name>ClientProps.class)`.
8. Register all three config classes in
   `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
9. Add `ApplicationContextRunner`-based tests: beans absent without a default client, present with one, and props
   binding.
10. Add a build step to `.github/workflows/maven.yml`.

## CI

GitHub Actions (`.github/workflows/maven.yml`) runs on pushes to `master`/`develop` and on PRs to `master`, using
Temurin Java 25. It builds `tesseract-parent`, then `tesseract-starter-jps`. The ZITADEL build step is commented out.

## Release

Artifacts are signed with GPG (`maven-gpg-plugin`) and published through `central-publishing-maven-plugin`.
`gpg-setup.sh <key>` builds the installable projects and detach-signs the jars and POMs manually. Bump `<version>` and
`<tesseract.version>` together in `tesseract-parent/pom.xml`, its child POMs, and each starter POM.
