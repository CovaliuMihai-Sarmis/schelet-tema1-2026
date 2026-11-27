package commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.MarketDataInput;
import startsimulation.Simulation;
import structuredfiles.DamPricePredictionInterval;
import structuredfiles.DamPricesInterval;
import structuredfiles.GridLoadInterval;
import structuredfiles.GridLoadPredictionInterval;
import structuredfiles.ImbalancePricesInterval;
import structuredfiles.ImbalanceVolumesInterval;
import structuredfiles.Interval;

public final class IntervalValidator {
    private static final double MIN_DAM_PRICE = -500.0;
    private static final double MAX_DAM_PRICE = 4000.0;
    private static final double MAX_GRID_LOAD_DIFF = 10000.0;
    private static final double MAX_IMBALANCE_PRICE = 15000.0;

    private IntervalValidator() {
    }

    private static void addWarning(final ArrayNode output,
                                   final String commandName,
                                   final String timestamp,
                                   final MarketDataInput item,
                                   final int index,
                                   final String message) {
        ObjectNode warning = output.addObject();
        warning.put("command", commandName);
        warning.put("timestamp", timestamp);
        warning.put("status", "WARNING");
        warning.put("id", item.id());
        warning.put("date", item.date());
        warning.put("interval", item.interval().replaceAll("\\s*-\\s*", " - "));
        warning.put("intervalIndex", index);
        warning.put("message", message);
    }

    public static Interval validateAndBuild(final MarketDataInput item,
                                            final int index,
                                            final Simulation simulation,
                                            final String commandName,
                                            final String timestamp,
                                            final ArrayNode output) {
        String id = item.id();

        if ("DAM_PRICE".equals(id)) {
            if (item.priceAmount() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "PriceAmount is null; interval skipped.");
                return null;
            }
            if (item.priceAmount() < MIN_DAM_PRICE || item.priceAmount() > MAX_DAM_PRICE) {
                addWarning(output, commandName, timestamp, item, index,
                        "PriceAmount " + item.priceAmount()
                                + " is outside the market range [-500, 4000] EUR/MWh; interval skipped.");
                return null;
            }
            DamPricesInterval p = new DamPricesInterval();
            p.setIntervalIndex(index);
            p.setPriceAmount(item.priceAmount());
            return p;
        }

