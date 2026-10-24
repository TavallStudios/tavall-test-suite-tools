# modules/testing

Owns Tavall's reusable test-authoring architecture rule as `org.tavall:tavall-architecture-testing`.

## Responsibility

### Owns
- Checking that behavior-bearing production types have a direct JUnit 5 test in the matching package and source path.
- Checking test naming, assertion evidence, and mechanically detectable test-authoring anti-patterns.
- Rejecting malformed Java test sources and generated incomplete scaffolds.

### Does Not Own
- Product behavior, consumer scenario design, or runtime/infrastructure realism.
- The consumer's test-suite composition, which stays in the owning repository's test suite.

The rule inspects test-source roots through the canonical test-suite boundary while production classes remain the subject of architecture assessment. Static checks do not claim to prove that fakes, infrastructure, cleanup, or scenarios are realistic or complete.

## Repository Structure

tavall-test-suite-tools/\
├── modules/\
│   ├── [`core`](../core/README.md)\
│   ├── [`patterns`](../patterns/README.md)\
│   ├── **[`testing`](README.md) ← This Module**\
│   ├── [`di`](../di/README.md)\
│   ├── [`registry`](../registry/README.md)\
│   ├── [`cache`](../cache/README.md)\
│   ├── [`database`](../database/README.md)\
│   ├── [`runtime`](../runtime/README.md)\
│   └── [`web`](../web/README.md)\
└── [`gradle-plugin`](../../gradle-plugin/README.md)\

## Relationships

| Module / System | Relationship |
| --- | --- |
| [`modules/core`](../core/README.md) | Provides source/class indexing, findings, assessments, and shared rule contracts. |
| [`gradle-plugin`](../../gradle-plugin/README.md) | Exposes `architectureAnalyze` and `architectureTest` to consumer test suites. |
| `tavall-docs` testing policy | Owns the human policy; this module enforces mechanically reliable portions. |

## Validation

`TestAuthoringRule` tests cover direct-test discovery, JUnit and naming requirements, assertion evidence, managed-dependency composition, generated scaffolds, and source diagnostics. Root `check` runs the module and Gradle plugin verification.

## Deployment

This module is not independently deployed. Runtime owner: `None`.

## Development

- **Module Type:** `LIBRARY`
- **Runtime:** `None`
- **Current source:** Tavall Test Suite Tools PR #8.
- **Current PR Stack:** [platform integration root #24](https://github.com/TavallStudios/tavall-test-suite-tools/pull/24) → [per-class assessment and test-authoring #8](https://github.com/TavallStudios/tavall-test-suite-tools/pull/8).
- Repository-level implementation and validation evidence is recorded in [Architecture Tests progression](../../docs/progression/ARCHITECTURE_TESTS_PROGRESSION.md).
