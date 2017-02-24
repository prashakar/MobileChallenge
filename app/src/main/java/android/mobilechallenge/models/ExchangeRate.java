package android.mobilechallenge.models;

import java.math.BigDecimal;

/**
 * ExchangeRate used for storing each exchange rate into DB and used in populating grid adapter.
 */
public class ExchangeRate {

    private String currencyName;
    private BigDecimal exchangeRate;
    private BigDecimal convertedValue;

    public ExchangeRate(String currencyName, BigDecimal exchangeRate) {
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
    }

    /**
     * Used to get name of the currency
     */
    public String getCurrencyName() {
        return this.currencyName;
    }

    /**
     * Used to get the exchange rate
     */
    public BigDecimal getExchangeRate() {
        return this.exchangeRate;
    }

    /**
     * Used to set the converted value
     */
    public void setConvertedValue(BigDecimal value) {
        this.convertedValue = value;
    }

    /**
     * Used to get the converted value
     */
    public BigDecimal getConvertedValue() {
        return this.convertedValue;
    }
}