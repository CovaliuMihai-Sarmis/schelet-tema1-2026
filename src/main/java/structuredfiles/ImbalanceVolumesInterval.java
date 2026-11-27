package structuredfiles;

public final class ImbalanceVolumesInterval extends Interval {
    private Double positiveImbalanceVol;
    private Double negativeImbalanceVol;

    public Double getPositiveImbalanceVol() {
        return positiveImbalanceVol;
    }

    public void setPositiveImbalanceVol(final Double positiveImbalanceVol) {
        this.positiveImbalanceVol = positiveImbalanceVol;
    }

    public Double getNegativeImbalanceVol() {
        return negativeImbalanceVol;
    }

    public void setNegativeImbalanceVol(final Double negativeImbalanceVol) {
        this.negativeImbalanceVol = negativeImbalanceVol;
    }

    @Override
    public boolean hasData() {
        return positiveImbalanceVol != null && negativeImbalanceVol != null;
    }
}
