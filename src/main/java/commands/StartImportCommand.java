package commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import startsimulation.Simulation;
import fileio.CommandInput;
import java.time.LocalDateTime;

public final class StartImportCommand extends ExecuteCommand {
    public StartImportCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public void execute(final Simulation simulation, final ArrayNode output) {
        if (input.getTimestamp() != null) {
            simulation.setCurrentTime(LocalDateTime.parse(input.getTimestamp()));
        }
        simulation.setImporting(true);
    }
}
