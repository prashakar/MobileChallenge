package android.mobilechallenge.adapters;

import android.content.Context;
import android.mobilechallenge.R;
import android.mobilechallenge.models.ExchangeRate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.List;

public class ConvertedTilesAdapter extends RecyclerView.Adapter<ConvertedTilesAdapter.ViewHolder> {

    private Context mContext;

    private List<ExchangeRate> mExchangeRates;

    private String mInputCurrencyName;

    private BigDecimal mInputValue;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView currencyNameText;
        public TextView convertedValueText;
        public CardView singleCard;

        public ViewHolder(View v) {
            super(v);
            // lookup views
            currencyNameText = (TextView) v.findViewById(R.id.currency_name);
            convertedValueText = (TextView) v.findViewById(R.id.converted_value);
            singleCard = (CardView) v.findViewById(R.id.single_card);
        }
    }

    // constructor
    public ConvertedTilesAdapter(Context context, String currencyName,
                                 BigDecimal inputValue, List<ExchangeRate> mExchangeRates) {
        this.mContext = context;
        this.mInputCurrencyName = currencyName;
        this.mInputValue = inputValue;
        this.mExchangeRates = mExchangeRates;
    }

    // create new views
    @Override
    public ConvertedTilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_converted_item,
                parent, false);

        return new ViewHolder(v);
    }

    // replace the contents of a view
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the item at position
        ExchangeRate exchangeRate = mExchangeRates.get(position);

        final String currencyName = exchangeRate.getCurrencyName();
        final BigDecimal convertedValue = exchangeRate.getConvertedValue();

        // populate views with data
        holder.currencyNameText.setText(currencyName);
        if (convertedValue == null) {
            holder.convertedValueText.setText("-");
        } else {
            holder.convertedValueText.setText(String.valueOf(convertedValue));
        }
        holder.singleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInputValue.compareTo(BigDecimal.ZERO) == 0) {
                    // no user input yet
                    Toast.makeText(mContext, mContext.getString(R.string.input_missing),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // user has inputted data
                    String message = String.format(mContext.getString(R.string.card_details),
                            mInputValue, mInputCurrencyName, convertedValue, currencyName);
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Return the size
    @Override
    public int getItemCount() {
        return mExchangeRates.size();
    }

    public void setInputData(String currencyName, BigDecimal value) {
        this.mInputCurrencyName = currencyName;
        this.mInputValue = value;
    }
}