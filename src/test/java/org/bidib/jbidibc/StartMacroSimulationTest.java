package org.bidib.jbidibc;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;

class StartMacroSimulationTest {

    @Test
    void testExecuteWithSimulation() {
        String[] args = { "-port=sim", "-nodeId=0x45000D7F000E94", "-macro=0", "-simFile=/simulation-demo/simulation.xml" };
        StartMacroSimulation command = new StartMacroSimulation();
        int exitCode = new CommandLine(command).execute(args);
        assert exitCode == 0 : "Expected exit code 0, but got " + exitCode;
    }
}
