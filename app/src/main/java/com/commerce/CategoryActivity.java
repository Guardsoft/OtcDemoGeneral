package com.commerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.commerce.adapter.CategoryAdapter;
import com.commerce.model.local.ShopCategory;
import com.commerce.model.local.ShopCategoryRepository;
import com.pax.jemv.demo.R;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private ArrayList<ShopCategory> itemArrayList;
    private CategoryAdapter mAdapter;
    private RecyclerView recyclerView;
    int numberOfColumns = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initData();

        initUI();

        initDataBinding();

        initActions();

    }

    private void initData() {
        itemArrayList = ShopCategoryRepository.getShopCategoryList();
    }

    private void initUI() {
        initToolbar();

        mAdapter = new CategoryAdapter(itemArrayList);

        // get RecyclerView and bind
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initDataBinding() {
        recyclerView.setAdapter(mAdapter);
    }

    private void initActions() {
        mAdapter.setOnItemClickListener((view, obj, position) ->
                {
                    startActivity(new Intent(this, GaleryListActivity.class));
                }
        );
    }

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.baseline_menu_black_24);

        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP);
        }

        toolbar.setTitle("Category 2");

        try {
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        } catch (Exception e) {
            Log.e("TEAMPS", "Can't set color.");
        }

        try {
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            Log.e("TEAMPS", "Error in set support action bar.");
        }

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e("TEAMPS", "Error in set display home as up enabled.");
        }

    }
}
