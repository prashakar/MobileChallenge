package android.mobilechallenge.others;

import android.mobilechallenge.models.LatestData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FixerIOApiInterface {
    @GET("/latest")
    Call<LatestData> loadExchangeRates();
}