package com.example.callblocker.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class BlockListManager {
    private static final String BLOCK_LIST_KEY = "block_list";
    private final SharedPreferences prefs;

    public BlockListManager(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Set<String> getBlockList() {
        return new HashSet<>(prefs.getStringSet(BLOCK_LIST_KEY, new HashSet<>()));
    }

    public void addNumberToBlockList(String number) {
        Set<String> blockList = getBlockList();
        blockList.add(number);
        prefs.edit().putStringSet(BLOCK_LIST_KEY, blockList).apply();
    }

    public void removeNumberFromBlockList(String number) {
        Set<String> blockList = getBlockList();
        blockList.remove(number);
        prefs.edit().putStringSet(BLOCK_LIST_KEY, blockList).apply();
    }
}
