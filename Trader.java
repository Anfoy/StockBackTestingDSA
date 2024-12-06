import java.io.IOException;
import java.util.List;
import java.io.FileWriter;
public class Trader {

    private double balance; // Cash balance
    private int sharesOwned; // Number of shares owned
    private Stock stock; // Stock data

    public Trader(double initialBalance, Stock stock) {
        this.balance = initialBalance;
        this.sharesOwned = 0;
        this.stock = stock;
    }

    public int tradeEvauluator(TradingDay day, double rsi, double movingAvg){
        if (rsi <= 30.0 && day.getClose() < movingAvg){
            return 10;
        }
        if (rsi >= 70.0 && day.getClose() > movingAvg){
            return -10;
        }
        return 0;
    }


    /**
     * This algorithm just holds for a year.
     */
    public void runAlgoOne() {
        List<TradingDay> tradingDays = stock.getTradingDays();

        if (tradingDays.isEmpty()) {
            System.out.println("No trading data available.");
            return;
        }

        // Buy shares on the first trading day
        TradingDay firstDay = tradingDays.getFirst();
        double sharePrice = firstDay.getClose();
        sharesOwned = (int) (balance / sharePrice);
        balance -= sharesOwned * sharePrice;

        System.out.printf("Bought %d shares on %s at %.2f each. Remaining balance: %.2f\n",
                sharesOwned, firstDay.getDate(), sharePrice, balance);

        // Hold shares until the last trading day
        TradingDay lastDay = tradingDays.getLast();
        double finalSharePrice = lastDay.getClose();

        // Calculate final net worth
        double netWorth = balance + (sharesOwned * finalSharePrice);

        System.out.printf("After holding for a year, on " + lastDay.getDate() + ", share price is %.2f. Final net worth: %.2f\n",
                finalSharePrice, netWorth);
    }

    /**
     * This algorithm uses RSI and EMA to purchase/ sell shares.
     */

    public void runAlgoTwo() {
        String csvFile = stock.getSymbol() + "log.csv";

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Date,Action,Price,Shares,Balance\n");
        } catch (IOException e) {
            System.err.println("Error initializing CSV: " + e.getMessage());
            return;
        }

        for (TradingDay tradingDay : stock.getTradingDays()) {
            double rsi = stock.calculateRSI(tradingDay, 14);
            double ema = stock.calculateEMA(tradingDay, 14);

            if (rsi == 0.0 || ema == 0.0) {
                continue;
            } else {
                int sharesToManage = tradeEvauluator(tradingDay, rsi, ema);

                if (sharesToManage > 0) { // Buy action
                    double sharePrice = tradingDay.getClose();
                    int maxAffordableShares = (int) (balance / sharePrice);
                    int sharesToBuy = Math.min(sharesToManage, maxAffordableShares);

                    if (sharesToBuy > 0) {
                        double cost = sharesToBuy * sharePrice;
                        sharesOwned += sharesToBuy;
                        balance -= cost;

                        // Log buy action
                        logTrade(csvFile, tradingDay.getDate(), "BUY", sharePrice, sharesToBuy, balance);
                    }
                } else if (sharesToManage < 0) { // Sell action
                    int sharesToSell = Math.min(sharesOwned, -sharesToManage);

                    if (sharesToSell > 0) {
                        double revenue = sharesToSell * tradingDay.getClose();
                        sharesOwned -= sharesToSell;
                        balance += revenue;

                        // Log sell action
                        logTrade(csvFile, tradingDay.getDate(), "SELL", tradingDay.getClose(), sharesToSell, balance);
                    }
                }
            }

            if (tradingDay == stock.getTradingDays().getLast()) {
                double revenue = sharesOwned * tradingDay.getClose();
                sharesOwned = 0;
                balance += revenue;

                // Log final liquidation
                logTrade(csvFile, tradingDay.getDate(), "SELL (FINAL)", tradingDay.getClose(), sharesOwned, balance);
            }
        }

        System.out.printf("Final Balance: %.2f. Remaining Shares: %d\n", balance, sharesOwned);
    }

    /**
     * Logs the trade action to a CSV file.
     */
    private void logTrade(String csvFile, String date, String action, double price, int shares, double balance) {
        try (FileWriter writer = new FileWriter(csvFile, true)) {
            writer.append(String.format("%s,%s,%.2f,%d,%.2f\n", date, action, price, shares, balance));
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    /**
     * This algorithm uses Mean and Standard Deviation (SD)
     * to decide when to trade.
     */
    public void runAlgoThree() {
        String csvFile = stock.getSymbol() + ".algoThree.log.csv";


        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Date,Action,Price,Shares,Balance\n");
        } catch (IOException e) {
            System.err.println("Error initializing CSV: " + e.getMessage());
            return;
        }

        List<TradingDay> tradingDays = stock.getTradingDays();

        if (tradingDays.size() < 20) {
            System.out.println("Not enough data for algorithm.");
            return;
        }

        int lookbackPeriod = 14; // Period for mean and standard deviation calculations

        for (int i = lookbackPeriod; i < tradingDays.size(); i++) {
            TradingDay currentDay = tradingDays.get(i);

            // Calculate Mean and Standard Deviation of closing prices for the period
            List<Double> closingPrices = tradingDays.subList(i - lookbackPeriod, i)
                    .stream()
                    .map(TradingDay::getClose)
                    .toList();
            double[] closingPricesArray = closingPrices.stream().mapToDouble(Double::doubleValue).toArray();
            double mean = calculateMean(closingPricesArray);
            double sd = calculateSD(closingPricesArray);

            if (currentDay.getClose() < mean - 2 * sd) { // Buy condition
                double sharePrice = currentDay.getClose();
                int maxAffordableShares = (int) (balance / sharePrice);

                if (maxAffordableShares > 0) {
                    sharesOwned += maxAffordableShares;
                    balance -= maxAffordableShares * sharePrice;

                    // Log buy action
                    logTrade(csvFile, currentDay.getDate(), "BUY", sharePrice, maxAffordableShares, balance);
                }
            } else if (currentDay.getClose() > mean + 2 * sd) { // Sell condition
                int sharesToSell = sharesOwned;

                if (sharesToSell > 0) {
                    double revenue = sharesToSell * currentDay.getClose();
                    sharesOwned -= sharesToSell;
                    balance += revenue;

                    // Log sell action
                    logTrade(csvFile, currentDay.getDate(), "SELL", currentDay.getClose(), sharesToSell, balance);
                }
            }
        }

        System.out.printf("Final Balance: %.2f. Remaining Shares: %d\n", balance, sharesOwned);
    }



    public double getBalance() {
        return balance;
    }

    public int getSharesOwned() {
        return sharesOwned;
    }

    /**
     * @link <a href="https://java.libhunt.com/jobfuscator-alternatives">...</a>
     * @param numArray numbers to find SD of
     * @return SD of set.
     */
    private double calculateSD(double[] numArray){
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for (double num : numArray) {
            sum += num;
        }

        double mean = sum / length;

        for (double num : numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / length);
    }

    private double calculateMean(double[] numArray){
        double sum = 0.0;
        for (double num : numArray) {
            sum += num;
        }
        return sum / numArray.length;

    }
    }