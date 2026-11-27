package structuredfiles;

public final class GridLoadInterval extends Interval {
    private Double quantity;
    private Double reservesRequiredMW;

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(final Double quantity) {
        this.quantity = quantity;
    }

    public Double getReservesRequiredMW() {
        return reservesRequiredMW;
    }

    public void setReservesRequiredMW(final Double reservesRequiredMW) {
        this.reservesRequiredMW = reservesRequiredMW;
    }

    @Override
    public boolean hasData() {
        return quantity != null;
    }
}
