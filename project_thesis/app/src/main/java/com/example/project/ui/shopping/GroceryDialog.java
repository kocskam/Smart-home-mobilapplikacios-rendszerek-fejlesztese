package com.example.project.ui.shopping;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project.R;
import com.example.project.model.GroceryItem;

public class GroceryDialog extends DialogFragment {

    private EditText item, amount, unit, description;
    private GroceryDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.grocery_dialog_layout, null);

        item = view.findViewById(R.id.sl_name);
        amount = view.findViewById(R.id.sl_amount);
        unit = view.findViewById(R.id.sl_unit);
        description = view.findViewById(R.id.sl_description);

        return showAddAlertDialog(view);
    }

    public Dialog showAddAlertDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle("Új termék hozzáadása")
                .setNegativeButton("Mégse", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Hozzáadás", (dialog, which) -> listener.passToBeAddedGroceryItem(getGroceryItemFromUser()));
        return builder.create();
    }

    public GroceryItem getGroceryItemFromUser() {
        if (!(isEmpty(item))) {
            String itemString = item.getText().toString().toLowerCase().trim();
            String amountString = amount.getText().toString().trim();
            String unitString = unit.getText().toString().toLowerCase().trim();
            String descriptionString = description.getText().toString();

            return new GroceryItem(null, itemString, transformToFloat(amountString), unitString, descriptionString);
        }
        return null;
    }

    private boolean isEmpty(EditText text) {
        return text.getText().toString().trim().length() == 0;
    }

    public float transformToFloat(String amountString) {
        if (isEmpty(amount)) {
            return Float.parseFloat("0");
        }
        return Float.parseFloat(amountString);
    }

    public void setGroceryDialogListener(GroceryDialogListener groceryDialogListener) {
        this.listener = groceryDialogListener;
    }

    public interface GroceryDialogListener {
        void passToBeAddedGroceryItem(GroceryItem groceryItem);
    }

}
