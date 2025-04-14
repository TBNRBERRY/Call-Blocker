package com.example.callblocker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.callblocker.util.BlockListManager;

import java.util.ArrayList;
import java.util.List;

public class BlockedNumbersFragment extends Fragment {

    private ArrayAdapter<String> adapter;
    private BlockListManager blockListManager;

    @Override
    public View onCreateView(LayoutInflater inflater, android.view.ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blocked_numbers, container, false);

        ListView listView = root.findViewById(R.id.block_list_view);
        EditText input = root.findViewById(R.id.block_input);
        Button addButton = root.findViewById(R.id.block_add_btn);

        blockListManager = new BlockListManager(requireContext());
        List<String> blockList = new ArrayList<>(blockListManager.getBlockList());

        adapter = new ArrayAdapter<>(requireContext(), R.layout.list_item_blocked_number, R.id.blocked_number_text, blockList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String number = adapter.getItem(position);
            showConfirmationDialog(number);
        });

        addButton.setOnClickListener(v -> {
            String number = input.getText().toString().trim();
            if (!number.isEmpty() && !blockList.contains(number)) {
                blockListManager.addNumberToBlockList(number);
                adapter.add(number);
                input.setText("");
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshBlockList();
    }

    private void refreshBlockList() {
        List<String> updatedList = new ArrayList<>(blockListManager.getBlockList());
        adapter.clear();
        adapter.addAll(updatedList);
        adapter.notifyDataSetChanged();
    }

    private void showConfirmationDialog(String number) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove Blocked Number")
                .setMessage("Are you sure you want to remove " + number + " from the block list?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    blockListManager.removeNumberFromBlockList(number);
                    adapter.remove(number);
                    Toast.makeText(getContext(), "Number removed from block list", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
