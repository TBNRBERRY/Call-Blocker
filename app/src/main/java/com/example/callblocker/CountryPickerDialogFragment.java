package com.example.callblocker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryPickerDialogFragment extends DialogFragment {

    public interface OnCountrySelectedListener {
        void onCountrySelected(String countryName, String countryCode);
    }

    private final OnCountrySelectedListener listener;

    public CountryPickerDialogFragment(OnCountrySelectedListener listener) {
        this.listener = listener;
    }

    private List<String> filteredCountryNames;
    private String[] countryNames;
    private String[] countryCodes;
    private ArrayAdapter<String> adapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.country_picker, null);

        EditText searchEditText = view.findViewById(R.id.editTextSearch);
        ListView listView = view.findViewById(R.id.listViewCountries);

        countryNames = context.getResources().getStringArray(R.array.country_names);
        countryCodes = context.getResources().getStringArray(R.array.country_names_with_codes);
        filteredCountryNames = new ArrayList<>(Arrays.asList(countryNames));

        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, filteredCountryNames);
        listView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCountries(s.toString());
            }
        });

        listView.setOnItemClickListener((AdapterView<?> parent, View itemView, int position, long id) -> {
            String selectedCountry = filteredCountryNames.get(position);
            int originalIndex = Arrays.asList(countryNames).indexOf(selectedCountry);
            String selectedCode = countryCodes[originalIndex];

            listener.onCountrySelected(selectedCountry, selectedCode);
            dismiss();
        });

        return new AlertDialog.Builder(context)
                .setTitle("Select Country")
                .setView(view)
                .setNegativeButton("Cancel", null)
                .create();
    }

    private void filterCountries(String query) {
        filteredCountryNames.clear();
        for (String name : countryNames) {
            if (name.toLowerCase().contains(query.toLowerCase())) {
                filteredCountryNames.add(name);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
