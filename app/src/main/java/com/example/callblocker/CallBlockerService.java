package com.example.callblocker;

import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.callblocker.util.BlockListManager;

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

            boolean isBlocked = false;

            // 1. Block if number is not in contacts (placeholder for contact check)
            if (!isInContacts(phoneNumber)) {
                isBlocked = true;
            }

            // 2. Block if area code is not allowed
            if (!isBlocked && isAreaCodeFilterEnabled()) {
                String areaCode = getAreaCode(phoneNumber);
                if (!isAllowedAreaCode(areaCode)) {
                    isBlocked = true;
                }
            }

            // 3. Block if country code is not allowed
            if (!isBlocked && isCountryFilterEnabled()) {
                String countryCode = getCountryCode(phoneNumber);
                if (!isAllowedCountry(countryCode)) {
                    isBlocked = true;
                }
            }

            if (isBlocked) {
                // Add number to block list
                BlockListManager blockListManager = new BlockListManager(getApplicationContext());
                blockListManager.addNumberToBlockList(phoneNumber);

                // Block and reject the call
                CallResponse response = new CallResponse.Builder()
                        .setDisallowCall(true)
                        .setRejectCall(true)
                        .setSkipCallLog(false)
                        .setSkipNotification(false)
                        .build();
                respondToCall(callDetails, response);

                Log.i(TAG, "Blocked and added to block list: " + phoneNumber);
            } else {
                // Allow call
                CallResponse response = new CallResponse.Builder()
                        .setDisallowCall(false)
                        .setRejectCall(false)
                        .build();
                respondToCall(callDetails, response);

                Log.i(TAG, "Allowed call: " + phoneNumber);
            }
        }
    }

    // ---------- Helper Methods (stubs) ----------

    private boolean isInContacts(String phoneNumber) {
        // TODO: Replace with actual contact-check logic
        return false;
    }

    private boolean isAreaCodeFilterEnabled() {
        // TODO: Check user settings for area code filter toggle
        return true;
    }

    private boolean isAllowedAreaCode(String areaCode) {
        // TODO: Replace with logic to compare against allowed area codes
        return true;
    }

    private boolean isCountryFilterEnabled() {
        // TODO: Check user settings for country code filter toggle
        return true;
    }

    private boolean isAllowedCountry(String countryCode) {
        // TODO: Replace with logic to compare against allowed countries
        return true;
    }

    private String getAreaCode(String phoneNumber) {
        // Simplified example: area code = first 3 digits after country code
        return phoneNumber.length() > 3 ? phoneNumber.substring(0, 3) : "";
    }

    private String getCountryCode(String phoneNumber) {
        // Simplified example: assumes country code is the first digit(s)
        return phoneNumber.startsWith("+") && phoneNumber.length() > 2
                ? phoneNumber.substring(0, 2) // e.g., +1 or +44
                : phoneNumber.length() > 1 ? phoneNumber.substring(0, 1) : "";
    }
}
