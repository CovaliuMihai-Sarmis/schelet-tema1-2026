package commands;

import fileio.CommandInput;

public final class ReturnObjectFromCommand {
    private ReturnObjectFromCommand() {
    }

    public static ExecuteCommand createCommand(final CommandInput input) {
        final String commandName = input.getCommand();

        switch (commandName) {
            case "startImport":
                return new StartImportCommand(input);
            case "stopImport":
                return new StopImportCommand(input);
            case "import":
                return new ImportCommand(input);
            case "patch":
                return new PatchCommand(input);
            case "get":
                return new GetCommand(input);
            default:
                throw new IllegalArgumentException("Unknown command: " + commandName);
        }
    }
}