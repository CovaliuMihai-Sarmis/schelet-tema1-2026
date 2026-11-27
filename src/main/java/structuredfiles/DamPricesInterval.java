package structuredfiles;

public final class DamPricesInterval extends Interval {
    private Double priceAmount;

    public Double getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(final Double priceAmount) {
        this.priceAmount = priceAmount;
    }

    @Override
    public boolean hasData() {
        return priceAmount != null;
    }
}