# Tavall Test Suite Tools Progression

> **Document Type:** System Progression
> **Source of Truth For:** Audited provenance, implementation, publication, and consumer adoption progression for the canonical Tavall Test Suite Tools
> **Must Not Define:** Written architecture policy (owned by `tavall-docs`), product-specific behavior (owned by consumer repositories)
> **Current Status:** Production Canonical / Reusable Gradle Plugin & Modules Published / Active Across Consumers
> **Last Audited Producer Commit:** `c37d37e1924c777b3c7d5e0174891af6396cdc86`
> **Canonical Repository:** `TavallStudios/tavall-test-suite-tools`

## 1. About

Tracks the evolution and adoption of Tavall Studios' canonical executable architecture-testing layer, from its extraction out of Project Novus to its modular distribution and consumer integration model.

`tavall-docs` owns written architecture policy; this repository implements reusable, mechanically enforceable subsets of that policy as a Gradle plugin and test modules that consumer repositories execute during their own verification cycles.

## 2. Authority & Consumer Model

The division of authority is explicit:

- **Written Policy (`tavall-docs`)**: Canonical written architecture rules, design standards, module boundaries, and quality requirements.
- **Executable Enforcement (`tavall-test-suite-tools`)**: Reusable ArchUnit and AST-based rule implementations packaged as Gradle plugin and modular libraries.
- **Consumer Execution (Consumer Repositories)**: Each repository's canonical testing suite (`*-test-suite`, or root verification suite) applies `org.tavall.architecture-tests` and declares its production targets.
- **Historical Provenance (`repositories/`, `manifest/sources.json`)**: Preserved historical snapshots and imported commit hashes from Project Novus. These provide provenance only, not execution authority.

## 3. Progression Timeline

