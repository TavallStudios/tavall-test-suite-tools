# Tavall Test Suite Tools Progression

> **Document Type:** System Progression
> **Source of Truth For:** Audited provenance, implementation, publication, and consumer adoption progression for the canonical Tavall Test Suite Tools
> **Must Not Define:** Written architecture policy (owned by `tavall-docs`), product-specific behavior (owned by consumer repositories)
> **Current Status:** Production Canonical / Reusable Gradle Plugin & Modules Published / Active Across Consumers
> **Last Audited Producer Commit:** `60aa9bee998b403175ff159572ab3546a996803b`
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
- **2026-10-07 - integrate the current Web owner and DI follow-up**: PR #22 now contains PR #21 as an explicit parent and has no net Web rule/source diff relative to that parent. The combined TST source passed root `check` and affected module tests on `60aa9bee`.

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
| Producer source, module tests, and repository check | PASS_LOCAL | Exact combined PR #22 source `60aa9bee998b403175ff159572ab3546a996803b`: root `./gradlew check` succeeded with 44 actionable tasks (6 executed, 38 up-to-date). The affected Web, DI, and Gradle plugin tests were rerun; reports show 34 tests total (Web 11, DI 13, CLI 5, Gradle plugin 5), 0 failures/errors/skips. | Submit exact combined source through hosted Tavall CI. |
| Job-scoped artifact resolution | PARTIAL_LOCAL | Root check published `core` and `patterns` to `build/tavall-ci-dependencies`. The current PR #22 TestKit fixture still has a Maven Local path; PR #8 separately validates the job-scoped repository integration but is not yet in this branch. | Integrate PR #8 into the producer root and rerun the combined source. |
| Hosted Tavall CI | NOT_RUN_CURRENT_HEAD | No hosted Tavall CI request was executed for `60aa9bee`. | Run the canonical exact-source executor after the source graph is complete. |
| Web exact-source consumer architecture/build | PENDING_CURRENT_SOURCE | Web PR #59 previously reported 436 findings with the stale TST #22 source `345b914`, including 58 frontend package-ownership findings that PR #21 handles. | Pin `60aa9bee` in Web and rerun its architecture gate. |
| Package-backed Web consumer | PENDING | No producer artifact was published to GitHub Packages or a hosted internal package authority for this source. | Validate package-backed resolution separately after hosted producer and Web architecture gates pass. |
