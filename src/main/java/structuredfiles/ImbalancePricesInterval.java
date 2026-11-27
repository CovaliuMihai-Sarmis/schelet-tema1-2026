package structuredfiles;

public final class ImbalancePricesInterval extends Interval {
    private Double positiveImbalancePrice;
    private Double negativeImbalancePrice;

    public Double getPositiveImbalancePrice() {
        return positiveImbalancePrice;
    }

    public void setPositiveImbalancePrice(final Double positiveImbalancePrice) {
        this.positiveImbalancePrice = positiveImbalancePrice;
    }

    public Double getNegativeImbalancePrice() {
        return negativeImbalancePrice;
    }

    public void setNegativeImbalancePrice(final Double negativeImbalancePrice) {
        this.negativeImbalancePrice = negativeImbalancePrice;
    }

    @Override
    public boolean hasData() {
        return positiveImbalancePrice != null && negativeImbalancePrice != null;
    }
}
