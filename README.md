# Java HTTP Server from Scratch

A minimal HTTP/1.1 server written in plain Java on top of `ServerSocket`, with a front controller, a servlet-style filter chain and HTTP Basic authentication. It uses no web framework and no external dependencies.

**Result: all four manual test cases below return the expected status codes (200, 401, 405, 404).**

## Features

- Thread-pool request handling (fixed pool of 10 threads)
- Front controller that maps paths to controllers
- Filter chain (`LoggingFilter`, then `BasicAuthFilter`)
- HTTP Basic authentication; credentials read from environment variables
- Per-request `SecurityContext` stored in a `ThreadLocal`
- Buffered responses with `Content-Length` and `Connection: close`

## Project structure

```
src/main/java/com/alpvatansever/httpserver/
  Main.java                  # wires filters, registers the /hello route, starts the server
  filter/                    # LoggingFilter, BasicAuthFilter
  lib/
    Server.java              # socket accept loop and thread pool
    FrontController.java     # filter chain execution and routing
    BaseController.java      # doGet / doPost / doPut / doDelete (405 by default)
    filter/                  # Filter, FilterChain
    http/                    # HttpRequest, HttpResponse, HttpMethod
    security/                # Authentication, SecurityContext, SecurityUtil (Base64)
```

## Request flow

1. `Server` accepts a connection and hands it to the thread pool.
2. `HttpRequest.parse` reads the request line and headers.
3. `FrontController` runs the filter chain; `BasicAuthFilter` answers 401 if the credentials are missing or wrong.
4. The matching controller handles the request (404 if no route, 405 if the method is not implemented).
5. `HttpResponse.flush` writes the status line, headers and body.

## Configuration

| Variable | Default | Purpose |
|---|---|---|
| `AUTH_USER` | `admin` | Basic auth user name |
| `AUTH_PASS` | `changeme` | Basic auth password (change it) |

The server listens on port 8080 (set in `Main.java`).

## Test results

Run with `AUTH_PASS=demo-pass`, using `curl`:

| Request | Result |
|---|---|
| `GET /hello` with correct credentials | `200 OK`, body `Hello, authenticated user!` |
| `GET /hello` with wrong password | `401 Unauthorized` |
| `POST /hello` with correct credentials | `405 Method Not Allowed` |
| `GET /nope` with correct credentials | `404 Not Found` |

## What I learned

- The status line and headers must be written before the body. My first version wrote the body into the socket writer before `flush()` sent the headers, and `curl` received no valid response at all. Buffering the body in `HttpResponse` fixed it.
- 404, 405 and 500 responses never called `flush()`, so I made `flush()` idempotent and let `Server` call it once per request.
- Filter order matters: logging runs before authentication, so rejected requests are still logged.
- Next steps: add unit tests (the project currently has none), read the request body, and keep connections alive.

## Known limitations

- `LoggingFilter` logs the status code when the filter chain returns, which happens before the controller runs. For 404 and 405 responses it therefore logs `200 OK`.
- The request body is not read, so POST/PUT payloads are ignored.
- One request per connection; there is no keep-alive.
- No automated tests.

## How to run

Requires JDK 21 or newer. Gradle is downloaded by the included wrapper.

```bash
AUTH_PASS=demo-pass ./gradlew run
```

In another terminal:

```bash
curl -i -u admin:demo-pass http://localhost:8080/hello
```

## Tech stack

Java 21, Gradle
