package android.mobilechallenge.models;

/**
 * ExchangeRate used for storing each exchange rate into DB and used in populating grid adapter.
 */
public class ExchangeRate {

    private String currencyName;
    private double exchangeRate;
    private double convertedValue;

    public ExchangeRate(String currencyName, double exchangeRate) {
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
    public double getExchangeRate() {
        return this.exchangeRate;
    }

    /**
     * Used to set the converted value
     */
    public void setConvertedValue(double value) {
        this.convertedValue = value;
    }

    /**
     * Used to get the converted value
     */
    public double getConvertedValue() {
        return this.convertedValue;
    }
}