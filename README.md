# Tavall Architecture Tests

Reusable, opt-in Java architecture rules and a Gradle plugin for checking Tavall architecture boundaries in consumer builds.

## Why Tavall Architecture Tests

- Reuse the same architecture checks across repositories without copying rule source.
- Inspect real consumer production classes and source through the consumer's test-suite boundary.
- Select only the rule artifacts a repository needs.

## Features

- Canonical rule API and source/class analysis engine.
- Optional rule modules for naming/source structure, DI, registries, cache, databases, runtime support, and web.
- Gradle plugin `org.tavall.architecture-tests` for consumer task configuration.
- Migration-debt support that tolerates known findings temporarily while failing on new or obsolete entries.

## Quick Start

Add the plugin and selected rule modules to a repository testing suite. The [Consumer contract](#consumer-contract) below shows package resolution and the required Gradle wiring.

## Consumer Contract

Do not copy canonical test source into consumers. A repository's canonical testing suite (`*-test-suite`, or the equivalent root verification suite for a single-module repository) consumes this repository's published Gradle plugin/modules and feeds the real production modules it owns through that boundary.

The testing suite is the repository-level architecture verification boundary. Production subprojects do not each need to apply and execute a duplicate architecture gate.

Resolve the plugin marker from GitHub Packages in `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        maven("https://maven.pkg.github.com/TavallStudios/Tavall-Architecture-Tests") {
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: "github"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        gradlePluginPortal()
    }
}
```

Enable canonical architecture execution in the repository testing suite and declare the production projects it owns:

```kotlin
plugins {
    id("org.tavall.architecture-tests") version "<version>"
}

architectureTests {
    modules.set(listOf("core", "patterns", "di"))
    targetProjects.set(listOf(":backend-api", ":runtime", ":discord"))
}

rootProject.tasks.named("check") {
    dependsOn(tasks.named("check"))
}
```

The plugin registers `architectureTest`, runs it on JUnit Platform, compiles every configured target, and points the canonical engine at the target modules' compiled production classes, Java source roots, and runtime classpaths. If `targetProjects` is empty, the plugin preserves the single-project fallback.

Selecting a rule module changes executable verification. `core` is always included; other rule modules add rules through the `ArchitectureRule` service-provider contract. The consumer root `check` must depend on the repository testing suite.

## Project Structure

Tavall-Architecture-Tests/  
├── modules/  
│   ├── [core](modules/core/README.md)  
│   ├── [patterns](modules/patterns/README.md)  
│   ├── [di](modules/di/README.md)  
│   ├── [registry](modules/registry/README.md)  
│   ├── [cache](modules/cache/README.md)  
│   ├── [database](modules/database/README.md)  
│   ├── [runtime](modules/runtime/README.md)  
│   └── [web](modules/web/README.md)  
└── [gradle-plugin](gradle-plugin/README.md)
## Documentation

- [Contribution guide](CONTRIBUTING.md).
- [Architecture Tests progression evidence](docs/progression/ARCHITECTURE_TESTS_PROGRESSION.md) — audited implementation and validation record.
- [Tavall Docs Git Workflow](https://github.com/TavallStudios/tavall-docs/blob/main/docs/quality/GIT_WORKFLOW.md) — shared contribution and review policy.

## Requirements / Compatibility

- JDK 25 and Gradle Wrapper versions are set by this repository's build.
- Consumer builds need GitHub Packages credentials to resolve the artifacts.
- Select only rule artifacts relevant to the consumer's dependencies.

## Building From Source

Run `./gradlew check` with the repository's configured Java toolchain.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) and the shared review policy linked above.

## License

No license file is currently tracked in this repository. Contact the maintainers before redistributing or reusing this code.

## Publishing

Every current rule module and the Gradle plugin publish Gradle-compatible Maven artifacts to this repository's GitHub Packages feed. Supply `GITHUB_TOKEN` and `GITHUB_ACTOR` when resolving or publishing artifacts.

## Migration Debt

Consumers may point the plugin at a temporary debt file:

```kotlin
architectureTests {
    debtFile.set(layout.projectDirectory.file("config/architecture-debt.txt"))
}
```

Each non-comment line is `<rule-id>|<subject>`. Matching current findings are temporarily tolerated. New violations and entries that no longer match a real finding fail.

## Validation Boundary

For this repository, root `check` depends on each module/plugin `check`. For consumers, the test-suite `check` depends on `architectureTest`, and root `check` must depend on that suite. Product/runtime simulations remain owned by the consumer repository.

<details>
<summary>Documentation Update State</summary>

### Current Locations

| Surface | Sync State | Location | Last Updated | Evidence |
| --- | --- | --- | --- | --- |
| GitHub | `PRIMARY` | `TavallStudios/Tavall-Architecture-Tests/README.md` | 2026-09-27 12:51 PM PDT | __PR_URL__ |
| Notion | `NOT_APPLICABLE` | — | 2026-09-27 12:51 PM PDT | README routing surface; no 1:1 twin is assigned. |

### Update History

| Timestamp | Surface | Event | Location | Previous Location | Evidence | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-09-27 12:51 PM PDT | GitHub | `UPDATED` | `TavallStudios/Tavall-Architecture-Tests/README.md` | `TavallStudios/Tavall-Architecture-Tests/README.md` | __PR_URL__ | Added a public front door and current module map while removing internal Maven destination details. |

</details>
