package structuredfiles;

public abstract class Interval {
    private Integer intervalIndex;

    public final Integer getIntervalIndex() {
        return intervalIndex;
    }

    public final void setIntervalIndex(final Integer intervalIndex) {
        this.intervalIndex = intervalIndex;
    }

    public abstract boolean hasData();
}