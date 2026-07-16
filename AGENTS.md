# AGENTS.md

Instructions for AI agents working on this project.

## Build & Test

```sh
mvn clean test
mvn clean package
```

The test class is `StartMacroSimulationTest` — runs the main command with simulation parameters and asserts exit code 0.

## Project Structure

```
src/main/java/org/bidib/jbidibc/StartMacroSimulation.java   — Main class
src/main/resources/simulation-demo/simulation.xml            — Simulation config
src/test/java/org/bidib/jbidibc/StartMacroSimulationTest.java — Test
```

## Dependencies

- `jbidibc-tools:2.0-SNAPSHOT` (sonatype snapshots)
- `bidibwizard-simulation:2.0-SNAPSHOT` (sonatype snapshots)
- `junit-jupiter:5.10.2` (test)

## Key Details

- Simulation port is `sim`
- Simulation config uses IF2Simulator master with LedIo24Simulator subnode (address 5, uniqueId `45000D7F000E94`)
- `-simFile` parameter uses classpath resource paths (e.g. `/simulation-demo/simulation.xml`)

## AI Agent Rules of Engagement

These rules apply to ALL AI agents working on this codebase.

### Attribution

- All AI-generated content (GitHub PR descriptions, review comments, JIRA comments) MUST clearly
  identify itself as AI-generated and mention the human operator.
  Example: "_Claude Code on behalf of [Human Name]_"
- **Never guess or hallucinate the operator's name.** Always determine it programmatically:
  - Use `gh api /user --jq '.login'` to get the authenticated GitHub username.
  - If for any reason the lookup fails, omit the name rather than guessing.
- AI coding agents MUST be configured to add co-authorship trailers to commits
  (e.g., `Co-authored-by`). For Claude Code, enable this via the
  [attribution settings](https://code.claude.com/docs/en/settings#attribution-settings).
