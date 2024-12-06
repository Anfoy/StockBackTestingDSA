public class TradingDay {

    private final String date;

    private final double open;
    private final double high;
    private final double low;

    private final double close;
    private final double volume;
    public TradingDay(String date, double open, double high, double low, double close, double volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public String getDate() {
        return date;
    }

    public double getVolume() {
        return volume;
    }

    public double getClose() {
        return close;
    }

    public double getLow() {
        return low;
    }

    public double getHigh() {
        return high;
    }

    public double getOpen() {
        return open;
    }
}
