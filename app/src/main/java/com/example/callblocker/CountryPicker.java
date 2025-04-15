package com.example.callblocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryPicker extends AppCompatActivity {

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_picker);

        EditText editTextSearch = findViewById(R.id.editTextSearch);
        ListView listViewCountries = findViewById(R.id.listViewCountries);

        List<String> countryList = Arrays.asList(getResources().getStringArray(R.array.country_names_with_codes));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(countryList));
        listViewCountries.setAdapter(adapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        listViewCountries.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCountry = adapter.getItem(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_country", selectedCountry);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }
}

