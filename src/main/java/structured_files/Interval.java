package structured_files;

public abstract class Interval {
    private Integer intervalIndex;

    public Integer getIntervalIndex() {
        return intervalIndex;
    }

    public void setIntervalIndex(Integer intervalIndex) {
        this.intervalIndex = intervalIndex;
    }

    public abstract boolean hasData();
}