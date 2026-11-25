package start_simulation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import commands.ExecuteCommand;
import commands.ReturnObjectFromCommand;
import fileio.CommandInput;
import repository.MarketDataRepository;

import java.time.LocalDateTime;
import java.util.List;

public final class Simulation {
    private final MarketDataRepository repository = new MarketDataRepository();
    private boolean isImporting = false;
    private LocalDateTime currentTime;

    private void updateClock(final String timestamp) {
        if (timestamp != null) {
            this.currentTime = LocalDateTime.parse(timestamp);
        }
    }

    public void run(final List<CommandInput> commands, final ArrayNode output) {
        for (CommandInput cmdInput : commands) {
            updateClock(cmdInput.getTimestamp());
            ExecuteCommand command = ReturnObjectFromCommand.createCommand(cmdInput);
            command.execute(this, output);
        }
    }

    public MarketDataRepository getRepository() {
        return repository;
    }

    public boolean isImporting() {
        return isImporting;
    }

    public void setImporting(final boolean importing) {
        this.isImporting = importing;
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(final LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }
}