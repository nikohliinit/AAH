<!-- AAH:BEGIN -->
# AAH Delivery Project — service

This project (service, stack: <unspecified>) uses the AAH (Ascend Agentic
Harness) delivery framework for standardized AI-assisted software delivery.

## Core Rules

### State Management
- ALWAYS read `manifest.yaml` and `claude-progress.json` before starting any work
- ALWAYS read `decision-registry.yaml` for decision state (replaces phase-plan.yaml)
- ALWAYS update `claude-progress.json` at the end of every feature or session

### Testing — NO MOCKS
- NEVER use mock frameworks (jest.mock, unittest.mock, sinon, pytest-mock,
  nock, testdouble, vitest mock, proxyquire) in test code
- ALL tests must be functional — executing against the real running system
- ALL tests must be executable locally
- Do not generate mocks, stubs, or test doubles unless the feature spec
  explicitly defines a local test double

### Environment Configuration — NON-NEGOTIABLE
- `.env.example` is the SINGLE SOURCE OF TRUTH for what configuration this
  application needs. It is COMMITTED and holds variable NAMES with safe
  placeholders — NEVER a real secret, key, password, or endpoint
- `.env` holds the real values, is gitignored, and is the ONLY place values live.
  Never read a value from `.env.example`; never write a value into it
- The startup env checker (`config/env_check.py` or `config/env-check.cjs`,
  depending on stack) validates every required variable at boot and fails with
  `ERR_CDR_78_EX_CONFIG`, naming every missing variable at once and pointing the
  user at `cp .env.example .env`. Call it from the application entrypoint BEFORE
  any module reads configuration
- EVERY module that reads a new environment variable does THREE things in the
  same change: (1) lists the name in its feature file's
  `## Required Env Variables` section, (2) adds it to `.env.example` with a safe
  placeholder, (3) registers it in the checker's required list. Doing one or two
  of the three is what produces a runtime failure nobody can diagnose
- If the project's stack ships no checker (Go, Rust, Java, …), the scaffold-first
  feature implements the SAME contract in the project's own language: same error
  code, same message shape, same `.env.example` guidance
- NEVER work around a missing variable by hardcoding a value, inlining a default
  for a secret, or catching the config error — fix the environment

### Feature List Protection
- It is UNACCEPTABLE to remove or edit features in feature-list.json
- ONLY status changes (passes: true/false) are allowed
- Structural changes will be blocked by hooks

### Artifacts
- ALWAYS write artifacts to the correct `.aah/` subdirectory
- ALWAYS follow artifact templates when generating documents

### Git Discipline
- ALWAYS commit progress to git with descriptive messages after meaningful changes
- NEVER leave uncommitted work at the end of a feature or session
- Leave the environment in a clean, working state
- In case of brownfield, confirm branch before doing anything

### Feedback Routing — NON-NEGOTIABLE
- ANY user message describing a bug, broken behavior, or change request
  MUST be routed through `Skill("aah-fix")` — NEVER act on it directly
- Bypass ONLY when the user explicitly says "do not update files" or
  "just tell me"
- **Exception — aah-build §7B in-place fix:** when aah-build's §7B two-test
  passes (change is confined to the same module AND needs no prior-phase
  expertise), aah-build dispatches the implementer directly in FIX mode.
  Do NOT route to `aah-fix` in that case.

### Build Phase Constraints
- NEVER run raw test commands (`pytest`, `npm test`, `jest`) in the main
  conversation — dispatch the implementer, which runs its own tests; the
  cumulative gate is `aah run core.build.run_regression_suite`
- ALL failures route through `Skill("aah-fix")` which dispatches the appropriate agent
- If a framework tool fails: fix only the git precondition (commit/checkout),
  retry. Everything else goes through `Skill("aah-fix")`
- **Exception — aah-build §7B in-place fix:** see Feedback Routing above

### Session Compaction Recovery
- If the session context is compacted, immediately reinvoke the skill that was
  active at the time of compaction using the Skill tool. This ensures full skill
  instructions are reloaded and no steps are missed.

### Work Increments
- Work on ONE feature at a time — complete it fully before starting the next

## Directory Structure

- `.aah/discuss/` — Discuss phase artifacts
- `.aah/architecture/` — Architecture phase artifacts
- `.aah/plan/specs/` — Technical specifications
- `.aah/plan/features/` — Feature YAML definitions
- `.aah/plan/sprint-contracts/` — Sprint contracts per wave
- `.aah/build/` — Implementation state, test results
- `.aah/build/test-results/` — Per-feature and regression test results
- `.aah/deploy/` — Deployment configs and IaC
- `.aah/deploy/infra/` — Infrastructure provisioning templates
- `.aah/codebase-intel/` — Codebase intelligence artifacts (unified for greenfield and brownfield)
- `.aah/audit/` — Phase logs, traceability matrix
<!-- AAH:END -->
