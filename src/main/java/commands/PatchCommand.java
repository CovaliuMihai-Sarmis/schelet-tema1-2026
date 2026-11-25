package commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.CommandInput;
import fileio.MarketDataInput;
import start_simulation.Simulation;
import structured_files.Interval;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class PatchCommand extends ExecuteCommand {
    public PatchCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public void execute(final Simulation simulation, final ArrayNode output) {
        if (input.getTimestamp() != null) {
            simulation.setCurrentTime(LocalDateTime.parse(input.getTimestamp()));
        }

        if (input.getParameters() == null || input.getParameters().getData() == null) {
            return;
        }

        String dataType = input.getParameters().getDataType();
        List<MarketDataInput> dataList = input.getParameters().getData();

        for (MarketDataInput item : dataList) {
            if (item == null || item.id() == null
                    || item.date() == null || item.interval() == null) {
                continue;
            }
            if (dataType != null && !item.id().equals(dataType)) {
                continue;
            }

            LocalTime start;
            LocalTime end;
            try {
                LocalDate.parse(item.date());
                String[] parts = item.interval().split("-");
                if (parts.length != 2) {
                    continue;
                }
                String startStr = parts[0].trim();
                String endStr = parts[1].trim();

                start = LocalTime.parse(startStr);
                end = endStr.equals("24:00") ? LocalTime.MIDNIGHT : LocalTime.parse(endStr);
            } catch (Exception e) {
                continue;
            }

            if (start.getMinute() % 15 != 0 || !start.plusMinutes(15).equals(end)) {
                continue;
            }

            int index = (start.getHour() * 4) + (start.getMinute() / 15) + 1;
            Interval valid = IntervalValidator.validateAndBuild(
                    item, index, simulation, "patch", input.getTimestamp(), output);

            if (valid != null) {
                simulation.getRepository().update(item.id(), item.date(), valid);
            }
        }
    }
}