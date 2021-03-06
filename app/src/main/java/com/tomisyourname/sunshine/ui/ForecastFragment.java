package com.tomisyourname.sunshine.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tomisyourname.sunshine.BuildConfig;
import com.tomisyourname.sunshine.R;
import com.tomisyourname.sunshine.ui.adapter.ForecastAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

  private static final String LOG_TAG = "ForecastFragment";

  private ForecastAdapter adapter;
  private RecyclerView forecasts;

  public ForecastFragment() {
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
    forecasts = (RecyclerView) rootView.findViewById(R.id.rv_forecast);
    forecasts.setHasFixedSize(true);
    forecasts.setLayoutManager(new LinearLayoutManager(getContext()));
    ArrayList<String> data = loadCachedData();
    if(data != null) {
      adapter = new ForecastAdapter(data);
      forecasts.setAdapter(adapter);
    } else {
      String q = "Shanghai,China";
      new FetchWeatherTask().execute(q);
    }
    return rootView;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_forecast_fragment, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_refresh) {
      String q = "Shanghai,China";
      new FetchWeatherTask().execute(q);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private ArrayList<String> loadCachedData() {
    return null;
  }

  class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    @Override
    protected String[] doInBackground(String... params) {
      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;

      String forecastJsonStr = null;
      String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily";
      String units = "metric";
      int numDays = 7;
      Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
      builder.appendQueryParameter("q", params[0])
          .appendQueryParameter("units", units)
          .appendQueryParameter("appid", BuildConfig.OPEN_WEATHER_MAP_API_KEY)
          .appendQueryParameter("cnt", String.valueOf(numDays));
      try {
        URL url = new URL(builder.build().toString());

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
          forecastJsonStr = null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
          buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
          // Stream was empty.  No point in parsing.
          forecastJsonStr = null;
        }
        forecastJsonStr = buffer.toString();
      } catch (IOException e) {
        Log.e(LOG_TAG, "Error ", e);
        forecastJsonStr = null;
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (final IOException e) {
            Log.e(LOG_TAG, "Error closing stream", e);
          }
        }
      }
      Log.e(LOG_TAG, "Response json >>>" + forecastJsonStr);
      try {
        return getWeatherDataFromJson(forecastJsonStr, numDays);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String[] data) {
      if(data == null || data.length < 1) return;
      if(adapter != null) {
        adapter.clear();
        adapter.addAll(Arrays.asList(data));
        adapter.notifyDataSetChanged();
      } else {
        adapter = new ForecastAdapter(Arrays.asList(data));
        forecasts.setAdapter(adapter);
      }
    }
  }

  private String getReadableDateString(long time) {
    SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
    return shortenedDateFormat.format(time);
  }

  /**
   * Prepare the weather high/lows for presentation.
   */
  private String formatHighLows(double high, double low) {
    long roundedHigh = Math.round(high);
    long roundedLow = Math.round(low);

    String highLowStr = roundedHigh + "/" + roundedLow;
    return highLowStr;
  }

  /**
   * Take the String representing the complete forecast in JSON Format and
   * pull out the data we need to construct the Strings needed for the wireframes.
   * <p>
   * Fortunately parsing is easy:  constructor takes the JSON string and converts it
   * into an Object hierarchy for us.
   */
  private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
      throws JSONException {

    final String OWM_LIST = "list";
    final String OWM_WEATHER = "weather";
    final String OWM_TEMPERATURE = "temp";
    final String OWM_MAX = "max";
    final String OWM_MIN = "min";
    final String OWM_DESCRIPTION = "main";

    JSONObject forecastJson = new JSONObject(forecastJsonStr);
    JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

    Time dayTime = new Time();
    dayTime.setToNow();

    int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

    dayTime = new Time();

    String[] resultStrs = new String[numDays];
    for (int i = 0; i < weatherArray.length(); i++) {
      String day;
      String description;
      String highAndLow;

      JSONObject dayForecast = weatherArray.getJSONObject(i);

      long dateTime;
      dateTime = dayTime.setJulianDay(julianStartDay + i);
      day = getReadableDateString(dateTime);

      JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
      description = weatherObject.getString(OWM_DESCRIPTION);

      JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
      double high = temperatureObject.getDouble(OWM_MAX);
      double low = temperatureObject.getDouble(OWM_MIN);

      highAndLow = formatHighLows(high, low);
      resultStrs[i] = day + " - " + description + " - " + highAndLow;
    }

    return resultStrs;
  }

}
