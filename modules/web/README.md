# modules/web

Owns reusable Tavall Web architecture checks.

## Responsibility

### Owns
- Tavall Web-specific architecture rule implementation.

### Does Not Own
- Web runtime behavior or consumer-specific endpoint tests.

## Tavall Web Contract Map

This module checks the Web ownership boundary documented by Tavall Web PR #58's API and frontend DESIGN pages:

- `tavall-web-api` owns route, surface, and exposure contracts.
- `tavall-web-frontend` owns page, HTML/CSS, animation, asset, TypeScript, and rendering contracts. Its retained `org.tavall.web.api.*` packages are Java compatibility names; they do not make the Web frontend artifact depend on the route API artifact.
- The compatibility package roots are explicitly limited to `animation`, `asset`, `css`, `html`, `page`, `render`, `symbol`, and `ts` under `org.tavall.web.api`. The namespace root and route/surface packages remain owned by `tavall-web-api`, and product modules may not redeclare frontend contracts.
- `tavall-web-frontend-spring` is a Web platform adapter over the frontend framework. It depends on the frontend artifact and remains independent of the route API.
- `tavall-web-app` composes the route API with the frontend when it hosts product pages. Web product modules depend on the route API when they declare Web surfaces.

The reusable rule checks those source/artifact owners. Product-specific route behavior stays with its product module, and this module must not require the frontend library to depend on the route API merely because a Java package retains the historical `.api` segment.

Project-level checks run once per module even when consumers provide several source roots. Java source ownership checks still inspect every source root. The executable Web application must depend on `tavall-web-api`; the reusable frontend model and Spring renderer do not.

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
│   ├── **[`web`](README.md) ← This Module**\
│   └── [`cli`](../cli/README.md)\
└── [`gradle-plugin`](../../gradle-plugin/README.md)\
## Relationships

| Module / System | Relationship |
| --- | --- |
| [`modules/core`](../core/README.md) | Uses the shared rule contract and execution engine. |
| [`gradle-plugin`](../../gradle-plugin/README.md) | Selected artifacts are run by the consumer-facing Gradle plugin. |
| [`modules/runtime`](../runtime/README.md) | Provides reusable runtime-oriented test support. |
| [Tavall Web API DESIGN](https://github.com/TavallStudios/tavall-web/blob/working/retire-deleted-discord-web-module-20261003/docs/design/modules/TAVALL_WEB_API_DESIGN.md) | Defines the route/surface/exposure artifact boundary enforced by this module. |
| [Tavall Web frontend DESIGN](https://github.com/TavallStudios/tavall-web/blob/working/retire-deleted-discord-web-module-20261003/docs/design/modules/TAVALL_WEB_FRONTEND_DESIGN.md) | Defines page/rendering ownership and legacy Java package compatibility. |

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
- **Current PR Stack:** [platform integration root #24](https://github.com/TavallStudios/tavall-test-suite-tools/pull/24) → [Web artifact ownership #21](https://github.com/TavallStudios/tavall-test-suite-tools/pull/21).
- Repository-specific development guide: [CONTRIBUTING.md](../../CONTRIBUTING.md).


<details>
<summary>Documentation Update State</summary>

### Current Locations

| Surface | Sync State | Location | Last Updated | Evidence |
| --- | --- | --- | --- | --- |
| GitHub | `PRIMARY` | `TavallStudios/tavall-test-suite-tools/modules/web/README.md` | 2026-10-07 UTC | PR #21 source `526e807`; Web package ownership and root-source behavior align to the current DESIGN. |
| Notion | `NOT_APPLICABLE` | — | 2026-09-27 12:59 PM PDT | README routing surface; no 1:1 twin is assigned. |

### Update History

| Timestamp | Surface | Event | Location | Previous Location | Evidence | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-09-27 12:59 PM PDT | GitHub | `CREATED` | `TavallStudios/tavall-test-suite-tools/modules/web/README.md` | — | [PR #16](https://github.com/TavallStudios/tavall-test-suite-tools/pull/16) | Added a contextual module README with source-backed ownership and links to the current consumer and validation records. |
| 2026-10-03 4:49 PM PDT | GitHub | `UPDATED` | `TavallStudios/tavall-test-suite-tools/modules/web/README.md` | Same path | [PR #21](https://github.com/TavallStudios/tavall-test-suite-tools/pull/21) | Recorded the API/frontend artifact rule boundary and linked its current Tavall Web DESIGN owners. |
| 2026-10-03 4:57 PM PDT | GitHub | `UPDATED` | `TavallStudios/tavall-test-suite-tools/modules/web/README.md` | Same path | Tavall CI request `6438a2b6-9481-4b17-af2c-dbc57d3adf94`; [PR #21](https://github.com/TavallStudios/tavall-test-suite-tools/pull/21) | Recorded exact-source producer validation and the remaining Web consumer/package gates. |
| 2026-10-04 12:08 AM UTC | GitHub | `UPDATED` | `TavallStudios/tavall-test-suite-tools/modules/web/README.md` | Same path | Tavall CI request `176b1dc7-df2b-4f99-aaf6-b19370f98e11`; [PR #21](https://github.com/TavallStudios/tavall-test-suite-tools/pull/21) | Recorded producer validation on the exact current Web rule source and consumer/package gates. |
| 2026-10-07 UTC | GitHub | `UPDATED` | `TavallStudios/tavall-test-suite-tools/modules/web/README.md` | Same path | PR #21 source `526e807`; root `check` passed 44 actionable tasks and `modules:web:test` passed 11 tests | Listed the eight frontend-owned compatibility package prefixes and recorded the current staging parent. |

</details>
