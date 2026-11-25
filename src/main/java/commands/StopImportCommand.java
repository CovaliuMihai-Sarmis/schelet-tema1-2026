package commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.CommandInput;
import start_simulation.Simulation;
import java.time.LocalDateTime;

public final class StopImportCommand extends ExecuteCommand {

    public StopImportCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public void execute(final Simulation simulation, final ArrayNode output) {
        if (input.getTimestamp() != null) {
            simulation.setCurrentTime(LocalDateTime.parse(input.getTimestamp()));
        }
        simulation.setImporting(false);
    }
}