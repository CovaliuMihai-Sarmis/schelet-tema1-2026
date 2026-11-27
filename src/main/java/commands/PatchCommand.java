package commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.CommandInput;
import fileio.MarketDataInput;
import startsimulation.Simulation;
import structuredfiles.Interval;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class PatchCommand extends ExecuteCommand {
    private static final int INTERVAL_MINUTES = 15;
    private static final int INTERVALS_PER_HOUR = 4;

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

            if (start.getMinute() % INTERVAL_MINUTES != 0
                    || !start.plusMinutes(INTERVAL_MINUTES).equals(end)) {
                continue;
            }

            int index = (start.getHour() * INTERVALS_PER_HOUR)
                    + (start.getMinute() / INTERVAL_MINUTES) + 1;
            Interval valid = IntervalValidator.validateAndBuild(
                    item, index, simulation, "patch", input.getTimestamp(), output);

            if (valid != null) {
                simulation.getRepository().update(item.id(), item.date(), valid);
            }
        }
    }
}