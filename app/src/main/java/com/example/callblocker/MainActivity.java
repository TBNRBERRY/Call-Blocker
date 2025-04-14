package com.example.callblocker;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView callLogListView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyAppTheme(); // Apply the theme before setContentView()
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callLogListView = findViewById(R.id.callLogListView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {
                    android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ANSWER_PHONE_CALLS
            }, 1);
        } else {
            loadCallLogs();  // If permissions are already granted
        }
    }

    private void applyAppTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String themePref = prefs.getString("pref_theme", "system");

        switch (themePref) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "system":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private String getContactName(String phoneNumber) {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = resolver.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                cursor.close();
                return name;
            }
            cursor.close();
        }

        return phoneNumber; // fallback to number if no match
    }

    // Menu setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_menu) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCallLogs();
            } else {
                Toast.makeText(this, "Call log permission is required.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String formatPhoneNumber(String number) {
        if (number == null) return "Unknown";

        // Remove all non-digit characters except leading '+'
        String cleaned = number.replaceAll("[^\\d+]", "");

        // US/Canada number with +1 (e.g., +11234567890)
        if (cleaned.startsWith("+1") && cleaned.length() == 12) {
            String area = cleaned.substring(2, 5);
            String prefix = cleaned.substring(5, 8);
            String line = cleaned.substring(8);
            return "+1(" + area + ")-" + prefix + "-" + line;
        }

        // 10-digit local number (e.g., 1234567890)
        if (cleaned.length() == 10) {
            String area = cleaned.substring(0, 3);
            String prefix = cleaned.substring(3, 6);
            String line = cleaned.substring(6);
            return "(" + area + ") " + prefix + "-" + line;
        }

        // Return as-is if it doesn't match the patterns
        return number;
    }

    private void loadCallLogs() {
        ArrayList<CallLogEntry> logs = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
        );

        if (cursor != null) {
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);

            while (cursor.moveToNext()) {
                String number = cursor.getString(numberIndex);
                long dateMillis = cursor.getLong(dateIndex);
                int callType = cursor.getInt(typeIndex);

                String formattedNumber = formatPhoneNumber(number);
                String contactName = getContactName(number);
                String displayName = contactName.equals(number) ? formattedNumber : contactName;

                String typeStr = switch (callType) {
                    case CallLog.Calls.INCOMING_TYPE -> "Incoming";
                    case CallLog.Calls.OUTGOING_TYPE -> "Outgoing";
                    case CallLog.Calls.MISSED_TYPE -> "Missed";
                    default -> "Other";
                };

                logs.add(new CallLogEntry(displayName, typeStr, dateMillis));
            }
            cursor.close();
        }

        CallLogAdapter adapter = new CallLogAdapter(this, logs);
        callLogListView.setAdapter(adapter);
    }

}
