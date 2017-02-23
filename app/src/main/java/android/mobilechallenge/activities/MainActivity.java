package android.mobilechallenge.activities;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.mobilechallenge.R;
import android.mobilechallenge.adapters.CurrencyNameAdapter;
import android.mobilechallenge.fragments.ConvertedGridFragment;
import android.mobilechallenge.models.ExchangeRate;
import android.mobilechallenge.models.LatestData;
import android.mobilechallenge.others.FixerIOApiInterface;
import android.mobilechallenge.utils.ExchangeRateService;
import android.mobilechallenge.utils.ExchangeRatesDBHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<LatestData>, AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private JobScheduler mJobScheduler;

    private Spinner mCurrencyNameSpinner;

    private EditText mCurrencyValueEdit;

    private ConvertedGridFragment mConvertedGridFragment;

    private TextView mHeaderText;

    private ProgressBar mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);
        mHeaderText = (TextView) findViewById(R.id.header_message);

        Log.i(TAG, "Initial data load");

        // show loading as we load initial data
        showLoading();

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

        // text box where user inputs value
        mCurrencyValueEdit = (EditText) findViewById(R.id.currency_value);
        // when user taps outside of EditText, hide the keyboard
        mCurrencyValueEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // send data to fragment
                    sendUserInput();
                }
            }
        });
        // when user taps done button
        mCurrencyValueEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // send data to fragment
                    sendUserInput();
                    return true;
                }
                return false;
            }
        });

        // spinner element
        mCurrencyNameSpinner = (Spinner) findViewById(R.id.currency_name_spinner);
        // spinner click listener
        mCurrencyNameSpinner.setOnItemSelectedListener(this);

        // grid fragment
        mConvertedGridFragment = (ConvertedGridFragment) getFragmentManager()
                .findFragmentById(R.id.currency_view);

    }

    @Override
    protected void onResume() {
        super.onResume();

        ComponentName serviceComponent = new ComponentName(getPackageName(), ExchangeRateService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(1, serviceComponent);

        // ensure updates are done at most every 30 min
        builder.setPeriodic(30 * 60000);

        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        int result = mJobScheduler.schedule(builder.build());
        if (result == JobScheduler.RESULT_SUCCESS) Log.d(TAG, "Job scheduled successfully!");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.v(TAG, "Job scheduling cancelled.");
        ExchangeRatesDBHelper exchangeRatesDBHelper = new ExchangeRatesDBHelper(this);
        ArrayList<ExchangeRate> test = exchangeRatesDBHelper.getAllRates();
        for (ExchangeRate rate : test) {
            Log.v(TAG, rate.getCurrencyName());
        }
        Log.v(TAG, String.valueOf(test.size()));

        exchangeRatesDBHelper.close();

        // cancel all network data service jobs
        mJobScheduler.cancelAll();
    }

    @Override
    public void onResponse(Call<LatestData> call, retrofit2.Response<LatestData> response) {

        // hide loading indicator now that data has been loaded
        hideLoading();

        // instantiate database helper
        ExchangeRatesDBHelper exchangeRatesDBHelper = new ExchangeRatesDBHelper(this);
        // clear all data in the table
        exchangeRatesDBHelper.deleteAll();

        // used to store name of all currencies
        List<String> currencyNames = new ArrayList<>();

        try {
            // convert all each rate in Rates object into JSON
            Gson gson = new Gson();
            JSONObject ratesJSONObj = new JSONObject(gson.toJson(response.body().getRates()));

            // iterate for each rate key-value
            Iterator<String> iter = ratesJSONObj.keys();
            while (iter.hasNext()) {
                // store the name of the currency
                String key = iter.next();
                // add to the list of currencies
                currencyNames.add(key);
                try {
                    // store the value of the currency
                    double value = ratesJSONObj.getDouble(key);

                    // now insert into DB
                    exchangeRatesDBHelper.addNewRate(key, value);

                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            // close the DB object
            exchangeRatesDBHelper.close();
        }

        Log.i(TAG, "Initial data loaded & saved successfully");

        // create adapter for the spinner
        SpinnerAdapter spinnerAdapter = new CurrencyNameAdapter(this, currencyNames);
        // attach adapter to the spinner
        mCurrencyNameSpinner.setAdapter(spinnerAdapter);

        // show input field and spinner
        mCurrencyValueEdit.setVisibility(View.VISIBLE);
        mCurrencyNameSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(Call<LatestData> call, Throwable t) {
        Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // send data to fragment
        sendUserInput();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * This method is used to send the value and currency input to the fragment.
     */
    private void sendUserInput() {
        // get value in EditText as soon as currency is selected
        String inputValueStr = mCurrencyValueEdit.getText().toString().trim();

        // send value if exists
        if (!inputValueStr.isEmpty()) {
            double inputValue = Double.valueOf(inputValueStr);
            mConvertedGridFragment.convert(inputValue, mCurrencyNameSpinner.getSelectedItem().toString());

            Log.i(TAG, "Sending user data");
        }
    }

    /**
     * This method is used to show the loading indicator also hides the text
     */
    private void showLoading() {
        mLoadingProgress.setVisibility(View.VISIBLE);
        mHeaderText.setVisibility(View.GONE);
    }

    /**
     * This method is used to hide the loading indicator also shows the text
     */
    private void hideLoading() {
        mLoadingProgress.setVisibility(View.GONE);
        mHeaderText.setVisibility(View.VISIBLE);
    }
}
