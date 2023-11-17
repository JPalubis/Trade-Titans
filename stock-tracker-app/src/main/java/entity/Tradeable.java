package entity;

import java.util.Date;
import java.util.TreeMap;
import java.time.LocalDate;

public class Tradeable {
    private String name; // e.g. "Apple Inc."
    private String symbol; // e.g. "AAPL"
    private TreeMap<Date, Double> priceHistory; // price history in USD

    public Tradeable(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
        this.priceHistory = new TreeMap<>();
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public TreeMap<Date, Double> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(TreeMap<Date, Double> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public double getCurrentPrice() {
        if (priceHistory.size() == 0) {
            throw new RuntimeException("No price history found for " + this.symbol);
        }
        return priceHistory.lastEntry().getValue();
    }
}
