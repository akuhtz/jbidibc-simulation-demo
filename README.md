# jBiDiB Simulation Demo

A Maven project demonstrating [jbidibc](https://github.com/bidib/jbidibc) simulation using the `StartMacro` command.

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

## Test

```sh
mvn test
```

## Simulation Config

The simulation setup is defined in `src/main/resources/simulation-demo/simulation.xml`.
It uses `IF2Simulator` as the master node with a `LedIo24Simulator` subnode.
