package android.mobilechallenge.utils;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.mobilechallenge.R;
import android.mobilechallenge.models.LatestData;
import android.mobilechallenge.others.FixerIOApiInterface;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * JobService to be scheduled by the JobScheduler.
 */
public class ExchangeRateService extends JobService implements Callback<LatestData> {

    private static final String TAG = "ExchangeRateService";

    private JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;

        // use retrofit as an HTTP client for making API calls
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.fixer_io_api_call))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call
        FixerIOApiInterface fixerIOApiInterface = retrofit.create(FixerIOApiInterface.class);
        Call<LatestData> call = fixerIOApiInterface.loadExchangeRates();
        // asynchronous call
        call.enqueue(this);

        // we have performed some async operation
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @Override
    public void onResponse(Call<LatestData> call, retrofit2.Response<LatestData> response) {
        // instantiate database helper
        ExchangeRatesDBHelper exchangeRatesDBHelper = new ExchangeRatesDBHelper(this);
        // clear all data in the table
        exchangeRatesDBHelper.deleteAll();

        try {
            // convert all each rate in Rates object into JSON
            Gson gson = new Gson();
            JSONObject ratesJSONObj = new JSONObject(gson.toJson(response.body().getRates()));

            // iterate for each rate key-value
            Iterator<String> iter = ratesJSONObj.keys();
            while (iter.hasNext()) {
                // store the name of the currency
                String key = iter.next();
                try {
                    // store the value of the currency
                    BigDecimal value = BigDecimal.valueOf(ratesJSONObj.getDouble(key));

                    // now insert into DB
                    exchangeRatesDBHelper.addNewRate(key, value);

                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "New data loaded & saved successfully");

//        ArrayList<ExchangeRate> test = exchangeRatesDBHelper.getAllRates();
//        for (ExchangeRate rate : test) {
//            Log.v(TAG, rate.getCurrencyName());
//        }
//        Log.v(TAG, String.valueOf(test.size()));

        // close the DB object
        exchangeRatesDBHelper.close();

        // notify that the job is finished
        this.jobFinished(params, false);
    }

    @Override
    public void onFailure(Call<LatestData> call, Throwable t) {
        Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
