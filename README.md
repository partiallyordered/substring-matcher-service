## Build
### With Nix
From the project root: `nix-shell` to enter the development shell, then from the development shell
`./gradlew build`

### Without Nix
1. Install JDK 1.8 or higher
2. `./gradlew build`

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
