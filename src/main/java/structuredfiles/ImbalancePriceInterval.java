package structuredfiles;

public class ImbalancePriceInterval extends Interval {
    private Double positiveImbalancePrice;
    private Double negativeImbalancePrice;

    public Double getPositiveImbalancePrice() {
        return positiveImbalancePrice;
    }

    public void setPositiveImbalancePrice(Double positiveImbalancePrice) {
        this.positiveImbalancePrice = positiveImbalancePrice;
    }

    public Double getNegativeImbalancePrice() {
        return negativeImbalancePrice;
    }

    public void setNegativeImbalancePrice(Double negativeImbalancePrice) {
        this.negativeImbalancePrice = negativeImbalancePrice;
    }
}
