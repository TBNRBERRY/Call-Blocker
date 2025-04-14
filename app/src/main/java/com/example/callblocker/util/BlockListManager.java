package com.example.callblocker.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class BlockListManager {
    private static final String PREF_NAME = "block_list_prefs";
    private static final String BLOCK_LIST_KEY = "blocked_numbers";
    private final SharedPreferences sharedPreferences;

    public BlockListManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addNumberToBlockList(String phoneNumber) {
        Set<String> blockList = getBlockList();
        blockList.add(phoneNumber);
        sharedPreferences.edit().putStringSet(BLOCK_LIST_KEY, blockList).apply();
    }

    public void removeNumberFromBlockList(String phoneNumber) {
        Set<String> blockList = getBlockList();
        blockList.remove(phoneNumber);
        sharedPreferences.edit().putStringSet(BLOCK_LIST_KEY, blockList).apply();
    }

    public Set<String> getBlockList() {
        return new HashSet<>(sharedPreferences.getStringSet(BLOCK_LIST_KEY, new HashSet<>()));
    }

}