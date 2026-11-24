package structured_files;

public class DamPricePredictionInterval extends Interval {
    private Double predictedPriceEurMwh;
    private Double spikeProbability;

    public Double getPredictedPriceEurMwh() {
        return predictedPriceEurMwh;
    }

    public void setPredictedPriceEurMwh(Double predictedPriceEurMwh) {
        this.predictedPriceEurMwh = predictedPriceEurMwh;
    }

    public Double getSpikeProbability() {
        return spikeProbability;
    }

    public void setSpikeProbability(Double spikeProbability) {
        this.spikeProbability = spikeProbability;
    }
}