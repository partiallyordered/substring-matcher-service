## Run

### Without Nix
1. Install Java >= 17
```sh
./gradlew bootRun -q
```
2. Optionally, with pretty-printed logs
    ###### With lnav
    Install lnav, then
    ```sh
    ./gradlew bootRun -q | lnav -Ilnav
    ```
    ###### With jq
    Install jq, then
    ```sh
    ./gradlew bootRun -q | jq
    ```

### With Nix
```sh
nix-shell --command './gradlew bootRun -q | lnav -Ilnav'
```

### Sample requests
```sh
./sample.sh
```

## Test
Sadly, there are no automated tests. There was something about Spring's reflection that I couldn't
work out. The service has been (pretty poorly) manually tested.

## Build
### Without Nix
1. Install JDK 1.15 or higher
2. `./gradlew build`

### With Nix
```sh
nix-shell --command './gradlew build'
```

## Design Notes
### Implementation
#### Server Framework
Spring Boot, for the sake of learning. Will follow this guide (at least, to begin with):
https://spring.io/guides/gs/rest-service/

##### Prerequisites
The guide specifies the following prerequisites:
- JDK 1.8 or later
- Gradle 4+ or Maven 3.2+

###### JDK
It seems OpenJDK is the de facto default, and others should be interoperable, so went with OpenJDK
and didn't spend much time on this. OpenJDK is GPLv2 licensed with a linking exception. I don't
care much about the license of this code, but the Spring Boot example REST service that I'll work
from, the _Jackson_ JSON library, and Spring Boot itself are all licensed APLv2- so I'll use that.
This is generally compatible with other non-copyleft OSS licenses.

