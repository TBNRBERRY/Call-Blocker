package com.example.callblocker;

import android.os.Build;
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

            // 1. Block if not in contacts and "Block Unknown Callers" is ON
            if (prefs.isBlockUnknownCallersEnabled() && !isInContacts(phoneNumber)) {
                isBlocked = true;
            }

            // 2. Area code filter
            if (!isBlocked && prefs.isAreaCodeFilterEnabled()) {
                String areaCode = getAreaCode(phoneNumber);
                if (!prefs.getAllowedAreaCodes().contains(areaCode)) {
                    isBlocked = true;
                }
            }

            // 3. Country code filter
            if (!isBlocked && prefs.isCountryFilterEnabled()) {
                String countryCode = getCountryCode(phoneNumber);
                if (!prefs.getAllowedCountryCodes().contains(countryCode)) {
                    isBlocked = true;
                }
            }

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
        // TODO: Add real contact-check logic
        return false;
    }

    private String getAreaCode(String number) {
        return number.length() >= 10 ? number.substring(number.length() - 10, number.length() - 7) : "";
    }

    private String getCountryCode(String number) {
        return number.startsWith("+") ? number.substring(0, 2) : "";
    }
}
