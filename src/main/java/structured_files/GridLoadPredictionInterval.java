package structured_files;

public class GridLoadPredictionInterval extends Interval {
    private Double quantity;
    private Double reservesRequiredMW;

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getReservesRequiredMW() {
        return reservesRequiredMW;
    }

    public void setReservesRequiredMW(Double reservesRequiredMW) {
        this.reservesRequiredMW = reservesRequiredMW;
    }

    @Override
    public boolean hasData() {
        return quantity != null && reservesRequiredMW != null;
    }
}
