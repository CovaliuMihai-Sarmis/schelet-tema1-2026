package commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CommandInput;
import fileio.CommandParametersInput;
import startsimulation.Simulation;
import structuredfiles.DamPricePredictionInterval;
import structuredfiles.DamPricesInterval;
import structuredfiles.GridLoadInterval;
import structuredfiles.GridLoadPredictionInterval;
import structuredfiles.ImbalancePricesInterval;
import structuredfiles.ImbalanceVolumesInterval;
import structuredfiles.Interval;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GetCommand extends ExecuteCommand {
    private static final int INTERVAL_MINUTES = 15;
    private static final int INTERVALS_PER_HOUR = 4;
    private static final DateTimeFormatter ISO_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public GetCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public void execute(final Simulation simulation, final ArrayNode output) {
        LocalDateTime cmdTime = LocalDateTime.parse(input.getTimestamp());
        simulation.setCurrentTime(cmdTime);

        CommandParametersInput parameters = input.getParameters();
        if (parameters == null) {
            return;
        }

        String clientTier = parameters.getClientTier();
        String dataType = parameters.getDataType();

        // 1. Calculam from si to
        LocalDateTime from;
        LocalDateTime to;
        String target = parameters.getTarget();
        if (target != null) {
            if ("LATEST".equals(target)) {
                int extraMinutes = cmdTime.getMinute() % INTERVAL_MINUTES;
                LocalDateTime defaultTo = cmdTime.minusMinutes(extraMinutes)
                        .withSecond(0).withNano(0);
                LocalDateTime defaultFrom = defaultTo.minusMinutes(INTERVAL_MINUTES);

                LocalDateTime latestStart = simulation.getRepository()
                        .findLatestIntervalStart(dataType, defaultFrom);
                if (latestStart != null) {
                    from = latestStart;
                    to = latestStart.plusMinutes(INTERVAL_MINUTES);
                } else {
                    from = defaultFrom;
                    to = defaultTo;
                }
            } else {
                from = LocalDateTime.parse(target);
                to = from.plusMinutes(INTERVAL_MINUTES);
            }
        } else {
            from = LocalDateTime.parse(parameters.getStart());
            to = LocalDateTime.parse(parameters.getEnd());
        }

        String fromStr = from.format(ISO_FMT);
        String toStr = to.format(ISO_FMT);

        if ("FREE".equals(clientTier) && "DAM_PRICE_PREDICTION".equals(dataType)) {
            ObjectNode node = output.addObject();
            node.put("command", "get");
            node.put("timestamp", input.getTimestamp());
            node.put("status", "ERROR");
            node.put("from", fromStr);
            node.put("to", toStr);
            node.put("message",
                    "Access denied: DAM_PRICE_PREDICTION requires PREMIUM subscription.");
            return;
        }

        Map<String, List<ObjectNode>> pointsByDate = new LinkedHashMap<>();

        for (LocalDateTime cur = from;
             cur.isBefore(to);
             cur = cur.plusMinutes(INTERVAL_MINUTES)) {
            String date = cur.toLocalDate().toString();
            int index = (cur.getHour() * INTERVALS_PER_HOUR)
                    + (cur.getMinute() / INTERVAL_MINUTES) + 1;

            Interval interval = simulation.getRepository().selectInterval(dataType, date, index);
            if (interval != null && interval.hasData()) {
                ObjectNode pointNode = buildPointNode(output, dataType, cur, index, interval);
                if (!pointsByDate.containsKey(date)) {
                    pointsByDate.put(date, new ArrayList<>());
                }
                pointsByDate.get(date).add(pointNode);
            }
        }

        ObjectNode response = output.addObject();
        response.put("command", "get");
        response.put("timestamp", input.getTimestamp());

        if (pointsByDate.isEmpty()) {
            response.put("status", "WARNING");
            response.put("from", fromStr);
            response.put("to", toStr);
            response.putNull("data");
            response.put("message", "Data is not yet available. Try again later.");
            return;
        }

        response.put("status", "SUCCESS");
        response.put("from", fromStr);
        response.put("to", toStr);


        ArrayNode daysArray = response.putArray("data");
        for (Map.Entry<String, List<ObjectNode>> entry : pointsByDate.entrySet()) {
            ObjectNode dayGroup = daysArray.addObject();
            dayGroup.put("id", dataType);
            dayGroup.put("date", entry.getKey());

            ArrayNode dayPoints = dayGroup.putArray("data");
            for (ObjectNode p : entry.getValue()) {
                dayPoints.add(p);
            }
        }
    }

    private ObjectNode buildPointNode(final ArrayNode output,
                                      final String dataType,
                                      final LocalDateTime slotStart,
                                      final int index,
                                      final Interval interval) {
        LocalTime s = slotStart.toLocalTime();
        LocalTime e = s.plusMinutes(INTERVAL_MINUTES);
        String intervalLabel = String.format("%02d:%02d - %02d:%02d",
                s.getHour(), s.getMinute(), e.getHour(), e.getMinute());

        ObjectNode point = output.objectNode();
        point.put("interval", intervalLabel);
        point.put("intervalIndex", index);

        if ("DAM_PRICE".equals(dataType)) {
            DamPricesInterval p = (DamPricesInterval) interval;
            point.put("priceAmount", p.getPriceAmount());
        } else if ("DAM_PRICE_PREDICTION".equals(dataType)) {
            DamPricePredictionInterval p = (DamPricePredictionInterval) interval;
            point.put("predictedPriceEurMwh", p.getPredictedPriceEurMwh());
            point.put("spikeProbability", p.getSpikeProbability());
        } else if ("GRID_LOAD".equals(dataType)) {
            GridLoadInterval p = (GridLoadInterval) interval;
            point.put("quantity", p.getQuantity());
        } else if ("GRID_LOAD_PREDICTION".equals(dataType)) {
            GridLoadPredictionInterval p = (GridLoadPredictionInterval) interval;
            point.put("quantity", p.getQuantity());
        } else if ("IMBALANCE_PRICE".equals(dataType)) {
            ImbalancePricesInterval p = (ImbalancePricesInterval) interval;
            point.put("positiveImbalancePrice", p.getPositiveImbalancePrice());
            point.put("negativeImbalancePrice", p.getNegativeImbalancePrice());
        } else if ("IMBALANCE_VOLUME".equals(dataType)) {
            ImbalanceVolumesInterval p = (ImbalanceVolumesInterval) interval;
            point.put("positiveImbalanceVol", p.getPositiveImbalanceVol());
            point.put("negativeImbalanceVol", p.getNegativeImbalanceVol());
        }

        return point;
    }
}
