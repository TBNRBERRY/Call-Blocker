package com.example.callblocker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load preferences from the XML file
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Theme preference: Apply selected theme
        ListPreference themePref = findPreference("pref_theme");
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                AppCompatDelegate.setDefaultNightMode(getNightModeFromValue((String) newValue));
                requireActivity().recreate(); // Recreate the activity to apply theme
                return true;
            });
        }

        // Area Code filter: Enable/disable area code input
        SwitchPreferenceCompat areaCodeSwitch = findPreference("pref_enable_area_code_filter");
        EditTextPreference areaCodeInput = findPreference("pref_area_codes");

        if (areaCodeSwitch != null && areaCodeInput != null) {
            areaCodeInput.setEnabled(areaCodeSwitch.isChecked());

            areaCodeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isEnabled = (Boolean) newValue;
                areaCodeInput.setEnabled(isEnabled);
                return true;
            });
        }

        // Navigate to BlockedNumbersFragment when "Blocked Numbers" is clicked
        Preference blockedListPref = findPreference("blocked_numbers");
        if (blockedListPref != null) {
            blockedListPref.setOnPreferenceClickListener(preference -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, new BlockedNumbersFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            });
        }
    }

    private int getNightModeFromValue(String value) {
        switch (value) {
            case "light":
                return AppCompatDelegate.MODE_NIGHT_NO;
            case "dark":
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }
}
