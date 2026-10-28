# Contributing to Tavall Test Suite Tools

> **Current consumer policy (2026-10-28):** Architecture-test execution is suspended organization-wide. Keep the reusable tool and its own regular tests; do not wire the plugin or its tasks into consumer `check`/CI until replacement setup is approved.

This repository is the canonical executable source for Tavall Studios architecture tests.

## Development & Contribution Guidelines

1. **Policy Authority**:
   - Written architecture and documentation policy is governed organization-wide by [`TavallStudios/tavall-docs`](https://github.com/TavallStudios/tavall-docs), including `docs/quality/GIT_WORKFLOW.md` and relevant architecture standards.
   - This repository implements the mechanically enforceable subset of that policy as automated JUnit Platform architecture tests and a reusable Gradle plugin.

2. **Source Layout & Executable Boundary**:
   - `modules/` and `gradle-plugin/` are the maintained executable architecture-test sources.
   - Files under `repositories/` are historical provenance/migration snapshots, pinned via `manifest/sources.json`. Do not edit them as consumer runtime logic.
   - Do not copy reusable canonical rule source into consumer repositories. Consumer repositories consume the published Gradle plugin and declare the production projects inspected by their repository test suite (`*-test-suite`).

3. **Rule Verification Requirements**:
   - Reusable-rule changes must include TestKit or module-test evidence for the rule behavior. During the organization-wide suspension, consumer wiring must keep `architectureTest` out of ordinary `check`; TestKit may invoke it explicitly to verify the reusable tool.

4. **Architecture Authoring Loop**:
   - After Java production or test edits in this repository, run the regular repository tests. `architectureAnalyze` and `architectureTest` remain explicit tool capabilities for replacement-suite setup and are not current consumer acceptance gates.
   - Use `generateTavallTestScaffold -PtavallTestClass=<production FQCN>` only as a fail-closed starting point. Replace the incomplete marker with real behavior assertions before accepting the test.
   - Keep source locations and blocking findings visible. A diagnostic score does not turn an unbaselined blocking finding into a pass.

5. **Git Workflow**:
   - Create a focused topic branch (`working/<topic>`) targeting `main`.
   - Validate locally with `./gradlew check` (Java 25).
   - Follow standard review flow with visible review or Owner Self-Review records before promotion.
