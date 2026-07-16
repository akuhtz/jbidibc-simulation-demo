package org.bidib.jbidibc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

class SetAccessoryAspectSimulationTest {

    @Test
    void testExecuteWithSimulation() {
        String[] args = { "-port=sim", "-nodeId=0x45000D7F000E94", "-accessory=0", "-aspect=1", "-simFile=/simulation-demo/simulation.xml" };
        SetAccessoryAspectSimulation command = new SetAccessoryAspectSimulation();
        int exitCode = new CommandLine(command).execute(args);
        Assertions.assertThat(exitCode).isEqualTo(0).as("Expected exit code 0, but got " + exitCode);
    }

    @Test
    void testExecuteWithSimulationFail() {

        // the LedIo24Simulator has an error simulation on accessory 1 and aspect 1

        String[] args = { "-port=sim", "-nodeId=0x45000D7F000E94", "-accessory=1", "-aspect=1", "-simFile=/simulation-demo/simulation.xml" };
        SetAccessoryAspectSimulation command = new SetAccessoryAspectSimulation();
        int exitCode = new CommandLine(command).execute(args);
        Assertions.assertThat(exitCode).isEqualTo(-2).as("Expected exit code -2, but got " + exitCode);
    }
}
