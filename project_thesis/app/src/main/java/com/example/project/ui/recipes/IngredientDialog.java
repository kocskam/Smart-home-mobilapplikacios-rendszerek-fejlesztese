package com.example.project.ui.recipes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project.R;
import com.example.project.database.RecipeDatabaseAccess;
import com.example.project.model.Importance;
import com.example.project.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientDialog extends DialogFragment {

    private EditText amount, unit;
    private Spinner importance;
    private IngredientDialogListener listener;
    private RecipeDatabaseAccess db;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ingredient_dialog_layout, null);

        db = RecipeDatabaseAccess.getInstance(getContext());

        amount = view.findViewById(R.id.dl_amount);
        unit = view.findViewById(R.id.dl_unit);
        importance = view.findViewById(R.id.dl_importance);

        initImportanceSpinner();

        importance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return showAddAlertDialog(view);
    }

    public void initImportanceSpinner() {
        List<String> importanceList = getImportance();
        ArrayAdapter<String> importanceListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, importanceList);
        importanceListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        importance.setAdapter(importanceListAdapter);
    }

    private List<String> getImportance() {
        db.open();
        List<String> allImportance = new ArrayList<>();
        List<Importance> importanceList = db.getAllImportance();
        for (Importance imp : importanceList) {
            allImportance.add(imp.value + ":" + imp.description);
        }
        db.close();
        return allImportance;
    }

    public Dialog showAddAlertDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle("Új hozzávaló")
                .setNegativeButton("Mégse", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Hozzáadás", (dialog, which) -> {
                    if (!(getIngredientFromUser() == null)) {
                        listener.passToBeAddedIngredient(getIngredientFromUser());
                    }
                    else {
                        Toast.makeText(getActivity(), "A hozzávaló neve nem lehet üres!",
                                Toast.LENGTH_LONG).show();
                    }

                });
        return builder.create();
    }

    public Ingredient getIngredientFromUser() {
            String amountString = amount.getText().toString().trim();
            String unitString = unit.getText().toString().toLowerCase().trim();
            String importanceString = importance.getSelectedItem().toString();
            return new Ingredient("", (amountString.isEmpty()) ? 0 : Float.parseFloat(amountString), unitString, Integer.parseInt(importanceString.split(":")[0]));
    }

    public interface IngredientDialogListener {
        void passToBeAddedIngredient(Ingredient ingredient);
    }

    public void setIngredientListener(IngredientDialogListener listener) {
        this.listener = listener;
    }
}
