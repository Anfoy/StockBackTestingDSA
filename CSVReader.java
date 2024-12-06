import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private final String filePath;
    private final List<String[]> data = new ArrayList<>();
    private String[] headers;

    public CSVReader(String filePath) {
        this.filePath = filePath;
    }

    public void readCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (isHeader) {
                    headers = values;
                    isHeader = false;
                } else {
                    data.add(values);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }
    }


    public String[] getHeaders() {
        return headers;
    }


    public List<String> getColumnData(String columnName) {
        List<String> columnData = new ArrayList<>();
        int columnIndex = getColumnIndex(columnName);

        if (columnIndex == -1) {
            System.err.println("Column " + columnName + " not found");
            return columnData;
        }

        for (String[] row : data) {
            columnData.add(row[columnIndex]);
        }
        return columnData;
    }

    public List<TradingDay> getTradingDays() {
        List<TradingDay> tradingDays = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }


                TradingDay tradingDay = getTradingDay(line);
                tradingDays.add(tradingDay);
            }
        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numeric data: " + e.getMessage());
        }

        return tradingDays;
    }

    private static TradingDay getTradingDay(String line) {
        String[] values = line.split(",");

        String date = values[0];
        double open = Double.parseDouble(values[1]);
        double high = Double.parseDouble(values[2]);
        double low = Double.parseDouble(values[3]);
        double close = Double.parseDouble(values[4]);
        double volume = Double.parseDouble(values[5]);

        return new TradingDay(date, open, high, low, close, volume);
    }


    public String[] getRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            return data.get(rowIndex);
        } else {
            System.err.println("Row index " + rowIndex + " is out of bounds");
            return null;
        }
    }


    private int getColumnIndex(String columnName) {
        if (headers == null) {
            System.err.println("CSV file not read yet. Call readCSV() first.");
            return -1;
        }
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }
}