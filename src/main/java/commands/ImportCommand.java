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

public final class ImportCommand extends ExecuteCommand {
    public ImportCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public void execute(final Simulation simulation, final ArrayNode output) {
        LocalDateTime cmdTime = LocalDateTime.parse(input.getTimestamp());
        simulation.setCurrentTime(cmdTime);

        if (!simulation.isImporting()) {
            return;
        }

        if (input.getParameters() == null || input.getParameters().getData() == null) {
            return;
        }

        List<MarketDataInput> dataList = input.getParameters().getData();

        for (MarketDataInput item : dataList) {
            if (item == null || item.id() == null
                    || item.date() == null || item.interval() == null) {
                continue;
            }

            LocalDate date;
            LocalTime start;
            LocalTime end;
            try {
                date = LocalDate.parse(item.date());
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

//            LocalDateTime slotStart = LocalDateTime.of(date, start);
//            if (!slotStart.equals(cmdTime.minusMinutes(15))) {
//                continue;
//            }

            int index = (start.getHour() * 4) + (start.getMinute() / 15) + 1;
            Interval valid = IntervalValidator.validateAndBuild(
                    item, index, simulation, "import", input.getTimestamp(), output);

            if (valid != null) {
                simulation.getRepository().insert(item.id(), item.date(), valid);
            } else {
                simulation.getRepository().clearInterval(item.id(), item.date(), index);
            }
        }
    }
}