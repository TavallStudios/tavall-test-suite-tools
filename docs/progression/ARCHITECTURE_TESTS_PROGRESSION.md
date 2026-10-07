# Tavall Test Suite Tools Progression

> **Document Type:** System Progression
> **Source of Truth For:** Audited provenance, implementation, publication, and consumer adoption progression for the canonical Tavall Test Suite Tools
> **Must Not Define:** Written architecture policy (owned by `tavall-docs`), product-specific behavior (owned by consumer repositories)
> **Current Status:** Production Canonical / Reusable Gradle Plugin & Modules Published / Active Across Consumers
> **Last Audited Producer Commit:** `0609adbc9257914be5758d3c14743fcccd9a1926`
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
- **2026-09-11 - per-class assessment and direct-test authoring (PR #8 original head `80938a9`)**: Added a shared assessment engine, machine-readable source evidence, `architectureAnalyze`, fail-closed test scaffolds, and the `testing` rule module. GitHub Actions run [34658277898](https://github.com/TavallStudios/tavall-test-suite-tools/actions/runs/34658277898) passed on that earlier exact head with Java 25 / Gradle 9.6.1.
- **2026-10-07 - reconcile PR #8 with the current integration root**: Merged current `main` and the refreshed `staging/platform` root into the existing PR branch, removed the obsolete tracked `AGENTS.md` guidance, moved the authoring instructions to `CONTRIBUTING.md`, and switched TestKit artifact resolution from Maven Local to the job-scoped `TAVALL_CI_DEPENDENCY_REPOSITORY`. Current merge-forward source is `0609adbc9257914be5758d3c14743fcccd9a1926`.

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
3. **Consumer Verification Gate**: Consumers execute `architectureTest` through their repository test-suite boundary; failure blocks local CI and PR promotion.
4. **Temporary Debt Gate**: Deprecated or legacy patterns permitted temporarily via `config/architecture-debt.txt`; non-matching entries cause immediate build failure to prevent debt creep.

## Current Validation State

| Gate | State | Evidence | Next |
| --- | --- | --- | --- |
| Producer source, module tests, and repository check | PASS_LOCAL | Exact merge-forward source `0609adbc9257914be5758d3c14743fcccd9a1926`: `./gradlew check` succeeded with 53 actionable tasks (9 executed, 44 up-to-date). JUnit results: Gradle plugin 6, DI 10, CLI 5, Web 3; 24 tests total, 0 failures/errors/skips. The `testing` rule is exercised through Gradle TestKit; its module has no separate test source yet. | Run canonical hosted Tavall CI at this exact source. |
| Job-scoped source artifact resolution | PASS_LOCAL | Root `check` published `core`, `patterns`, and `testing` into `build/tavall-ci-dependencies`; the plugin's TestKit consumers resolved them through `TAVALL_CI_DEPENDENCY_REPOSITORY`. | Hosted executor publication/resolution remains separate. |
| Hosted Tavall CI | NOT_RUN_CURRENT_HEAD | Previous GitHub Actions run `34658277898` passed on `80938a9`, before current-main/root reconciliation. No hosted Tavall CI run was executed on `0609adbc`. | Submit exact current source through the canonical Tavall CI executor. |
| Web exact-source consumer architecture/build | PENDING_CURRENT_SOURCE | Web PR #59 previously reported 436 architecture findings with the stale TST #22 source `345b914`; 58 were frontend package-ownership findings absent from the PR #21 rule source. | Reconcile PR #22 over PR #21, pin the exact combined source in Web, and rerun the consumer. |
| Package-backed consumer resolution | PENDING | No artifacts were published to GitHub Packages or a hosted internal package authority for this source. | Validate published coordinates separately after hosted producer acceptance. |
