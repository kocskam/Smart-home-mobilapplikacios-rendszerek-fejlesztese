package com.example.project.ui.shopping;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.database.ShoppingDBAccess;
import com.example.project.model.GroceryItem;
import com.example.project.ui.shopping.adapter.GroceryRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;

import java.util.List;

public class ShoppingFragment extends Fragment implements GroceryDialog.GroceryDialogListener, GroceryRecyclerAdapter.GroceryAdapterListener {

    private static Toast t;
    private static Context context;

    private RecyclerView groceryRecycler;
    private FloatingActionButton fabAdd;

    private List<GroceryItem> groceries;

    private ShoppingDBAccess db;
    private GroceryRecyclerAdapter groceryRecyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        db = ShoppingDBAccess.getInstance(context);

        fabAdd = view.findViewById(R.id.famAddGro);
        groceryRecycler = view.findViewById(R.id.groceryList);

        setupListView();

        fabAdd.setOnClickListener(v -> showGroceryDialog());
    }

    public void setupListView() {
        db.open();
        groceries = db.getGroceries();

        groceryRecyclerAdapter = new GroceryRecyclerAdapter(groceries, context);
        groceryRecyclerAdapter.setGroceryAdapterListener(ShoppingFragment.this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(groceryRecycler.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        groceryRecycler.addItemDecoration(dividerItemDecoration);
        groceryRecycler.setLayoutManager(layoutManager);
        groceryRecycler.setItemAnimator(new DefaultItemAnimator());
        groceryRecycler.setAdapter(groceryRecyclerAdapter);

        db.close();
    }

    public void showGroceryDialog() {
        GroceryDialog groceryDialog = new GroceryDialog();
        assert getFragmentManager() != null;
        groceryDialog.show(getFragmentManager(), "Dialog");
        groceryDialog.setGroceryDialogListener(ShoppingFragment.this);
    }

    public void reloadListView() {
        db.open();
        groceries = db.getGroceries();
        groceryRecyclerAdapter = new GroceryRecyclerAdapter(groceries, context);
        groceryRecyclerAdapter.setGroceryAdapterListener(ShoppingFragment.this);
        groceryRecycler.setAdapter(groceryRecyclerAdapter);
        db.close();
    }

    @Override
    public void passToBeAddedGroceryItem(GroceryItem groceryItem) {
        if (groceryItem == null) {
            makeToast("A termék neve nem lehet üres!");
        }
        else {
            db.open();
            long id = db.addGrocery(groceryItem);
            groceryItem.id = (int) id;
            reloadListView();
            db.close();
        }
    }

    @Override
    public void passToBeDeletedGroceryItem(GroceryItem groceryItem) {
        db.open();
        db.deleteGrocery(groceryItem.id);
        setupListView();
        db.close();
    }

    @Override
    public void passToBeUpdatedGroceryItem(GroceryItem groceryItem) {
        if (groceryItem == null) {
            makeToast("A termék neve nem lehet üres!");
        }
        else {
            db.open();
            db.updateGrocery(groceryItem);
            setupListView();
            db.close();
        }
    }

    public static void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        t.show();
    }

}