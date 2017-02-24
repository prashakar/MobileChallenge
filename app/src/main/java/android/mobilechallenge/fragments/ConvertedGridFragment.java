package android.mobilechallenge.fragments;


import android.app.Fragment;
import android.mobilechallenge.R;
import android.mobilechallenge.adapters.ConvertedTilesAdapter;
import android.mobilechallenge.models.ExchangeRate;
import android.mobilechallenge.others.GridOffsetDecoration;
import android.mobilechallenge.utils.ExchangeRatesDBHelper;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


/**
 * This fragment is used to display the converted values.
 */
public class ConvertedGridFragment extends Fragment {

    private RecyclerView mConvertedRecyclerView;

    private ConvertedTilesAdapter mConvertedTilesAdapter;

    private List<ExchangeRate> mExchangeRates;

    public ConvertedGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExchangeRates = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_converted_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // lookup view
        mConvertedRecyclerView = (RecyclerView) view.findViewById(R.id.converted_recycler_view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get data from DB after activity has been created
        ExchangeRatesDBHelper exchangeRatesDBHelper = new ExchangeRatesDBHelper(getContext());
        mExchangeRates = exchangeRatesDBHelper.getAllRates();

        // use a grid layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mConvertedRecyclerView.setLayoutManager(gridLayoutManager);

        // use custom item decoration to achieve spacing between grid items
        GridOffsetDecoration gridOffsetDecoration =
                new GridOffsetDecoration(getContext(), R.dimen.grid_spacing);
        mConvertedRecyclerView.addItemDecoration(gridOffsetDecoration);

        // adapter for RecyclerView
        mConvertedTilesAdapter = new ConvertedTilesAdapter(getContext(), null,
                BigDecimal.ZERO, mExchangeRates);
        // set the adapter
        mConvertedRecyclerView.setAdapter(mConvertedTilesAdapter);

    }

    /**
     * Method that retrieves all currency exchange rates from the DB then updates UI with
     * converted value.
     *
     * @param inputValue   The value to convert
     * @param currencyName The name of the input currency
     */
    public void convert(BigDecimal inputValue, String currencyName) {

        // conversion rate from 1 EUR to currencyName
        BigDecimal conversionExchangeRate = BigDecimal.ZERO;

        // find exchange rate of the input currency
        for (ExchangeRate exchangeRate : mExchangeRates) {
            // match currency name in DB with user supplied currency name
            if (exchangeRate.getCurrencyName().equals(currencyName)) {
                conversionExchangeRate = exchangeRate.getExchangeRate();
                break;
            }
        }

        // find converted value w.r.t EUR
        BigDecimal convertedValueEur = BigDecimal.ZERO;
        try {
            convertedValueEur = inputValue.divide(conversionExchangeRate, 4, RoundingMode.HALF_UP);
        } catch (ArithmeticException ignored) {

        }

        // using the input value and currency, determine converted value for each exchange rate
        for (ExchangeRate exchangeRate : mExchangeRates) {
            // convert into each currency w.r.t. exchange rate
            BigDecimal convertedValue = convertedValueEur.multiply(exchangeRate.getExchangeRate());
            // store the converted value
            if (convertedValue.compareTo(BigDecimal.ZERO) == 0) {
                exchangeRate.setConvertedValue(BigDecimal.ZERO);
            } else {
                exchangeRate.setConvertedValue(convertedValue.setScale(4, BigDecimal.ROUND_HALF_UP));
            }
        }

        // update adapter
        mConvertedTilesAdapter.setInputData(currencyName, inputValue);
        mConvertedTilesAdapter.notifyDataSetChanged();
    }
}