        if ("DAM_PRICE_PREDICTION".equals(id)) {
            if (item.predictedPriceEurMwh() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "PredictedPriceEurMwh is null; interval skipped.");
                return null;
            }
            if (item.predictedPriceEurMwh() < MIN_DAM_PRICE
                    || item.predictedPriceEurMwh() > MAX_DAM_PRICE) {
                addWarning(output, commandName, timestamp, item, index,
                        "PredictedPriceEurMwh " + item.predictedPriceEurMwh()
                                + " is outside the market range [-500, 4000] EUR/MWh; interval skipped.");
                return null;
            }
            if (item.spikeProbability() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "SpikeProbability is null; interval skipped.");
                return null;
            }
            if (item.spikeProbability() < 0.0 || item.spikeProbability() > 1.0) {
                addWarning(output, commandName, timestamp, item, index,
                        "SpikeProbability " + item.spikeProbability()
                                + " is outside the valid range [0.0, 1.0]; interval skipped.");
                return null;
            }
            DamPricePredictionInterval p = new DamPricePredictionInterval();
            p.setIntervalIndex(index);
            p.setPredictedPriceEurMwh(item.predictedPriceEurMwh());
            p.setSpikeProbability(item.spikeProbability());
            return p;
        }

        if ("GRID_LOAD".equals(id)) {
            if (item.quantity() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "Quantity is null; interval skipped.");
                return null;
            }
            double val = item.quantity().doubleValue();
            if (val < 0) {
                addWarning(output, commandName, timestamp, item, index,
                        "Quantity " + val
                                + " is below the minimum allowed value 0; interval skipped.");
                return null;
            }
            Interval prev = simulation.getRepository()
                    .findPreviousInterval("GRID_LOAD", item.date(), index);
            if (prev instanceof GridLoadInterval gridPrev
                    && Math.abs(val - gridPrev.getQuantity()) > MAX_GRID_LOAD_DIFF) {
                addWarning(output, commandName, timestamp, item, index,
                        "Quantity change exceeds 10000 MW compared with the previous interval; interval skipped.");
                return null;
            }
            GridLoadInterval p = new GridLoadInterval();
            p.setIntervalIndex(index);
            p.setQuantity(val);
            return p;
        }

        if ("GRID_LOAD_PREDICTION".equals(id)) {
            if (item.quantity() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "Quantity is null; interval skipped.");
                return null;
            }
            double val = item.quantity().doubleValue();
            if (val < 0) {
                addWarning(output, commandName, timestamp, item, index,
                        "Quantity " + val
                                + " is below the minimum allowed value 0; interval skipped.");
                return null;
            }
            if (item.reservesRequiredMW() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "ReservesRequiredMW is null; interval skipped.");
                return null;
            }
            if (item.reservesRequiredMW() <= 0) {
                addWarning(output, commandName, timestamp, item, index,
                        "ReservesRequiredMW " + item.reservesRequiredMW()
                                + " is not positive; interval skipped.");
                return null;
            }
            GridLoadPredictionInterval p = new GridLoadPredictionInterval();
            p.setIntervalIndex(index);
            p.setQuantity(val);
            p.setReservesRequiredMW(item.reservesRequiredMW());
            return p;
        }

        if ("IMBALANCE_PRICE".equals(id)) {
            if (item.positiveImbalancePrice() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "PositiveImbalancePrice is null; interval skipped.");
                return null;
            }
            if (Math.abs(item.positiveImbalancePrice()) > MAX_IMBALANCE_PRICE) {
                addWarning(output, commandName, timestamp, item, index,
                        "PositiveImbalancePrice " + item.positiveImbalancePrice()
                                + " exceeds the maximum allowed value 15000 EUR/MWh; interval skipped.");
                return null;
            }
            if (item.negativeImbalancePrice() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "NegativeImbalancePrice is null; interval skipped.");
                return null;
            }
            if (Math.abs(item.negativeImbalancePrice()) > MAX_IMBALANCE_PRICE) {
                addWarning(output, commandName, timestamp, item, index,
                        "NegativeImbalancePrice " + item.negativeImbalancePrice()
                                + " exceeds the maximum allowed value 15000 EUR/MWh; interval skipped.");
                return null;
            }
            ImbalancePricesInterval p = new ImbalancePricesInterval();
            p.setIntervalIndex(index);
            p.setPositiveImbalancePrice(item.positiveImbalancePrice());
            p.setNegativeImbalancePrice(item.negativeImbalancePrice());
            return p;
        }

        if ("IMBALANCE_VOLUME".equals(id)) {
            if (item.positiveImbalanceVol() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "PositiveImbalanceVol is null; interval skipped.");
                return null;
            }
            if (item.positiveImbalanceVol() < 0) {
                addWarning(output, commandName, timestamp, item, index,
                        "PositiveImbalanceVol " + item.positiveImbalanceVol()
                                + " is below the minimum allowed value 0; interval skipped.");
                return null;
            }
            if (item.negativeImbalanceVol() == null) {
                addWarning(output, commandName, timestamp, item, index,
                        "NegativeImbalanceVol is null; interval skipped.");
                return null;
            }
            if (item.negativeImbalanceVol() < 0) {
                addWarning(output, commandName, timestamp, item, index,
                        "NegativeImbalanceVol " + item.negativeImbalanceVol()
                                + " is below the minimum allowed value 0; interval skipped.");
                return null;
            }
            ImbalanceVolumesInterval p = new ImbalanceVolumesInterval();
            p.setIntervalIndex(index);
            p.setPositiveImbalanceVol(item.positiveImbalanceVol());
            p.setNegativeImbalanceVol(item.negativeImbalanceVol());
            return p;
        }

        return null;
    }


}