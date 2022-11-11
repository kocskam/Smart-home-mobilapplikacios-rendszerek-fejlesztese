package com.example.project.ui.shopping.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.model.GroceryItem;

import java.util.List;

public class GroceryRecyclerAdapter extends RecyclerView.Adapter<GroceryRecyclerAdapter.ViewHolder> {

    private final Context context;
    private EditText name, amount, unit, desc;
    private final List<GroceryItem> list;
    private GroceryAdapterListener listener;
    private AlertDialog.Builder builder;

    public GroceryRecyclerAdapter(List<GroceryItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public GroceryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grocery_list_row, null);
        return new ViewHolder(view);
    }
    
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GroceryRecyclerAdapter.ViewHolder holder, int position) {
        builder = new AlertDialog.Builder(context);

        holder.getItem().setText(list.get(position).name);

        if (list.get(position).amount == 0.0) {
            holder.getQty().setText(list.get(position).unit);
        }
        else {
            holder.getQty().setText(list.get(position).amount + list.get(position).unit);
        }
        holder.getDescription().setText(list.get(position).description);

        holder.modify.setOnClickListener(v -> {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View dView = layoutInflater.inflate(R.layout.grocery_dialog_layout, null);

            name = dView.findViewById(R.id.sl_name);
            amount = dView.findViewById(R.id.sl_amount);
            unit = dView.findViewById(R.id.sl_unit);
            desc = dView.findViewById(R.id.sl_description);

            setEditTexts(position);
            showModifyAlertDialog(dView, position);
        });

        holder.remove.setOnClickListener(v -> {
            GroceryItem selectedGrocery = list.get(position);
            showRemoveAlertDialog(selectedGrocery);
        });
        
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setEditTexts(int position) {
        GroceryItem groceryItem = list.get(position);
        name.setText(groceryItem.name);
        if (!(String.valueOf(groceryItem.amount).equals("0.0"))) {
            amount.setText(String.valueOf(groceryItem.amount));
        }
        unit.setText(groceryItem.unit);
        desc.setText(groceryItem.description);
    }

    public void showModifyAlertDialog(View dView, int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(dView);
        alertDialog.setPositiveButton("Mentés", (dialog, which) -> passGroceryItemAt(position));
        alertDialog.setNegativeButton("Mégse", (dialog, which) -> dialog.cancel());
        alertDialog.create().show();
    }

    public void passGroceryItemAt(int position) {
        GroceryItem newGroceryItem = getGroceryItemFromUser(list.get(position).id);
        listener.passToBeUpdatedGroceryItem(newGroceryItem);
    }

    public void showRemoveAlertDialog(GroceryItem selectedItem) {
        builder.setTitle("Megerősítés")
                .setMessage("Biztosan törli a(z) "+ selectedItem.name + " elemet?")
                .setCancelable(true)
                .setPositiveButton("Törlés", (dialog, which) -> listener.passToBeDeletedGroceryItem(selectedItem))
                .setNegativeButton("Mégse", (dialog, which) -> dialog.cancel())
                .show();
    }

    public GroceryItem getGroceryItemFromUser(int id) {
        if (!(isEmpty(name))) {
            String itemString = name.getText().toString().trim().toLowerCase();
            String amountString = amount.getText().toString().trim();
            String unitString = unit.getText().toString().trim().toLowerCase();
            String descriptionString = desc.getText().toString().trim();

            return new GroceryItem(id, itemString, transformToFloat(amountString), unitString, descriptionString);
        }
        return null;
    }

    private boolean isEmpty(TextView text) { 
        return text.getText().toString().trim().length() == 0;
    }
    
    public float transformToFloat(String amountString) {
        if (isEmpty(amount)) {
            return Float.parseFloat("0");
        }
        return Float.parseFloat(amountString);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView qty, item, description;
        private final ImageView remove, modify;

        public ViewHolder(View view) {
            super(view);
            qty = view.findViewById(R.id.l_qty);
            item = view.findViewById(R.id.l_item);
            description = view.findViewById(R.id.l_description);

            remove = view.findViewById(R.id.l_remove);
            modify = view.findViewById(R.id.l_modify);
        }

        public TextView getQty() {
            return qty;
        }

        public TextView getItem() {
            return item;
        }

        public TextView getDescription() {
            return description;
        }

    }

    public void setGroceryAdapterListener(GroceryAdapterListener groceryAdapterListener) {
        this.listener = groceryAdapterListener;
    }

    public interface GroceryAdapterListener {
        void passToBeDeletedGroceryItem(GroceryItem groceryItem);
        void passToBeUpdatedGroceryItem(GroceryItem groceryItem);
    }

}
