package de.kevoundfreun.micalendario;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Kevo on 15/02/2017.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}