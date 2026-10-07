# gradle-plugin

Owns Gradle plugin `org.tavall.architecture-tests` and consumer task/configuration wiring.

## Responsibility

### Owns
- Plugin extension and Gradle task registration/configuration.
- Connecting selected rule artifacts to a consumer's real production targets.
- `architectureTest` as the repository/root completion gate.
- `architectureAnalyze` for the same per-class assessment engine during authoring.
- `generateTavallTestScaffold` as a fail-closed direct-test starting point.

### Does Not Own
- Architecture rule implementation or consumer root-check policy.

## Repository Structure

tavall-test-suite-tools/\
├── modules/\
│   ├── [`core`](../modules/core/README.md)\
│   ├── [`patterns`](../modules/patterns/README.md)\
│   ├── [`testing`](../modules/testing/README.md)\
│   ├── [`di`](../modules/di/README.md)\
│   ├── [`registry`](../modules/registry/README.md)\
│   ├── [`cache`](../modules/cache/README.md)\
│   ├── [`database`](../modules/database/README.md)\
│   ├── [`runtime`](../modules/runtime/README.md)\
│   └── [`web`](../modules/web/README.md)\
└── **[`gradle-plugin`](README.md) ← This Module**\
## Relationships

| Module / System | Relationship |
| --- | --- |
| [`modules/core`](../modules/core/README.md) | Uses the shared rule contract and execution engine. |
| [`modules/runtime`](../modules/runtime/README.md) | Provides reusable runtime-oriented test support. |

`architectureTest` inspects configured production classes and source. `architectureAnalyze` runs that same rule engine directly and includes test-source evidence; it does not replace the final `check` gate. Generated test scaffolds intentionally fail until real behavior assertions replace the incomplete marker.

## Documentation

| Type | Document | Purpose | Surface |
| --- | --- | --- | --- |
| GENERAL | [Repository README](../README.md) | Public consumer contract and module map. | GitHub |
| Progression / Evidence | [Architecture Tests progression](../docs/progression/ARCHITECTURE_TESTS_PROGRESSION.md) | Audited implementation and validation state. | GitHub |
| Technical | [Contribution guide](../CONTRIBUTING.md) | Repository-specific development instructions. | GitHub |

## Deployment

> This module is not independently deployed.

Runtime owner: `None`. The module ships as a Gradle-compatible artifact or test-support library; no Deployment record applies.

## Development

- **Module Type:** `TOOLING`
- **Runtime:** `None`
- **Current PR Stack:** [platform integration root #15](https://github.com/TavallStudios/tavall-test-suite-tools/pull/15), [artifact version alignment #14](https://github.com/TavallStudios/tavall-test-suite-tools/pull/14), [test-authoring enforcement #8](https://github.com/TavallStudios/tavall-test-suite-tools/pull/8); documentation update: [PR #16](https://github.com/TavallStudios/tavall-test-suite-tools/pull/16).
- Repository-specific development guide: [CONTRIBUTING.md](../CONTRIBUTING.md).


<details>
<summary>Documentation Update State</summary>

### Current Locations

| Surface | Sync State | Location | Last Updated | Evidence |
| --- | --- | --- | --- | --- |
| GitHub | `PRIMARY` | `TavallStudios/tavall-test-suite-tools/gradle-plugin/README.md` | 2026-09-27 12:59 PM PDT | [PR #16](https://github.com/TavallStudios/tavall-test-suite-tools/pull/16) |
| Notion | `NOT_APPLICABLE` | — | 2026-09-27 12:59 PM PDT | README routing surface; no 1:1 twin is assigned. |

### Update History

| Timestamp | Surface | Event | Location | Previous Location | Evidence | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-09-27 12:59 PM PDT | GitHub | `CREATED` | `TavallStudios/tavall-test-suite-tools/gradle-plugin/README.md` | — | [PR #16](https://github.com/TavallStudios/tavall-test-suite-tools/pull/16) | Added a contextual module README with source-backed ownership and links to the current consumer and validation records. |

</details>
