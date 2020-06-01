package com.commerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.commerce.adapter.CategoryAdapter;
import com.commerce.adapter.GaleryListAdapter;
import com.commerce.model.local.ShopItem;
import com.commerce.model.local.ShopItemRepository;
import com.pax.jemv.demo.R;

import java.util.List;

public class GaleryListActivity extends AppCompatActivity {

    // data and adapter
    List<ShopItem> shopItemList;
    GaleryListAdapter adapter;

    // RecyclerView
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery_list);

        initData();

        initUI();

        initDataBindings();

        initActions();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_basket,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, BasketActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData()
    {
        // get place list
        shopItemList = ShopItemRepository.getWomenShopItemList();
    }

    private void initUI()
    {
        initToolbar();

        // get list adapter
        adapter = new GaleryListAdapter(shopItemList);

        // get recycler view
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initDataBindings()
    {
        // bind adapter to recycler
        recyclerView.setAdapter(adapter);
    }

    private void initActions()
    {
        adapter.setOnItemClickListener(new GaleryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ShopItem obj, int position) {
                Toast.makeText(getApplicationContext(), "Selected " + obj.name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(View view, ShopItem obj, int position) {
                Toast.makeText(getApplicationContext(), "Clicked add to cart.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMenuClick(View view, ShopItem obj, int position) {
                Toast.makeText(getApplicationContext(), "Clicked menu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //region Init Toolbar
    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.baseline_menu_black_24);

        if(toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP);
        }

        toolbar.setTitle("List 1");

        try {
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        }catch (Exception e){
            Log.e("TEAMPS","Can't set color.");
        }

        try {
            setSupportActionBar(toolbar);
        }catch (Exception e) {
            Log.e("TEAMPS","Error in set support action bar.");
        }

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }catch (Exception e) {
            Log.e("TEAMPS","Error in set display home as up enabled.");
        }

    }

}