[This](https://news.ycombinator.com/item?id=28820601) discussion and the site it's discussing may
help if the decision needs to be better evaluated.

###### Gradle or Maven
A few minutes reading of [this](https://gradle.org/maven-vs-gradle/) definitely totally unbiased
site suggests I'd probably prefer Gradle for increased power and flexibility, as well as faster
build times.

#### Build
Use `nix` to control some dependencies and create a development shell. I'll also use this to
perform the build in CI. This _could_ be extended later to control the implementation dependencies,
but probably won't be. Using _nix_ means my development environment can be reproduced in an
extremely tightly controlled fashion on another developer's machine, or in CI. This hugely narrows
the search space for bugs and makes bug reproduction much easier. It can also make developer
onboarding much quicker.

#### Logging
Based on https://medium.com/@zachcorbettmcelrath/structured-logging-in-spring-boot-with-log4j2-part-1-context-enriched-logs-57a74e92339c

Each request has an operation ID attached to it. Any logs written during the handling of the
request have this operation ID attached, so can be collated and filtered. In a distributed system,
this ID would be passed with any propagated requests enabling collation and correlation of events
through the system.

## TODO
- CI
  - Protect main branch such that it's not possible to push directly to it
  - Condition merges on passing tests
  - Packaging and releases
    - A container image, probably to Dockerhub, for convenience
    - A .jar in GH releases
- Reproducibility: control dependencies
  - code dependencies, i.e. those in Gradle
  - development dependencies, e.g. Gradle itself
  - production dependencies
- Data validation
 
    It's possible at present to create a dictionary with no supplied properties:

    `POST /dictionary { }`

    This will in fact generate a dictionary with a valid UUID, empty list of entries, and
    is_case_sensitive value of false. This is because `new Dictionary()` (the constructor Jackson
    uses) produces such a value, and Jackson then populates it. We probably don't want this. Is
    there some way to follow the _make invalid states unrepresentable_ rule? Do we need to write
    our own Jackson deserializer for `Dictionary` to prevent construction of an invalid
    `Dictionary`? Jackson might also have some configuration to use setters, in which case we could
    validate there.

    As a fallback we could write manual validation on each endpoint, or write validation on
    `Dictionary`, 

    Similar problems exist with the annotations endpoint.
- Check the SQLite maximum JSON/string field length; if we're storing dictionary entries as a
    string/json-array
- 400 errors are terrible for the user. E.g.
  ```sh
  $ curl 'localhost:8080/dictionary?id=1234'
  {"timestamp":"2022-06-21T14:11:13.293+00:00","status":400,"error":"Bad Request","path":"/dictionary"}
  ```
- Documentation
  - API spec / client library - can this be generated from the code?
  - Is there "javadoc" or similar, with docs in comments? Judging by the documentation I've seen,
      presumably this is how much API documentation is generated already
- Read about aspect-oriented programming
- Compile with warnings- do something about them?
- Autoformat/lint
- Test
  - System:
    - Smoke: just build and run the service, check it 
    - Probably just one "golden path" test:
      - Create a dictionary, run an annotation
  - Unit:
    - Test each controller route
    - Test a combination of routes, e.g.
      - create + update + delete
      - create + update + annotate
    - Probably don't bother testing the DAO- let that be tested by the controller tests
- Logging
  - See the section _How to avoid leaky context_ [here](https://medium.com/@zachcorbettmcelrath/structured-logging-in-spring-boot-with-log4j2-part-1-context-enriched-logs-57a74e92339c).
  - Exceptions seem able to escape our log handler intercepter and be printed without an operation id
  - Inject operation ID for system events so these can be more easily navigated/filtered
    - search "log4j random operationId"?
    - or just a default field that can be overridden?
  - Give the user some way of displaying log-lines in a way that's pleasant without lnav

## Notes
- It's a joy writing in an AOT compiled, strongly typed language with a passable type system.
- It's a joy using a mostly-declarative serializing/deserializing lib that works with the types of
    your language.
### Spring
  Caveat of these notes: without much experience with Java or Spring, it's tricky to have a
  valuable opinion, so this is heavily biased by my previous experience, especially comparisons
  with other languages/frameworks.

  So far I think Spring has too much magic (reflection, especially) and I don't really see the
  benefit of it. This might be because I'm a grumpy old man, or because I haven't spent enough
  time with it. But in general I've found I prefer to manage my database initialisation and
  connection pool myself, plug my handlers into my router myself, manage any shared data myself.
  It's opinionated, which is only good when they're opinions I agree with (joke..). I feel
  railroaded for little benefit. It also seems to have a large learning curve, and again, I don't
  really see the benefit.
  - This has hit me directly when trying to figure out why the tests won't work. I have something
      like
      ```
      java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:132
          Caused by: org.springframework.beans.factory.BeanDefinitionStoreException at ConfigurationClassParser.java:189
              Caused by: java.io.FileNotFoundException at ServletContextResource.java:159
      ```
      I _think_ this is caused by some problem with Spring trying to configure my data source in
      the tests, perhaps because when loading the application .
      But because the documentation, source code, and system is _huge_ I have no hope of figuring
      this out in a reasonable time-frame. So, no tests.

  Perhaps in the same way that a programming/natural language is shared, Spring is shared. In the
  sense of a language but also culture, patterns, etc. In other words, I _should_ be able to come
  to a new Spring codebase and understand it quickly. However, I have the feeling so far that
  reduced implementation complexity, and sticking closer to pure Java would be preferable. Having
  not tried writing a backend service in a framework with less reflection myself, it's difficult to
  have an opinion.
  - Counter to this, some of what I've read online suggests Spring may be a little like C++, or
      to a lesser extent Javascript or Haskell: everybody uses a different subset and the
      purported benefits of a shared language/framework etc. don't really pan out as well in
      practice as in theory.

  As Spring has a large DI/IoC component, perhaps testing is where it really shines, and that's
  where, having not been able to work out the tests, I missed the benefit of it. My experience
  elsewhere has been that automatic DI is something of an antipattern that is sometimes retrofitted
  because of code that hasn't been written to be testable.
