package structured_files;

public class DamPricesInterval extends Interval {
    private Double priceAmount;

    public Double getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(Double priceAmount) {
        this.priceAmount = priceAmount;
    }

    @Override
    public boolean hasData() {
        return priceAmount != null;
    }
}