package commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.CommandInput;
import start_simulation.Simulation;

public abstract class ExecuteCommand {
    protected final CommandInput input;

    protected ExecuteCommand(final CommandInput input) {
        this.input = input;
    }

    public abstract void execute(final Simulation simulation,
                                 final ArrayNode output);
}