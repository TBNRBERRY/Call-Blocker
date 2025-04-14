package com.example.callblocker;

import android.content.SharedPreferences;
import android.telecom.Call;
import android.util.Log;

public class CallScreeningService extends android.telecom.CallScreeningService {


    @Override
    public void onScreenCall(Call.Details callDetails) {
        String incomingNumber = callDetails.getHandle().getSchemeSpecificPart();
        Log.d("CallScreening", "Incoming call from: " + incomingNumber);

        SharedPreferences prefs = getSharedPreferences("CallBlockPrefs", MODE_PRIVATE);
        boolean blockUnknown = prefs.getBoolean("block_unknown", true);
        String allowedArea = prefs.getString("area_code", "");
        String allowedCountry = prefs.getString("country_code", "");

        boolean isBlocked = true;

        if (!allowedArea.isEmpty() && incomingNumber.startsWith(allowedArea)) {
            isBlocked = false;
        }

        if (!allowedCountry.isEmpty() && incomingNumber.startsWith(allowedCountry)) {
            isBlocked = false;
        }

        // You could enhance this by checking against saved contacts

        CallResponse.Builder responseBuilder = new CallResponse.Builder();
        if (isBlocked && blockUnknown) {
            responseBuilder.setDisallowCall(true);
            responseBuilder.setRejectCall(true);
            responseBuilder.setSkipCallLog(true);
            responseBuilder.setSkipNotification(true);
        }

        respondToCall(callDetails, responseBuilder.build());
    }
}

