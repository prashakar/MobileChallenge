package android.mobilechallenge.adapters;

import android.content.Context;
import android.mobilechallenge.R;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CurrencyNameAdapter extends ArrayAdapter<String> {

    public CurrencyNameAdapter(Context context, List<String> currencyNames) {
        super(context, 0, currencyNames);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get the string at this position
        String currency = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.single_currency_name_item, parent, false);
        }

        // lookup view for population
        TextView currencyNameText = (TextView) convertView.findViewById(R.id.currency_name);
        // populate data into view
        currencyNameText.setText(currency);

        // return the view to render
        return convertView;
    }
}
