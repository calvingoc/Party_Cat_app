package online.cagocapps.party_cat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;


/**
 * Created by cgehredo on 2/21/2017.
 */

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_screen);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        Boolean prem = sharedPreferences.getBoolean(getString(R.string.premium_key), false);
        Preference drinkingGame = findPreference(getString(R.string.drinking_game_key));
        drinkingGame.setEnabled(prem);
        Boolean drinking = sharedPreferences.getBoolean(getResources().getString(R.string.drinking_game_key), false);
        Preference preference = findPreference(getString(R.string.party_key));
        preference.setEnabled(drinking);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.drinking_game_key))){
            Preference pref = findPreference(getString(R.string.party_key));
            Boolean partyMode = sharedPreferences.getBoolean(getResources().getString(R.string.drinking_game_key), false);
            pref.setEnabled(partyMode);
            if (partyMode) {
                Toast.makeText(getActivity(), getResources().getString(R.string.party_toast), Toast.LENGTH_LONG).show();
            }
        }
        else if (s.equals(getString(R.string.premium_key))){
            Preference preference = findPreference(getString(R.string.drinking_game_key));
            preference.setEnabled(sharedPreferences.getBoolean(getResources().getString(R.string.premium_key), false));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
