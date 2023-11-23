package java.data_access;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.text.SimpleDateFormat;

import data_access.APIDataAccessObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

public class APIDataAccessObjectTest {
    private APIDataAccessObject DAO;

    //    @BeforeEach
    void setUp() {
        DAO = new APIDataAccessObject();
    }

    @Test
    public void testGetHistoricalQuotes() {
        setUp();
        String symbol = "IBM";
        String currency = "$USD";

        Map<Date, Double> historicalQuotes = DAO.getHistoricalQuotes(symbol, currency);

        assertNotNull(historicalQuotes);
        assertFalse(historicalQuotes.isEmpty());

        // Manual entered these from values on yahoo finance
        assert(historicalQuotes.get(new Date(123, 10, 1)) == 145.40);
        assert(historicalQuotes.get(new Date(123, 10, 2)) == 147.01);
        assert(historicalQuotes.get(new Date(123, 10, 3)) == 147.90);

        // These are weekends, so there should be no data
        assert(historicalQuotes.get(new Date(123, 10, 4)) == null);
        assert(historicalQuotes.get(new Date(123, 10, 5)) == null);
    }
}