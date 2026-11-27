package structuredfiles;

public final class DamPricePredictionInterval extends Interval {
    private Double predictedPriceEurMwh;
    private Double spikeProbability;

    public Double getPredictedPriceEurMwh() {
        return predictedPriceEurMwh;
    }

    public void setPredictedPriceEurMwh(final Double predictedPriceEurMwh) {
        this.predictedPriceEurMwh = predictedPriceEurMwh;
    }

    public Double getSpikeProbability() {
        return spikeProbability;
    }

    public void setSpikeProbability(final Double spikeProbability) {
        this.spikeProbability = spikeProbability;
    }

    @Override
    public boolean hasData() {
        return predictedPriceEurMwh != null && spikeProbability != null;
    }
}