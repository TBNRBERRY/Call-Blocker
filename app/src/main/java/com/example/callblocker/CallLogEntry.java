package com.example.callblocker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallLogEntry {
    private final String displayName;
    private final String callType;
    private final Date callDate;

    public CallLogEntry(String displayName, String callType, long dateMillis) {
        this.displayName = displayName;
        this.callType = callType;
        this.callDate = new Date(dateMillis);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFormattedDetails() {
        String symbol = switch (callType) {
            case "Incoming" -> "→ Incoming";
            case "Outgoing" -> "← Outgoing";
            case "Missed" -> "✖ Missed";
            default -> "• Other";
        };

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
        return symbol + " at " + sdf.format(callDate);
    }
}

