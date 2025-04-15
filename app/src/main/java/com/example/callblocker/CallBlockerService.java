package com.example.callblocker;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.callblocker.util.BlockListManager;
import com.example.callblocker.util.PreferenceUtils;

public class CallBlockerService extends CallScreeningService {

    private static final String TAG = "CallBlockerService";

    @Override
    public void onScreenCall(@NonNull Call.Details callDetails) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                callDetails.getCallDirection() == Call.Details.DIRECTION_INCOMING) {

            String phoneNumber = callDetails.getHandle() != null
                    ? callDetails.getHandle().getSchemeSpecificPart()
                    : null;

            if (phoneNumber == null || phoneNumber.isEmpty()) {
                Log.w(TAG, "Invalid phone number. Skipping.");
                return;
            }

            PreferenceUtils prefs = new PreferenceUtils(getApplicationContext());
            boolean isBlocked = false;

            // Log.d(TAG, "Unknown Caller Blocking Enabled: " + prefs.isUnknownCallerBlockingEnabled());

            // 1. Block if number is not in contacts AND user enabled this filter
            if (prefs.isUnknownCallerBlockingEnabled() && !isInContacts(phoneNumber)) {
                isBlocked = true;
                Log.i(TAG, "Blocking unknown caller (not in contacts): " + phoneNumber);
            }

            /* 2. Area code filter
            if (!isBlocked && prefs.isAreaCodeFilterEnabled()) {
                String areaCode = prefs.getAreaCode(phoneNumber);
                if (!prefs.getAllowedAreaCodes().contains(areaCode)) {
                    isBlocked = true;
                }
            }

            // 3. Country code filter
            if (!isBlocked && prefs.isCountryFilterEnabled()) {
                String countryCode = prefs.getCountryCode(phoneNumber);
                if (!prefs.getAllowedCountryCodes().contains(countryCode)) {
                    isBlocked = true;
                }
            }
             */

            if (isBlocked) {
                BlockListManager blockListManager = new BlockListManager(getApplicationContext());
                blockListManager.addNumberToBlockList(phoneNumber);

                respondToCall(callDetails, new CallResponse.Builder()
                        .setDisallowCall(true)
                        .setRejectCall(true)
                        .build());

                Log.i(TAG, "Blocked call: " + phoneNumber);
            } else {
                respondToCall(callDetails, new CallResponse.Builder()
                        .setDisallowCall(false)
                        .setRejectCall(false)
                        .build());

                Log.i(TAG, "Allowed call: " + phoneNumber);
            }
        }
    }

    private boolean isInContacts(String phoneNumber) {
        Log.d(TAG, "Phone number received: " + phoneNumber);

        Log.d(TAG, "Checking if number is in contacts: " + phoneNumber);

        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = null;

        try {
            cursor = resolver.query(uri,
                    new String[]{ContactsContract.PhoneLookup._ID},
                    null, null, null);

            if (cursor != null) {
                Log.d(TAG, "Cursor returned: " + cursor.getCount() + " entries");
                if (cursor.moveToFirst()) {
                    Log.i(TAG, "Match found in contacts for: " + phoneNumber);
                    return true;
                } else {
                    Log.i(TAG, "No match found in contacts for: " + phoneNumber);
                }
            } else {
                Log.w(TAG, "Cursor is null when checking contacts for: " + phoneNumber);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking contacts for number: " + phoneNumber, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }
}
