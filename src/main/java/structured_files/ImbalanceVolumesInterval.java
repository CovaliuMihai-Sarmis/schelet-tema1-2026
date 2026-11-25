package structured_files;

public class ImbalanceVolumesInterval extends Interval {
    private Double positiveImbalanceVol;
    private Double negativeImbalanceVol;

    public Double getPositiveImbalanceVol() {
        return positiveImbalanceVol;
    }

    public void setPositiveImbalanceVol(Double positiveImbalanceVol) {
        this.positiveImbalanceVol = positiveImbalanceVol;
    }

    public Double getNegativeImbalanceVol() {
        return negativeImbalanceVol;
    }

    public void setNegativeImbalanceVol(Double negativeImbalanceVol) {
        this.negativeImbalanceVol = negativeImbalanceVol;
    }

    @Override
    public boolean hasData() {
        return positiveImbalanceVol != null && negativeImbalanceVol != null;
    }
}
