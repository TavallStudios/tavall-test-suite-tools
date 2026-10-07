# modules/database

Owns reusable Tavall database architecture checks.

## Responsibility

### Owns
- Database-specific architecture rule implementation.

### Does Not Own
- Database provider behavior, schema, or service lifecycle.

## Repository Structure

tavall-test-suite-tools/\
├── modules/\
│   ├── [`core`](../core/README.md)\
│   ├── [`patterns`](../patterns/README.md)\
│   ├── [`di`](../di/README.md)\
│   ├── [`registry`](../registry/README.md)\
│   ├── [`cache`](../cache/README.md)\
│   ├── **[`database`](README.md) ← This Module**\
│   ├── [`runtime`](../runtime/README.md)\
│   ├── [`web`](../web/README.md)\
│   └── [`cli`](../cli/README.md)\
└── [`gradle-plugin`](../../gradle-plugin/README.md)\
## Relationships

| Module / System | Relationship |
| --- | --- |
| [`modules/core`](../core/README.md) | Uses the shared rule contract and execution engine. |
| [`gradle-plugin`](../../gradle-plugin/README.md) | Selected artifacts are run by the consumer-facing Gradle plugin. |
| [`modules/runtime`](../runtime/README.md) | Provides reusable runtime-oriented test support. |

## Documentation

| Type | Document | Purpose | Surface |
| --- | --- | --- | --- |
| GENERAL | [Repository README](../../README.md) | Public consumer contract and module map. | GitHub |
| Progression / Evidence | [Architecture Tests progression](../../docs/progression/ARCHITECTURE_TESTS_PROGRESSION.md) | Audited implementation and validation state. | GitHub |
| Technical | [Contribution guide](../../CONTRIBUTING.md) | Repository-specific development instructions. | GitHub |

## Deployment

> This module is not independently deployed.

Runtime owner: `None`. The module ships as a Gradle-compatible artifact or test-support library; no Deployment record applies.

## Development

- **Module Type:** `LIBRARY`
- **Runtime:** `None`
- **Current PR Stack:** [platform integration root #15](https://github.com/TavallStudios/tavall-test-suite-tools/pull/15), [artifact version alignment #14](https://github.com/TavallStudios/tavall-test-suite-tools/pull/14), [test-authoring enforcement #8](https://github.com/TavallStudios/tavall-test-suite-tools/pull/8); documentation update: [PR #16](https://github.com/TavallStudios/tavall-test-suite-tools/pull/16).
- Repository-specific development guide: [CONTRIBUTING.md](../../CONTRIBUTING.md).


<details>
<summary>Documentation Update State</summary>

### Current Locations

| Surface | Sync State | Location | Last Updated | Evidence |
| --- | --- | --- | --- | --- |
| GitHub | `PRIMARY` | `TavallStudios/tavall-test-suite-tools/modules/database/README.md` | 2026-09-27 12:59 PM PDT | [PR #16](https://github.com/TavallStudios/tavall-test-suite-tools/pull/16) |
| Notion | `NOT_APPLICABLE` | — | 2026-09-27 12:59 PM PDT | README routing surface; no 1:1 twin is assigned. |

### Update History

| Timestamp | Surface | Event | Location | Previous Location | Evidence | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-09-27 12:59 PM PDT | GitHub | `CREATED` | `TavallStudios/tavall-test-suite-tools/modules/database/README.md` | — | [PR #16](https://github.com/TavallStudios/tavall-test-suite-tools/pull/16) | Added a contextual module README with source-backed ownership and links to the current consumer and validation records. |

</details>
