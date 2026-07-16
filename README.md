# jBiDiB Simulation Demo

A Maven project demonstrating [jbidibc](https://github.com/bidib/jbidibc) simulation using the `StartMacro` command.

> **Note:** This project requires `2.0-SNAPSHOT` versions of `jbidibc-tools`, `bidibwizard-core`, and `bidibwizard-simulation` (from Sonatype snapshots repository). The fixes needed are not yet released, so snapshot versions are mandatory.

## Prerequisites

- Java 11+
- Maven 3.6+

## Build

```sh
mvn clean package
```

## Run

```sh
java -jar target/jbidibc-simulation-demo-1.0-SNAPSHOT.jar \
    -port=sim \
    -nodeId=0x45000D7F000E94 \
    -macro=0 \
    -simFile=/simulation-demo/simulation.xml
```

### Parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `-port` | yes | Port name (use `sim` for simulation) |
| `-nodeId` | yes | Target node unique ID |
| `-macro` | yes | Macro number to execute |
| `-simFile` | no | Simulation XML path (default: `/simulation.xml`) |

### SetAccessoryAspectSimulation

Additional parameters for the `SetAccessoryAspectSimulation` command:

| Parameter | Required | Description |
|-----------|----------|-------------|
| `-accessory` | yes | Accessory number to control |
| `-aspect` | yes | Aspect number to set |

Run:

```sh
java -jar target/jbidibc-simulation-demo-1.0-SNAPSHOT.jar \
    -port=sim \
    -nodeId=0x45000D7F000E94 \
    -accessory=0 \
    -aspect=1 \
    -simFile=/simulation-demo/simulation.xml
```

## Test

```sh
mvn test
```

Tests use [AssertJ](https://assertj.github.io/doc/) for assertions and cover both `StartMacroSimulation` and `SetAccessoryAspectSimulation` (including an error-simulation case on accessory 1 / aspect 1).

## Simulation Config

The simulation setup is defined in `src/main/resources/simulation-demo/simulation.xml`.
It uses `IF2Simulator` as the master node with a `LedIo24Simulator` subnode.

## Simulator Auto-Discovery

The application uses `BidibNodeSimulatorScanner` to auto-discover simulator classes by vid/pid mapping at startup, so manual registration of simulator implementations is not required.
