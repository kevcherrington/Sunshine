package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * The deatil fragment containing the details of the forecast
     */
    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

        private ShareActionProvider shareActionProvider;
        private String forecastStr;

        private static final int DETAIL_LOADER = 0;

        private static final String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        };

        // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
        // must change.
        static final int COL_WEATHER_ID = 0;
        static final int COL_WEATHER_DATE = 1;
        static final int COL_WEATHER_DESC = 2;
        static final int COL_WEATHER_MAX_TEMP = 3;
        static final int COL_WEATHER_MIN_TEMP = 4;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem item = menu.findItem(R.id.action_share);

            shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if (forecastStr != null) {
                shareActionProvider.setShareIntent(getShareForecastIntent());
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        private Intent getShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); // returns me to my activity instead of the sharing activity (i.e. messages)
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, forecastStr + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            return new CursorLoader(getActivity(), intent.getData(), FORECAST_COLUMNS, null, null, null);
        }

        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            if (!cursor.moveToFirst()) {
                return;
            }

            String dateString = Utility.formatDate(cursor.getLong(COL_WEATHER_DATE));
            String weatherDescription = cursor.getString(COL_WEATHER_DESC);
            boolean isMetric = Utility.isMetric(getActivity());
            String high = Utility.formatTemperature(cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String low = Utility.formatTemperature(cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

            forecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

            TextView detailTextView = (TextView) getView().findViewById(R.id.detail_text);
            detailTextView.setText(forecastStr);

            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(getShareForecastIntent());
            }
        }

        public void onLoaderReset(Loader<Cursor> cursorLoader) {
        }

    }
}
