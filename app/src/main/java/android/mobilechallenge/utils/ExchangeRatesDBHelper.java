package android.mobilechallenge.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.mobilechallenge.models.ExchangeRate;

import java.util.ArrayList;

public class ExchangeRatesDBHelper extends SQLiteOpenHelper {

    private Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_FILENAME = "rates.db";

    private static String CREATE_STATEMENT = "" +
            "CREATE TABLE rates(" +
            "currencyName varchar(100) primary key," +
            "exchangeRate decimal not null)";

    public ExchangeRatesDBHelper(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_STATEMENT = "" +
                "DROP TABLE rates";
        db.execSQL(DROP_STATEMENT);
        db.execSQL(CREATE_STATEMENT);
    }

    public ArrayList<ExchangeRate> getAllRates() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ExchangeRate> rates = new ArrayList<>();

        String[] columns = new String[]{"currencyName", "exchangeRate"};
        String where = "";
        String[] whereArgs = new String[]{};
        String groupBy = "";
        String groupArgs = "";
        String orderBy = "currencyName";

        Cursor cursor = db.query("rates", columns, where, whereArgs, groupBy, groupArgs, orderBy);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String currencyName = cursor.getString(0);
            double exchangeRate = cursor.getDouble(1);

            ExchangeRate rate = new ExchangeRate(currencyName, exchangeRate);
            rates.add(rate);

            cursor.moveToNext();
        }
        cursor.close();

        return rates;
    }

    public ExchangeRate addNewRate(String currencyName, double exchangeRate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("currencyName", currencyName);
        values.put("exchangeRate", exchangeRate);
        db.insertOrThrow("rates", null, values);

        db.close();

        ExchangeRate rate = new ExchangeRate(currencyName, exchangeRate);
        return rate;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("rates", null, null);
        db.close();
    }
}