- **2026-09-07 - bootstrap architecture test repository (`2d25857`)**: Created the standalone repository to decouple architecture rule verification from individual product codebases.
- **2026-09-07 - import canonical architecture suite from Project Novus (`e1ed715`, PR #1)**: Imported core architecture tests and established initial standalone suite structure.
- **2026-09-07 - establish architecture test staging root (`f1d4258`)**: Established persistent staging root and quality validation gates.
- **2026-09-07 to 2026-09-08 - reusable CLI, MCP, and staging architecture contracts (`990f13f`, `0345070`, `fc90e8a`, `04e7be4`, `7257a4d`, PR #4)**: Defined reusable CLI/MCP contracts, prohibited new Repository type names, and distinguished release discovery from lease authority.
- **2026-09-10 - executable consumer gates (`dc8b90e`, `df7897e`, `53620bc`, `bef2f47`, PR #6, PR #3)**: Modularized architecture test runner, aligned consumer gates with runtime dependency ownership, defined repository `LOCAL_CI` entrypoint, and published complete consumer artifact graph to GitHub Packages.
- **2026-09-11 - canonical repository test-suite consumer model (`09dda79`, `90b0418`, `31b91cd`, `77aaa4f`, `c88fae9`, `9e29da6`, PR #7)**: Established repository testing suite (`*-test-suite`) as the canonical consumer boundary, replacing per-subproject duplicate gates with suite-owned target inspection.
- **2026-09-14 - update Tavall MC provenance repository name (`b38f994`, PR #9)**: Aligned provenance manifests with `tavall-mc` repository rename.
- **2026-09-19 - Tavall MC adopts canonical architecture suite**: Tavall MC consumer wired to canonical architecture-tests plugin via test suite boundary.
- **2026-09-22 - aggregate inherited DI marker debt and include compile classpaths (`0f6ca4c`, `81ae78d`, `3e71575`, PR #10)**: Aggregated retired DI marker debt handling and included consumer compile classpaths for comprehensive dependency analysis.
- **2026-09-23 - progression and lineage consolidation (`working/architecture-tests-progression-lineage-20260923`)**: Added canonical progression documentation, linked authority model to Tavall Docs, and bound repository verification contract.
- **2026-10-03 - Web frontend artifact ownership correction**: Updated the reusable Web rule to follow the current Tavall Web module DESIGN: the frontend library owns Page/rendering contracts and their compatibility package names, while the Web app and product surface modules consume the route API. Project-level checks now execute once per module even when Gradle supplies more than one Java source root.
- **2026-10-04 - Web rule producer CI (PR #21)**: Tavall-Web #58's shared-Executor request `85b6c8d9-e54c-49d9-b50a-2422ae6dcb02` exposed the stale frontend/API rule. Producer Tavall CI request `176b1dc7-df2b-4f99-aaf6-b19370f98e11` passed `QUALITY` and `REQUIRED_ALL` on exact producer source `027d2cb4a8219d3e1c82922f5c2986a8e33ebc7b`, Java 25 / Gradle 9.6.1. Evidence SHA-256: `8c75b9c97ba3febdb6d30455eb408730cdd23071c9267f0959b4d4d5c43cbe4c`. Web consumer revalidation and package-backed resolution remain pending.
- **2026-10-05 - Tavall CLI architecture rule module (PR #23)**: Added the reusable `modules/cli` rule artifact for interface-first commands, `@DelegatesTo`, no constructor-injected collaborators, and no direct dependency-map access. The module follows `tavall-docs` CLI architecture policy; it does not own the CLI framework or product commands.
- **2026-10-07 - expand Web artifact-boundary regression coverage (PR #21 follow-up)**: Fixtures now cover all eight frontend-owned legacy package prefixes, reject those packages in Web product modules, and keep the `org.tavall.web.api` root in `tavall-web-api`. `:modules:web:test` passed 11 tests and root `./gradlew check` passed 44 actionable tasks on the current PR #21 source. Exact-source consumer validation remains separate.
- **2026-10-04 - canonicalize repeated DI findings and generated access (PR #22)**: Repeated field/constructor dependencies and same-file construction emit one canonical finding. Generated `@DelegatesTo` `*DependencyAccess` bridges are exempt from authored direct-map checks; hand-authored direct map access and concrete business implementations remain checked.
- **2026-10-07 - allow generated typed access bridges as DI consumers (PR #22)**: Added regression coverage for injecting generated dependency-access bridges. The prior PR #22 source passed `modules:di` (13 tests) and root `check` (40 tasks); this is historical evidence and does not validate the current combined head.
- **2026-10-04 - canonicalize repeated DI findings and generated access (PR #22)**: Repeated field/constructor dependencies and same-file construction emit one canonical finding. Generated `@DelegatesTo` `*DependencyAccess` bridges are exempt from authored direct-map checks; hand-authored direct map access and concrete business implementations remain checked.
- **2026-10-07 - allow generated typed access bridges as DI consumers (PR #22)**: Added regression coverage for injecting generated dependency-access bridges. The prior PR #22 source passed `modules:di` (13 tests) and root `check` (40 tasks); this is historical evidence and does not validate the current combined head.

## 4. Module Matrix

| Module | Published Artifact | Enforcement Scope |
| --- | --- | --- |
| `gradle-plugin` | `org.tavall.architecture-tests` | Gradle integration, task registration (`architectureTest`), target class and source resolution |
| `modules/core` | `org.tavall:tavall-architecture-core` | Core type hygiene, forbidden imports, package boundary assertions |
| `modules/patterns` | `org.tavall:tavall-architecture-patterns` | General structural patterns, exception handling rules, immutable record conventions |
| `modules/di` | `org.tavall:tavall-architecture-di` | Dependency injection hygiene, constructor injection rules, singleton/lifecycle validation |
| `modules/registry` | `org.tavall:tavall-architecture-registry` | Registry registration patterns and lookup lifecycle enforcement |
| `modules/cache` | `org.tavall:tavall-architecture-cache` | Cache boundaries, TTL declarations, and thread-safety invariants |
| `modules/database` | `org.tavall:tavall-architecture-database` | Persistence isolation, transaction boundary rules, and entity constraints |
| `modules/runtime` | `org.tavall:tavall-architecture-runtime` | Shared runtime verification helpers and test fixture boundaries |
| `modules/web` | `org.tavall:tavall-architecture-web` | Tavall Web platform/product dependency direction and source ownership |
| `modules/cli` | `org.tavall:tavall-architecture-cli` | CLI command interfaces, Tavall DI delegation, constructor, and direct map access rules |

## 5. Validation and Acceptance Gates

1. **Standalone Suite Validation**: Root `./gradlew check` validates all rule modules and executes Gradle plugin functional tests.
2. **Package Publication Gate**: All artifacts publish to `TavallStudios/tavall-test-suite-tools` on GitHub Packages with verified POMs and Gradle metadata.
3. **Consumer Verification Gate**: Consumers execute `architectureTest` through their repository test-suite boundary; failure blocks local CI and PR promotion.
4. **Temporary Debt Gate**: Deprecated or legacy patterns permitted temporarily via `config/architecture-debt.txt`; non-matching entries cause immediate build failure to prevent debt creep.

## Current Validation State

| Gate | State | Evidence | Next |
| --- | --- | --- | --- |
| Producer source, tests, and repository quality | PASS_LOCAL | Exact current PR #21 source `526e807da2e057bf2e01459a9afc83fc86027210`: `:modules:web:test` passed 11 tests; root `./gradlew check` passed 44 actionable tasks (6 executed, 38 up-to-date). The Web rule/test source is commit `c37d37e`. | Submit exact current source through hosted Tavall CI. |
| Web exact-source consumer architecture/build | PENDING_CURRENT_SOURCE | Web PR #59 app tests, `bootJar`, and artifact verification passed with TST PR #22 source `345b914`, but its Web rule copy reported 436 architecture findings, including 58 package-ownership findings that contradict the current Web DESIGN and PR #21's existing rule. | Compose the current PR #21 rule with PR #22's DI rule, then rerun Web #59 against that exact producer head. |
| Package-backed Web consumer | PENDING | No current producer package publication was performed for this update. | Validate package-backed resolution independently after the producer source and consumer architecture gates pass. |
