# Tesseract

Tesseract is a set of Spring Boot libraries that set up HTTP clients for you. Add one dependency to get a preconfigured
`RestClient` (synchronous) or `WebClient` (reactive) bean. Add a starter to get a client already set up for a specific
external system.

**Java 25** · **Spring Boot 4.x** (Tesseract 2.x) or **3.5.x** (Tesseract 1.x) · **Maven Central:**
`io.github.selimhorri` · **License:** MIT

## Versions and Spring Boot compatibility

**Use the latest Tesseract version, currently `2.0.0`.** Pick the 1.x line only if your application still runs on
Spring Boot 3.5.

| Tesseract                 | Spring Boot | When to use it                                        |
|---------------------------|-------------|-------------------------------------------------------|
| **2.x** (latest: `2.0.0`) | 4.x         | **Default.** New projects and anything on Boot 4.     |
| 1.x (latest: `1.1.0`)     | 3.5.x       | Applications that haven't moved to Spring Boot 4 yet. |

Use the same Tesseract version for every Tesseract artifact in a project (the `tesseract-parent` BOM and every
starter). A 1.x starter won't work with 2.x core modules, and a 2.x starter won't work with 1.x core modules. The
examples below use `2.0.0`. On Spring Boot 3.5, replace it with `1.1.0`.

## Modules

