import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Stock {

    private final String symbol;
    private final List<TradingDay> tradingDays;
    private final CSVReader reader;

    public Stock(String symbol, String filePath) {
        this.symbol = symbol;
        this.reader = new CSVReader(filePath);
        reader.readCSV();
        this.tradingDays = reader.getTradingDays();
    }

    public String getSymbol() {
        return symbol;
    }

    public List<TradingDay> getTradingDays() {
        return tradingDays;
    }

    public double calculateRSI(TradingDay currentDay, int period) {
        int currentIndex = tradingDays.indexOf(currentDay);

        // Ensure we have enough data for RSI calculation
        if (currentIndex < period) {
            return 0.0; // Not enough data points
        }

        double avgGain = 0;
        double avgLoss = 0;

        // Calculate gains and losses
        for (int i = currentIndex - period + 1; i <= currentIndex; i++) {
            double change = tradingDays.get(i).getClose() - tradingDays.get(i - 1).getClose();
            avgGain += Math.max(change, 0); // Positive change
            avgLoss += Math.max(-change, 0); // Negative change
        }

        avgGain /= period;
        avgLoss /= period;

        // Handle division by zero
        if (avgLoss == 0) {
            return 100;
        }

        double rs = avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }

    public double calculateEMA(TradingDay currentDay, int period) {
        int currentIndex = tradingDays.indexOf(currentDay);

        // Ensure there is enough data
        if (currentIndex < period - 1) {
            return 0.0;
        }

        // Extract closing prices for the period
        List<Double> closingPrices = tradingDays.subList(currentIndex - period + 1, currentIndex + 1)
                .stream()
                .map(TradingDay::getClose)
                .toList();

        double alpha = 2.0 / (period + 1);
        double ema = closingPrices.get(0);

        // Dynamically calculate EMA
        for (int i = 1; i < closingPrices.size(); i++) {
            ema = (closingPrices.get(i) * alpha) + (ema * (1 - alpha));
        }

        return ema;
    }



//    public void exportAllRSIToCSV(int period, String filePath) {
//        if (tradingDays.size() < period) {
//            System.out.println("Not enough data to calculate RSI.");
//            return;
//        }
//
//        try (FileWriter writer = new FileWriter(filePath)) {
//            writer.write("Date,RSI\n");
//
//            for (int i = period; i < tradingDays.size(); i++) {
//                TradingDay currentDay = tradingDays.get(i);
//                double rsi = calculateRSI(currentDay, period);
//                writer.write(currentDay.getDate() + "," + rsi + "\n");
//            }
//
//            System.out.println("RSI values exported to " + filePath);
//        } catch (IOException e) {
//            System.err.println("Error writing to CSV file: " + e.getMessage());
//        }
//    }

//    public void exportRollingAverageToCSV(int windowSize, String filePath) {
//        if (tradingDays.size() < windowSize) {
//            System.out.println("Not enough data points to calculate rolling average.");
//            return;
//        }
//
//        try (FileWriter writer = new FileWriter(filePath)) {
//            writer.write("Date,Rolling Average\n");
//
//            double sum = 0;
//            for (int i = 0; i < tradingDays.size(); i++) {
//                sum += tradingDays.get(i).getClose();
//
//                if (i >= windowSize) {
//                    sum -= tradingDays.get(i - windowSize).getClose();
//                }
//
//                if (i >= windowSize - 1) {
//                    double rollingAverage = sum / windowSize;
//                    writer.write(tradingDays.get(i).getDate() + "," + rollingAverage + "\n");
//                }
//            }
//
//            System.out.println("Rolling averages exported to " + filePath);
//        } catch (IOException e) {
//            System.err.println("Error writing to CSV file: " + e.getMessage());
//        }
//    }
}