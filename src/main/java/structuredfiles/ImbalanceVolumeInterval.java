package structuredfiles;

public class ImbalanceVolumeInterval extends Interval {
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
}