| Module          | Artifact                    | Purpose                                                                                                                           |
|-----------------|-----------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Core            | `tesseract-core`            | Shared `HttpClientProps` contract, bound to `tesseract.main.client.*`. Pulled in transitively; not meant to be declared directly. |
| Sync            | `tesseract-sync`            | Autoconfigures a primary `RestClient` (`defaultRestClient`) and an `HttpServiceProxyFactory` (`defaultSyncProxyFactory`).         |
| Async           | `tesseract-async`           | Autoconfigures a primary `WebClient` (`defaultWebClient`) and an `HttpServiceProxyFactory` (`defaultAsyncProxyFactory`).          |
| JPS starter     | `tesseract-starter-jps`     | Clients for the [JSONPlaceholder](https://jsonplaceholder.typicode.com/) API.                                                     |
| ZITADEL starter | `tesseract-starter-zitadel` | *Work in progress, not published yet.* Clients and a token retriever for the [ZITADEL](https://zitadel.com/) identity provider.   |

`tesseract-core`, `tesseract-sync` and `tesseract-async` live under `tesseract-parent/`. Each starter is a separate
Maven project at the repository root.

## How it works

```
tesseract-core     ──►  HttpClientProps (tesseract.main.client.*)
       │
       ├── tesseract-sync   ──►  defaultRestClient + defaultSyncProxyFactory
       └── tesseract-async  ──►  defaultWebClient  + defaultAsyncProxyFactory
                                        │
starters (jps, zitadel)  ──►  mutate() the default client, set the system's base URL,
                               and expose system-specific clients + proxy factories
```

The default clients use the JDK `HttpClient`. Their connect timeout, read timeout and HTTP version come from
`tesseract.main.client.*`.

Starters declare both `tesseract-sync` and `tesseract-async` as **optional** dependencies. You pick the style by adding
one of them yourself:

- `tesseract-sync` on the classpath gives you the starter's `RestClient` beans.
- `tesseract-async` on the classpath gives you the starter's `WebClient` beans.

See [Customizing the clients](#customizing-the-clients) to change what these beans do.

## Getting started

### 1. Import the `tesseract-parent` BOM

Before you use `tesseract-sync` or `tesseract-async`, import `tesseract-parent` as a BOM in the `<dependencyManagement>`
of your **parent POM**. The BOM manages the versions of `tesseract-core`, `tesseract-sync` and `tesseract-async`, so
submodules declare them without a `<version>`.

In a multi-module project, import the BOM once in the parent (aggregator) POM, then add `tesseract-sync` or
`tesseract-async` in the submodule that makes the HTTP calls. For example:

```
my-app/
├── pom.xml              ← imports the tesseract-parent BOM
└── my-module/
    └── pom.xml          ← depends on tesseract-sync (no version)
```

`my-app/pom.xml` (parent):

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.selimhorri</groupId>
            <artifactId>tesseract-parent</artifactId>
            <version>2.0.0</version> <!-- 1.x on Spring Boot 3.5 -->
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

`my-app/my-module/pom.xml` (submodule):

```xml

<dependencies>
    <dependency>
        <groupId>io.github.selimhorri</groupId>
        <artifactId>tesseract-sync</artifactId> <!-- or tesseract-async -->
    </dependency>
</dependencies>
```

In a single-module project, put both the BOM import and the dependency in the same POM.

### 2. Plain HTTP client

With `tesseract-sync` or `tesseract-async` on the classpath, you get the default client with no extra setup:

```java

@Service
class GreetingService {
	
	private final RestClient restClient;
	
	GreetingService(RestClient restClient) { // injects the @Primary defaultRestClient
		this.restClient = restClient;
	}

}
```

### 3. Using a starter (JSONPlaceholder example)

With the BOM imported in the parent POM (step 1), add the starter **and** the communication style you want to the
submodule. The BOM doesn't manage starters, so the starter needs an explicit `<version>`:

```xml

<dependencies>
    <!-- The system starter -->
    <dependency>
        <groupId>io.github.selimhorri</groupId>
        <artifactId>tesseract-starter-jps</artifactId>
        <version>2.0.0</version> <!-- same version as the BOM; 1.x on Spring Boot 3.5 -->
    </dependency>

    <!-- The communication style: tesseract-sync (RestClient) or tesseract-async (WebClient); version comes from the BOM -->
    <dependency>
        <groupId>io.github.selimhorri</groupId>
        <artifactId>tesseract-sync</artifactId>
    </dependency>
</dependencies>
```

Then use the system-specific proxy factory to build a declarative HTTP interface:

```java

@HttpExchange("/posts")
interface PostClient {
	
	@GetExchange("/{id}")
	Post findById(@PathVariable int id);

}

@Configuration
class JpsClientsConfig {
	
	@Bean
	PostClient postClient(@Qualifier("jpsSyncProxyFactory") HttpServiceProxyFactory factory) {
		return factory.createClient(PostClient.class);
	}

}
```

With `tesseract-async`, inject `jpsWebClient` or `jpsAsyncProxyFactory` instead, and return `Mono`/`Flux` from your
interface.

## Customizing the clients

> **Breaking changes.** Since `1.0.4` (and in every 2.x version), the default clients are no longer guarded by
> `@ConditionalOnMissingBean`. Since `1.1.0`, the JPS starter's clients aren't guarded by it either. You can't replace
> those beans by declaring a bean with the same name any more. Spring Boot rejects the duplicate at startup with a
> `BeanDefinitionOverrideException`. Use the customizers below instead.

### Default clients (`tesseract-sync` / `tesseract-async`)

`defaultRestClient` and `defaultWebClient` are built from Spring Boot's auto-configured `RestClient.Builder` /
`WebClient.Builder`. To add headers, interceptors, filters or message converters, declare a Spring Boot
`RestClientCustomizer` or `WebClientCustomizer` bean:

```java

@Bean
RestClientCustomizer correlationIdCustomizer() {
	return builder -> builder.requestInterceptor(new CorrelationIdInterceptor());
}
```

Tesseract sets the request factory (sync) or connector (async) itself, using `tesseract.main.client.*`. A customizer
that sets its own request factory or connector gets overwritten.

To take over completely, exclude the autoconfiguration and declare your own client. Name it `defaultRestClient` /
`defaultWebClient` so starters still pick it up:

```properties
spring.autoconfigure.exclude=io.github.selimhorri.tesseract.sync.HttpClientsAutoConfig
# or: io.github.selimhorri.tesseract.async.HttpClientsAutoConfig
```

In `2.0.0` these classes are still named `io.github.selimhorri.tesseract.sync.HttpClientsConfig` /
`io.github.selimhorri.tesseract.async.HttpClientsConfig`.

### Starter clients

Each starter builds its client by calling `mutate()` on the default client and setting the system's base URL. It then
applies every customizer bean of the starter's type, in order:

| Starter | Sync customizer               | Async customizer             | Beans replaceable by name?                |
|---------|-------------------------------|------------------------------|-------------------------------------------|
| JPS     | `JpsRestClientCustomizer`     | `JpsWebClientCustomizer`     | No, since `1.1.0` (yes in `2.0.0`)        |
| ZITADEL | `ZitadelRestClientCustomizer` | `ZitadelWebClientCustomizer` | Yes (`@ConditionalOnMissingBean` by name) |

```java

@Bean
JpsRestClientCustomizer jpsApiKeyCustomizer() {
	return builder -> builder.defaultHeader("X-Api-Key", "...");
}
```

A starter customizer only affects that starter's client. The default client stays as it is.

> In `2.0.0`, `JpsRestClientCustomizer` / `JpsWebClientCustomizer` exist but aren't applied yet.

## Configuration

```yaml
tesseract:
  main:
    client:
      connect-timeout: 5s        # default 5s
      read-timeout: 5s           # default 5s
      http-version: 2            # 1 = HTTP/1.1, 2 = HTTP/2 (default 2, validated between 1 and 3)
      ssl-bundle-name: ""        # default empty
  jps:
    base-url: https://jsonplaceholder.typicode.com   # default
  zitadel:
    base-url: http://127.0.0.1:8080   # default
    client-id: <required>
    client-secret: <required>
    grant-type: client_credentials    # default
    scope: openid                     # default
    whitelisted-paths: [ ]             # default empty
```

## Beans reference

| Module  | Sync (`tesseract-sync`)                                                    | Async (`tesseract-async`)                                                   |
|---------|----------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| Core    | `defaultRestClient` (`@Primary`), `defaultSyncProxyFactory` (`@Primary`)   | `defaultWebClient` (`@Primary`), `defaultAsyncProxyFactory` (`@Primary`)    |
| JPS     | `jpsRestClient`, `jpsSyncProxyFactory`                                     | `jpsWebClient`, `jpsAsyncProxyFactory`                                      |
| ZITADEL | `zitadelRestClient`, `zitadelSyncProxyFactory`, `tokenRetrieverSyncClient` | `zitadelWebClient`, `zitadelAsyncProxyFactory`, `tokenRetrieverAsyncClient` |

To change these beans, see [Customizing the clients](#customizing-the-clients).

## Building from source

The modules have no shared root POM. Build each group separately:

```bash
mvn -B package --file tesseract-parent/pom.xml        # core, sync, async
mvn -B package --file tesseract-starter-jps/pom.xml
mvn -B package --file tesseract-starter-zitadel/pom.xml
```

Build `tesseract-parent` and install it locally before building a starter against an unreleased version. Without the
release GPG key, skip signing:

```bash
mvn -B install -Dgpg.skip --file tesseract-parent/pom.xml
```

Spotless formats sources automatically during `compile`.

## License

MIT. See the license declared in the project POMs.