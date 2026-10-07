# modules/cli

Owns reusable Tavall CLI architecture checks as the `org.tavall:tavall-architecture-cli` rule artifact.

## Responsibility

### Owns
- Verifying concrete CLI commands implement a typed domain command interface.
- Verifying commands declare `@DelegatesTo` and do not constructor-inject managed collaborators.
- Rejecting direct `IDependencyMap` or `DependencyMap` access in command source.

### Does Not Own
- The reusable Tavall CLI framework or its executable.
- Product command implementations, domain behavior, or command registration for Cloud, CI, Minecraft, or other systems.

The rule enforces the command contract in [`tavall-docs` CLI Architecture](https://github.com/TavallStudios/tavall-docs/blob/main/docs/quality/CLI_ARCHITECTURE.md). It is verification tooling with no production runtime owner.

## Repository Structure

tavall-test-suite-tools/\
├── modules/\
│   ├── [`core`](../core/README.md)\
│   ├── [`patterns`](../patterns/README.md)\
│   ├── [`di`](../di/README.md)\
│   ├── [`registry`](../registry/README.md)\
│   ├── [`cache`](../cache/README.md)\
│   ├── [`database`](../database/README.md)\
│   ├── [`runtime`](../runtime/README.md)\
│   ├── [`web`](../web/README.md)\
│   └── **[`cli`](README.md) ← This Module**\
└── [`gradle-plugin`](../../gradle-plugin/README.md)\

## Relationships

| Module / System | Relationship |
| --- | --- |
| [`modules/core`](../core/README.md) | Supplies architecture rule contracts and consumer analysis. |
| [`modules/web`](../web/README.md) | Applies the same reusable rule-module boundary to Tavall Web consumers. |
| Tavall CLI (`tavall-cli-api`) | Defines the command types inspected by this rule. |
| Tavall DI | Defines the delegation and dependency-access patterns checked by this rule. |
| [`gradle-plugin`](../../gradle-plugin/README.md) | Makes the selected rule artifact available to consumer test suites. |

## Validation

`CliArchitectureRuleTest` covers valid command composition and violations for missing interfaces, missing `@DelegatesTo`, constructor injection, and direct dependency-map access. The module is also included in root `check`.

## Deployment

This module is not independently deployed. Runtime owner: `None`.

## Development

- **Module Type:** `LIBRARY`
- **Runtime:** `None`
- **Current source:** merged Tavall Test Suite Tools PR #23; current rule source is on `main`.
- Repository-level implementation and validation evidence is recorded in [Architecture Tests progression](../../docs/progression/ARCHITECTURE_TESTS_PROGRESSION.md).

## Documentation State

| Surface | State | Location |
| --- | --- | --- |
| GitHub | `PRIMARY` | `TavallStudios/tavall-test-suite-tools/modules/cli/README.md` |
| Notion | `NOT_APPLICABLE` | No module-specific twin is assigned. |
