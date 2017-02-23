package android.mobilechallenge.models;

/**
 * This class is used to map the JSON keys returned from the API call to the object by GSON. This
 * class was auto-generated.
 */
public class LatestData {

    private String base;
    private String date;
    private Rates rates;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Rates getRates() {
        return rates;
    }

    public void setRates(Rates rates) {
        this.rates = rates;
    }
}