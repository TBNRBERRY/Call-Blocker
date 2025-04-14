package com.example.callblocker.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreferenceUtils {

    private final SharedPreferences prefs;

    public PreferenceUtils(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isBlockUnknownCallersEnabled() {
        return prefs.getBoolean("pref_block_unknown_callers", true);
    }

    public boolean isAreaCodeFilterEnabled() {
        return prefs.getBoolean("pref_enable_area_code_filter", false);
    }

    public boolean isCountryFilterEnabled() {
        return prefs.getBoolean("pref_enable_country_code_filter", false);
    }

    public Set<String> getAllowedAreaCodes() {
        String areaCodes = prefs.getString("pref_area_codes", "");
        return new HashSet<>(Arrays.asList(areaCodes.split(",")));
    }

    public Set<String> getAllowedCountryCodes() {
        String countryCodes = prefs.getString("pref_country_codes", "");
        return new HashSet<>(Arrays.asList(countryCodes.split(",")));
    }
}

