package org.bidib.jbidibc;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.bidib.jbidibc.core.node.AccessoryNode;
import org.bidib.jbidibc.messages.Node;
import org.bidib.jbidibc.messages.enums.LcMacroOperationCode;
import org.bidib.jbidibc.messages.enums.LcMacroState;
import org.bidib.jbidibc.messages.helpers.Context;
import org.bidib.jbidibc.messages.helpers.DefaultContext;
import org.bidib.jbidibc.messages.utils.ByteUtils;
import org.bidib.jbidibc.simulation.SimulationInterface;
import org.bidib.jbidibc.simulation.annotation.BidibVidPid;
import org.bidib.wizard.core.utils.BidibNodeSimulatorScanner;

import picocli.CommandLine.Command;

@Command
public class StartMacroSimulation extends BidibNodeCommand {

    @picocli.CommandLine.Option(names = { "-macro" }, description = "The macro number", required = true)
    private int macroNumber;

    @picocli.CommandLine.Option(names = {
        "-simFile" }, description = "The path to the simulation file. If not specified it looks for simulation.xml", required = false)
    private String simulationFile = "/simulation.xml";

    public static void main(String[] args) {
        run(new StartMacroSimulation(), args);
    }

    @Override
    public Integer call() {
        int result = 20;

        try {
            openPort(getPortName(), createContext());

            Node node = findNode();

            if (node != null) {
                AccessoryNode accessoryNode = getBidib().getAccessoryNode(node);

                if (accessoryNode != null) {
                    System.out.println("Start macro: " + macroNumber);
                    LcMacroState macroState = accessoryNode.handleMacro(macroNumber, LcMacroOperationCode.START);
                    System.out.println("Start macro returned: " + macroState);

                    if (LcMacroState.RUNNING != macroState) {
                        result = ByteUtils.getInt(macroState.getType()) + 256;
                    }
                    else {
                        result = 0;
                    }
                }
                else {
                    System.err.println("node with unique id \"" + getNodeIdentifier() + "\" doesn't have macros");
                }
            }
            else {
                System.err.println("node with unique id \"" + getNodeIdentifier() + "\" not found");
            }

            getBidib().close();
        }
        catch (Exception ex) {
            System.err.println("Execute command failed: " + ex);
        }
        return result;
    }

    private Context createContext() throws URISyntaxException {
        Context ctx = new DefaultContext();
        URL resource = StartMacroSimulation.class.getResource(simulationFile);
        if (resource == null) {
            throw new RuntimeException("Simulation resource not found: " + simulationFile);
        }

        Path path = Paths.get(resource.toURI());
        ctx.register(SimulationInterface.CONTEXT_KEY_SIMULATION_FILENAME, path.toString());

        // add mapping of vid/pid to simulator classname
        final Map<BidibVidPid, String> simulatorClassMapping = BidibNodeSimulatorScanner.findSimulatorClasses();

        ctx.register(SimulationInterface.CONTEXT_KEY_SIMULATOR_CLASS_MAPPING, simulatorClassMapping);

        return ctx;
    }
}
