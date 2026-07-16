package org.bidib.jbidibc;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.bidib.jbidibc.core.BidibMessageProcessor;
import org.bidib.jbidibc.core.DefaultMessageListener;
import org.bidib.jbidibc.core.node.AccessoryNode;
import org.bidib.jbidibc.messages.AccessoryState;
import org.bidib.jbidibc.messages.AccessoryStateOptions;
import org.bidib.jbidibc.messages.Node;
import org.bidib.jbidibc.messages.helpers.Context;
import org.bidib.jbidibc.messages.helpers.DefaultContext;
import org.bidib.jbidibc.messages.utils.ByteUtils;
import org.bidib.jbidibc.simulation.SimulationInterface;
import org.bidib.jbidibc.simulation.annotation.BidibVidPid;
import org.bidib.wizard.core.utils.BidibNodeSimulatorScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;

@Command
public class SetAccessoryAspectSimulation extends BidibNodeCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetAccessoryAspectSimulation.class);

    @picocli.CommandLine.Option(names = { "-accessory" }, description = "The accessory number", required = true)
    private int accessoryNumber;

    @picocli.CommandLine.Option(names = { "-aspect" }, description = "The aspect number to set", required = true)
    private int aspectNumber;

    @picocli.CommandLine.Option(names = {
        "-simFile" }, description = "The path to the simulation file. If not specified it looks for simulation.xml", required = false)
    private String simulationFile = "/simulation.xml";

    public static void main(String[] args) {
        run(new SetAccessoryAspectSimulation(), args);
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

                    final AccessoryStateHelper accessoryStateHelper = new AccessoryStateHelper();

                    final BidibMessageProcessor bidibMessageProcessor = getBidib().getBidibMessageProcessor();
                    if (bidibMessageProcessor != null) {

                        bidibMessageProcessor.addMessageListener(new DefaultMessageListener() {

                            @Override
                            public void accessoryState(
                                byte[] address, int messageNum, final AccessoryState accessoryState, final AccessoryStateOptions accessoryStateOptions) {
                                LOGGER.info("Received current accessory state: {}, accessoryStateOptions: {}", accessoryState, accessoryStateOptions);
                                accessoryStateHelper.setAccessoryState(accessoryState);
                                accessoryStateHelper.setAccessoryStateOptions(accessoryStateOptions);

                                if (ByteUtils.getInt(accessoryState.getExecute()) == 1) {
                                    LOGGER.info("End position not yet reached.");
                                }
                                else {
                                    synchronized (accessoryStateHelper) {
                                        accessoryStateHelper.notifyAll();
                                    }
                                }
                            }

                            @Override
                            public void error(byte[] address, int messageNum, int errorCode, byte[] reasonData) {
                                LOGGER.warn("Received an error! Current errorCode: {},  reasonData: {}", errorCode, ByteUtils.bytesToHex(reasonData));

                                synchronized (accessoryStateHelper) {
                                    accessoryStateHelper.notifyAll();
                                }
                            }
                        });
                    }

                    System.out.println("Set the aspect on accessory: " + accessoryNumber + ", aspect: " + aspectNumber);
                    accessoryNode.setAccessoryState(accessoryNumber, aspectNumber);

                    // wait for repsonse
                    synchronized (accessoryStateHelper) {
                        LOGGER.info("Wait for response.");
                        if (accessoryStateHelper.getAccessoryState() == null) {
                            accessoryStateHelper.wait(3000L);
                        }
                    }

                    LOGGER
                        .info("Current accessory state: {}, accessoryStateOptions: {}", accessoryStateHelper.getAccessoryState(),
                            accessoryStateHelper.getAccessoryStateOptions());

                    if (accessoryStateHelper.getAccessoryState() == null) {
                        LOGGER.warn("No accessory state received!");
                        return -1;
                    }
                    else if (ByteUtils.getInt(accessoryStateHelper.getAccessoryState().getExecute()) == 128) {
                        LOGGER.warn("The accessory state signalled an error!");
                        return -2;
                    }

                    result = 0;
                }
                else {
                    System.err.println("node with unique id \"" + getNodeIdentifier() + "\" doesn't have accessories");
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
        URL resource = SetAccessoryAspectSimulation.class.getResource(simulationFile);
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

    private static final class AccessoryStateHelper {

        private AccessoryState accessoryState;

        private AccessoryStateOptions accessoryStateOptions;

        private Object lock = new Object();

        public AccessoryState getAccessoryState() {
            synchronized (lock) {
                return accessoryState;
            }
        }

        public void setAccessoryState(AccessoryState accessoryState) {
            synchronized (lock) {
                this.accessoryState = accessoryState;
            }
        }

        public AccessoryStateOptions getAccessoryStateOptions() {
            synchronized (lock) {
                return accessoryStateOptions;
            }
        }

        public void setAccessoryStateOptions(AccessoryStateOptions accessoryStateOptions) {
            synchronized (lock) {
                this.accessoryStateOptions = accessoryStateOptions;
            }
        }
    }
}
