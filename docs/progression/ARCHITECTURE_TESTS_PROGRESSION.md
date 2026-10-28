# Tavall Test Suite Tools Progression

> **Current execution status (2026-10-28):** Reusable rule/plugin source remains available for replacement setup. Consumer architecture-test execution and architecture-only Tavall CI gates are suspended organization-wide; regular consumer testing remains active. Historical consumer passes below are not current acceptance.

> **Document Type:** System Progression
> **Source of Truth For:** Audited provenance, implementation, publication, and consumer adoption progression for the canonical Tavall Test Suite Tools
> **Must Not Define:** Written architecture policy (owned by `tavall-docs`), product-specific behavior (owned by consumer repositories)
> **Current Status:** Production Canonical / Reusable Gradle Plugin & Modules Retained / Consumer Execution Suspended Organization-Wide
> **Last Audited Producer Candidates:** PR #21 `c37d37e1924c777b3c7d5e0174891af6396cdc86` (Web rule module local checks); PR #8 `e0f990f` (per-class/testing opt-in self-check); PR #22 pre-suspension candidate `a9f3ec84cc2ddbd6a3552e531ffc6ad46c895b9c` (DI rule changes). The current PR #8 → PR #21 → PR #22 stack requires regular-check revalidation before it is accepted.
> **Canonical Repository:** `TavallStudios/tavall-test-suite-tools`

## 1. About

Tracks the evolution and adoption of Tavall Studios' canonical executable architecture-testing layer, from its extraction out of Project Novus to its modular distribution and consumer integration model.

`tavall-docs` owns written architecture policy; this repository retains reusable, mechanically enforceable rule and test-tool source for replacement setup. Consumer repositories do not execute the architecture suite while the organization-wide suspension is active.

## 2. Authority & Consumer Model

The division of authority is explicit:

