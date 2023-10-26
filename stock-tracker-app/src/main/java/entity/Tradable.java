package entity;

import java.util.Date;

public abstract class Tradeable {
    private String name; // e.g. "Apple Inc."
    private String symbol; // e.g. "AAPL"
    private HashMap<Date, double> priceHistory; // price history in USD
    
    public Tradeable(String name, String symbol, HashMap<Date, double> priceHistory) {
        this.name = name;
        this.symbol = symbol;
        this.priceHistory = priceHistory;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public HashMap<Date, double> getPriceHistory() {
        return price_history;
    }
}
