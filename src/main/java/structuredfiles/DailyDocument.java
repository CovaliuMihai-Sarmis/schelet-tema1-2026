package structuredfiles;
import java.util.List;

public abstract class DailyDocument<T> {
    private String biddingZoneDomain;
    private String periodStart;
    private String periodEnd;
    private String resolution;
    private String currencyUnit;
    private String priceMeasureUnit;
    private String quantityMeasureUnit;

    private List<T> points;

    public final String getBiddingZoneDomain() {
        return biddingZoneDomain;
    }

    public final void setBiddingZoneDomain(final String biddingZoneDomain) {
        this.biddingZoneDomain = biddingZoneDomain;
    }

    public final String getPeriodStart() {
        return periodStart;
    }

    public final void setPeriodStart(final String periodStart) {
        this.periodStart = periodStart;
    }

    public final String getPeriodEnd() {
        return periodEnd;
    }

    public final void setPeriodEnd(final String periodEnd) {
        this.periodEnd = periodEnd;
    }

    public final String getResolution() {
        return resolution;
    }

    public final void setResolution(final String resolution) {
        this.resolution = resolution;
    }

    public final String getCurrencyUnit() {
        return currencyUnit;
    }

    public final void setCurrencyUnit(final String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public final String getPriceMeasureUnit() {
        return priceMeasureUnit;
    }

    public final void setPriceMeasureUnit(final String priceMeasureUnit) {
        this.priceMeasureUnit = priceMeasureUnit;
    }

    public final String getQuantityMeasureUnit() {
        return quantityMeasureUnit;
    }

    public final void setQuantityMeasureUnit(final String quantityMeasureUnit) {
        this.quantityMeasureUnit = quantityMeasureUnit;
    }

    public final List<T> getPoints() {
        return points;
    }

    public final void setPoints(final List<T> points) {
        this.points = points;
    }
}