- **Written Policy (`tavall-docs`)**: Canonical written architecture rules, design standards, module boundaries, and quality requirements.
- **Executable Enforcement (`tavall-test-suite-tools`)**: Reusable ArchUnit and AST-based rule implementations packaged as Gradle plugin and modular libraries.
- **Consumer Execution (Consumer Repositories)**: Suspended organization-wide as of 2026-10-28. Consumers use regular Tavall unit/contract/integration/runtime/quality/E2E procedures and do not apply this plugin or declare architecture-only CI gates until replacement setup is approved.
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
- **2026-09-11 - per-class assessment and direct-test authoring (PR #8 original head `80938a9`)**: Added a shared assessment engine, machine-readable source evidence, `architectureAnalyze`, fail-closed test scaffolds, and the `testing` rule module. GitHub Actions run [34658277898](https://github.com/TavallStudios/tavall-test-suite-tools/actions/runs/34658277898) passed on that earlier exact head with Java 25 / Gradle 9.6.1.
- **2026-10-03 - Web frontend artifact ownership correction**: Updated the reusable Web rule to follow the current Tavall Web module DESIGN: the frontend library owns Page/rendering contracts and their compatibility package names, while the Web app and product surface modules consume the route API. Project-level checks now execute once per module even when Gradle supplies more than one Java source root.
- **2026-10-04 - Web rule producer CI (PR #21)**: Tavall-Web #58's shared-Executor request `85b6c8d9-e54c-49d9-b50a-2422ae6dcb02` exposed the stale frontend/API rule. Producer Tavall CI request `176b1dc7-df2b-4f99-aaf6-b19370f98e11` passed `QUALITY` and `REQUIRED_ALL` on exact producer source `027d2cb4a8219d3e1c82922f5c2986a8e33ebc7b`, Java 25 / Gradle 9.6.1. Evidence SHA-256: `8c75b9c97ba3febdb6d30455eb408730cdd23071c9267f0959b4d4d5c43cbe4c`. Web consumer revalidation and package-backed resolution remain pending.
- **2026-10-04 - canonicalize repeated DI findings and generated access (PR #22)**: Repeated field/constructor dependencies and same-file construction emit one canonical finding. Generated `@DelegatesTo` `*DependencyAccess` bridges are exempt from authored direct-map checks; hand-authored direct-map access and concrete business implementations remain checked.
- **2026-10-05 - Tavall CLI architecture rule module (PR #23)**: Added the reusable `modules/cli` rule artifact for interface-first commands, `@DelegatesTo`, no constructor-injected collaborators, and no direct dependency-map access. The module follows `tavall-docs` CLI architecture policy; it does not own the CLI framework or product commands.
- **2026-10-07 - expand Web artifact-boundary regression coverage (PR #21 follow-up)**: Fixtures now cover all eight frontend-owned legacy package prefixes, reject those packages in Web product modules, and keep the `org.tavall.web.api` root in `tavall-web-api`. `:modules:web:test` passed 11 tests and root `./gradlew check` passed 44 actionable tasks on the current PR #21 source. Exact-source consumer validation remains separate.
- **2026-10-07 - reconcile PR #8 with the current integration root**: Merged current `main` and the refreshed `staging/platform` root into the existing PR branch, removed the obsolete tracked `AGENTS.md` guidance, moved the authoring instructions to `CONTRIBUTING.md`, and switched TestKit artifact resolution from Maven Local to the job-scoped `TAVALL_CI_DEPENDENCY_REPOSITORY`. Current merge-forward source is `0609adbc9257914be5758d3c14743fcccd9a1926`.
- **2026-10-07 - prior PR #22 combined candidate**: TST source `60aa9bee998b403175ff159572ab3546a996803b` passed root `check` and affected Web, DI, Gradle plugin, and CLI tests (34 total, no failures/errors/skips). This source predates the PR #8 integration and is historical evidence only.
- **2026-10-24 - retryable CLI source dependency bootstrap failure (request `d1b2d6e8-7ed8-4cca-8493-fdc72f0d4b15`)**: Exact-source CI reached publication of `tavall-cli@7f4747c8f488d4326d9082ce6d0bd61eec750fd6` and could not resolve `org.tavall:tavall-concurrency:1.0.0` because that source was absent from the TST aggregate and Maven Central DNS failed. TST build, tests, and artifact production did not run. The CLI rule module now declares the CLI owner’s concurrency, eventbus, and reflection source inputs; a new exact-source retry is planned after review. Evidence: `tavall-storage://ci/runs/d1b2d6e8-7ed8-4cca-8493-fdc72f0d4b15/ci-evidence.json` (SHA-256 `8c1d01b34afbede2aeb4f3cbe1548af7bf20039e223d15413aa0d336cda20956`).

## 4. Module Matrix

| Module | Published Artifact | Enforcement Scope |
| --- | --- | --- |
| `gradle-plugin` | `org.tavall.architecture-tests` | Gradle integration, task registration (`architectureTest`), target class and source resolution |
| `modules/core` | `org.tavall:tavall-architecture-core` | Core type hygiene, forbidden imports, package boundary assertions |
| `modules/patterns` | `org.tavall:tavall-architecture-patterns` | General structural patterns, exception handling rules, immutable record conventions |
| `modules/testing` | `org.tavall:tavall-architecture-testing` | Direct behavior-test and Java test-source architecture checks |
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
3. **Consumer Verification Gate**: Suspended organization-wide. Preserve regular consumer tests; do not require `architectureTest` or an architecture-only Tavall CI check.
4. **Temporary Debt Gate**: Deprecated or legacy patterns permitted temporarily via `config/architecture-debt.txt`; non-matching entries cause immediate build failure to prevent debt creep.

## Current Validation State

| Gate | State | Evidence | Next |
| --- | --- | --- | --- |
| Producer source and repository checks, PR #21 candidate | PASS_LOCAL | On exact candidate source `c37d37e1924c777b3c7d5e0174891af6396cdc86`, `:modules:web:test` passed 11 tests and root `./gradlew check` passed 44 actionable tasks. | This result does not cover the combined PR #8 → PR #21 source. |
| Producer source and repository checks, PR #8 candidate | PASS_LOCAL | Exact merge-forward source `0609adbc9257914be5758d3c14743fcccd9a1926`: `./gradlew check` succeeded with 53 actionable tasks (9 executed, 44 up-to-date); JUnit results were plugin 6, DI 10, CLI 5, Web 3, 24 total with no failures/errors/skips. | This result does not cover the combined PR #8 → PR #21 source. |
| Producer source and module tests, earlier PR #22 candidate | PASS_LOCAL | Exact source `60aa9bee998b403175ff159572ab3546a996803b`: root check and affected Web, DI, Gradle plugin, and CLI tests passed (34 total). | Historical evidence only; this result does not cover the current PR stack. |
| Job-scoped source artifact resolution, PR #8 candidate | PASS_LOCAL | Root check published `core`, `patterns`, and `testing` into `build/tavall-ci-dependencies`; Gradle TestKit consumers resolved them through `TAVALL_CI_DEPENDENCY_REPOSITORY`. | Hosted executor publication/resolution remains separate. |
| Previous TST primary-plugin bootstrap | RESOLVED_IN_SOURCE | TST PR #22 source `6e366e210e14e646bd07fede4c0707b33f6e043b` first failed before source publication because `org.tavall.architecture-tests:1.1.0` was absent from the job-private repository and Plugin Portal. PR #8 now declares the primary plugin and core module; retry request `d1b2d6e8` passed that stage and reached CLI source publication. No TST tests/artifacts were produced by the earlier attempt. | Retain the primary-plugin provider in the root manifest. |
| TST CLI transitive source dependency bootstrap | RETRYABLE_PREBUILD_FAILURE | On PR #22 source `66265de075273a467b85f01ae37f0f93c4242476`, CLI source `7f4747c8` could not resolve concurrency. Its required-source graph also names eventbus and reflection, but the TST aggregate omitted all three. Request `d1b2d6e8` failed before the TST build/tests and produced no artifact. | PR #21’s module-owned manifest now declares the three exact sources. Rerun after review. |
| Current PR #21 CLI module manifest candidate | NOT_RUN_CURRENT_HEAD | Local `modules/cli/.tavallci/ci.yaml` adds the CLI owner’s concurrency, eventbus, and reflection sources with `required-source` selectors and project substitutions. Only YAML/aggregate/source-substitution checks have run. | Review, then carry this head into PR #22 and schedule a fresh exact-source TCI. |
| Combined PR #8 → PR #21 → PR #22 source | NOT_RUN_CURRENT_HEAD | The local child now includes PR #21’s module source closure plus PR #22’s DI changes. YAML/aggregation/source-substitution parity passed locally; no build, tests, or TCI ran on this head. | Review the new source aggregate, then schedule the exact-source retry. |
| Web exact-source consumer architecture/build | PENDING_CURRENT_SOURCE | Web PR #59 previously reported 436 architecture findings with TST PR #22 source `345b914`; 58 were frontend package-ownership findings absent from the PR #21 rule source. | Reconcile the consumer pin to the combined source and rerun its owner profile. |
| Package-backed consumer resolution | PENDING | No artifacts were published to GitHub Packages or a hosted internal package authority for the combined source. | Validate package-backed resolution separately after producer acceptance. |
