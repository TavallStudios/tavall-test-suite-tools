# Tavall Test Suite Tools

This repository provides Tavall Studios' canonical executable architecture-test tooling as reusable, opt-in Java rules and a Gradle plugin. `tavall-docs` owns the written architecture policy; this repository turns reusable parts of that policy into tests that consumer repositories actually execute.

## Why Tavall Test Suite Tools

- Reuse the same architecture checks across repositories without copying rule source.
- Inspect real consumer production classes and source through the consumer's test-suite boundary.
- Select only the rule artifacts a repository needs.

## Features

- Canonical rule API and source/class analysis engine.
- Optional rule modules for naming/source structure, DI, registries, cache, databases, runtime support, web, and Tavall CLI.
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
        maven("https://maven.pkg.github.com/TavallStudios/tavall-test-suite-tools") {
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

tavall-test-suite-tools/\
├── modules/\
│   ├── [core](modules/core/README.md)\
│   ├── [patterns](modules/patterns/README.md)\
│   ├── [di](modules/di/README.md)\
│   ├── [registry](modules/registry/README.md)\
│   ├── [cache](modules/cache/README.md)\
│   ├── [database](modules/database/README.md)\
│   ├── [runtime](modules/runtime/README.md)\
│   ├── [web](modules/web/README.md)\
│   └── [cli](modules/cli/README.md)\
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

## Publishing and Tavall CI

Every module and the Gradle plugin publish as Gradle-compatible Maven artifacts for public distribution. GitHub Packages publication is a publication surface; it is not Tavall CI's internal source-resolution authority.

Tavall CI composes this repository at an exact source SHA. The Gradle plugin version and module artifact version are aligned through the `tavallArchitectureVersion` Gradle property. When another exact source build needs architecture modules, Tavall CI publishes only the declared modules to the operation's `TAVALL_CI_DEPENDENCY_REPOSITORY`, then resolves them from that job-scoped file repository. The path is per job and is not Maven Local or a durable shared package authority.

<details>
<summary>Documentation Update State</summary>

### Current Locations

| Surface | Sync State | Location | Last Updated | Evidence |
| --- | --- | --- | --- | --- |
| GitHub | `PRIMARY` | `TavallStudios/tavall-test-suite-tools/README.md` | 2026-09-27 12:59 PM PDT | [PR #16](https://github.com/TavallStudios/tavall-test-suite-tools/pull/16) |
| Notion | `NOT_APPLICABLE` | — | 2026-09-27 12:59 PM PDT | README routing surface; no 1:1 twin is assigned. |

### Update History

| Timestamp | Surface | Event | Location | Previous Location | Evidence | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-09-27 12:59 PM PDT | GitHub | `UPDATED` | `TavallStudios/tavall-test-suite-tools/README.md` | `TavallStudios/tavall-test-suite-tools/README.md` | [PR #16](https://github.com/TavallStudios/tavall-test-suite-tools/pull/16) | Added a public front door and current module map while removing internal Maven destination details. |
| 2026-10-07 UTC | GitHub | `UPDATED` | `TavallStudios/tavall-test-suite-tools/README.md` | Same path | Tavall-MC separation work; current main PR #23 | Added the merged CLI architecture rule module to the repository feature and module maps. |

</details>
