package commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.CommandInput;
import startsimulation.Simulation;

public abstract class ExecuteCommand {
    protected final CommandInput input;

    protected ExecuteCommand(final CommandInput input) {
        this.input = input;
    }

    /**
     * Executes this command and appends any result to the output.
     *
     * @param simulation simulation state used by the command
     * @param output output array receiving command results
     */
    public abstract void execute(Simulation simulation, ArrayNode output);
}
