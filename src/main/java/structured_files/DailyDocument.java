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

    public String getBiddingZoneDomain() {
        return biddingZoneDomain;
    }

    public void setBiddingZoneDomain(String biddingZoneDomain) {
        this.biddingZoneDomain = biddingZoneDomain;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public String getPriceMeasureUnit() {
        return priceMeasureUnit;
    }

    public void setPriceMeasureUnit(String priceMeasureUnit) {
        this.priceMeasureUnit = priceMeasureUnit;
    }

    public String getQuantityMeasureUnit() {
        return quantityMeasureUnit;
    }

    public void setQuantityMeasureUnit(String quantityMeasureUnit) {
        this.quantityMeasureUnit = quantityMeasureUnit;
    }

    public List<T> getPoints() {
        return points;
    }

    public void setPoints(List<T> points) {
        this.points = points;
    }
}