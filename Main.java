//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Stock tesla = new Stock("TSLA", "src/tesla_stock_data.csv");
        Stock amzn = new Stock("AMZN", "src/amazon_stock_data.csv");
        Stock pltr = new Stock("PLTR", "src/pltr_stock_data.csv");
        Stock Msft = new Stock("MSFT", "src/msft_stock_data.csv");
     //  tesla.exportAllRSIToCSV(14, "rsioutput.csv");
      // tesla.exportRollingAverageToCSV(5, "rollingaverageoutput.csv");
        Trader trader = new Trader(100000.0, pltr);
       trader.runAlgoThree();
       // trader.runAlgoOne();
    }
}