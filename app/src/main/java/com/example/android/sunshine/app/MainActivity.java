package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "On Create Called");
        super.onCreate(savedInstanceState);
//        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "On Destroy Called");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "On Pause Called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "On Resume Called");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "On Stop Called");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.d(LOG_TAG, "On Start Called");
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_map:
                return openPreferredLocationInMap();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean openPreferredLocationInMap() {
        String loc = Utility.getPreferredLocation(this);

        Uri uri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", Uri.encode(loc)).build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(uri);

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Unable to find suitable Map App", Toast.LENGTH_SHORT).show(); // Magic error string put error message into strings.
        }
        return true;
    }


}
