# Contributing to Tavall Test Suite Tools

This repository owns the reusable source for Tavall Studios architecture-test tooling. Consumer execution is suspended organization-wide as of 2026-10-28 while the replacement suite is prepared.

## Development & Contribution Guidelines

1. **Policy Authority**:
   - Written architecture and documentation policy is governed organization-wide by [`TavallStudios/tavall-docs`](https://github.com/TavallStudios/tavall-docs), including `docs/quality/GIT_WORKFLOW.md` and relevant architecture standards.
   - This repository retains the reusable JUnit Platform rules and Gradle plugin that may support a future replacement suite; the current consumer gate is not active.

2. **Source Layout & Executable Boundary**:
   - `modules/` and `gradle-plugin/` are the maintained executable architecture-test sources.
   - Files under `repositories/` are historical provenance/migration snapshots, pinned via `manifest/sources.json`. Do not edit them as consumer runtime logic.
   - Do not copy reusable canonical rule source into consumer repositories. Consumer repositories consume the published Gradle plugin and declare the production projects inspected by their repository test suite (`*-test-suite`).

3. **Rule Verification Requirements**:
   - Keep the regular repository `./gradlew check` green, including this plugin's unit/TestKit tests.
   - TestKit may invoke `architectureTest` explicitly inside an isolated plugin-contract fixture. The plugin must not attach it to the normal `check` lifecycle.
   - Do not add consumer plugin dependencies, `architectureTest` wiring, or Tavall CI architecture checks while the organization-wide suspension is active. Replacement consumer acceptance requires updated canonical Docs policy and a fresh owner-scoped rollout.

4. **Git Workflow**:
   - Create a focused topic branch (`working/<topic>`) targeting `main`.
   - Validate locally with `./gradlew check` (Java 25).
   - Follow standard review flow with visible review or Owner Self-Review records before promotion.
