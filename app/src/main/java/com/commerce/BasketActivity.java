package com.commerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.commerce.adapter.BasketAdapter;
import com.commerce.adapter.CategoryAdapter;
import com.commerce.model.local.Basket;
import com.commerce.model.local.BasketItemRepository;
import com.commerce.model.local.ShopItem;
import com.culqi.MainCulqiActivity;
import com.culqi.SalesActivity;
import com.otc.model.request.Header;
import com.otc.model.request.InitializeRequest;
import com.otc.model.response.InitializeResponse;
import com.otc.ui.util.UtilOtc;
import com.pax.jemv.demo.R;
import com.pax.tradepaypw.SwingCardActivity;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Route;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;

public class BasketActivity extends AppCompatActivity {

    private static final String TAG = "BasketActivity";

    // data and adapter
    List<Basket> basketList;
    BasketAdapter adapter;

    // RecyclerView
    RecyclerView recyclerView;

    Button checkoutButton;
    TextView totalPriceTextView;
    LinearLayout layoutProgress;
    SharedPreferences prefsPax;
    long purchaseNumber = 0L;

    double SUBTOTAL = 0.0;

    public static final String REQUEST_AMOUNT = "amount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        initData();

        initUI();

        initDataBindings();

        initActions();

    }

    private void initData() {
        // get place list
        basketList = BasketItemRepository.getBusketItemList();
    }

    private void initUI() {
        initToolbar();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long time = timestamp.getTime()/1000;
        prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE| Context.MODE_MULTI_PROCESS);
        purchaseNumber = prefsPax.getLong("purchase_number", time);
        purchaseNumber++;

        // get list adapter
        adapter = new BasketAdapter(basketList);

        // get recycler view
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        checkoutButton = findViewById(R.id.checkoutButton);

        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        layoutProgress = findViewById(R.id.layout_progress);
    }

    private void initDataBindings() {
        // bind adapter to recycler
        recyclerView.setAdapter(adapter);

        try {
            int total = 0;
            for (int i = 0; i < basketList.size(); i++) {
                Basket basket = basketList.get(i);
                total += Integer.parseInt(basket.price);
            }

            String totalCost = basketList.get(0).currency + " " + total;
            totalPriceTextView.setText(totalCost);
        }catch (Exception ignored) { }
    }

    private void initActions() {
        adapter.setOnItemClickListener(new BasketAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Basket obj, int position) {
                Toast.makeText(getApplicationContext(), "Clicked " + obj.name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(View view, Basket obj, int position) {
                Toast.makeText(getApplicationContext(), "Clicked Delete. ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPriceChange(String currency, int subTotal) {
                String totalStr = currency + " " + subTotal;
                SUBTOTAL = subTotal;
                totalPriceTextView.setText(totalStr);
            }
        });

        checkoutButton.setOnClickListener(view ->{
            readCard();
        });
    }

    //region Init Toolbar
    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.icon_back);

        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP);
        }

        toolbar.setTitle("Basket 3");

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

    private void readCard() {
        layoutProgress.setVisibility(View.VISIBLE);
        accessToken();
    }

    private void accessToken() {
        String DOMAIN = "https://culqimpos.quiputech.com/";

        layoutProgress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        OkHttpClient client = new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, okhttp3.Response response) throws IOException {
                String credential = Credentials.basic("integracion@otcperu.com", "Peru2019$$");
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();

        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.get(DOMAIN + "api.security/v2/culqi/security/accessToken")
                .setOkHttpClient(client)
                .setTag("accessToken")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        SharedPreferences.Editor editor = prefsPax.edit();
                        editor.putString("authorization", response);
                        editor.apply();

                        initialize(response);
                    }

                    @Override
                    public void onError(ANError error) {

                        layoutProgress.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);

                        UtilOtc.getInstance().dialogResult(BasketActivity.this, error.getErrorDetail());
                    }
                });

    }

    private void initialize(String authorization) {

        boolean reloadKeys = false;


        String DOMAIN = "https://culqimpos.quiputech.com/";

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        com.otc.model.request.Device device = new com.otc.model.request.Device();

        String serialNumber = UtilOtc.getSerialNumber();
        Log.i(TAG, "getSerialNumber: " + serialNumber);

        device.setSerialNumber(serialNumber);

        reloadKeys = prefsPax.getBoolean("reloadkeys", true);
        if (reloadKeys) {
            //slot para pinblock
            device.setReloadKeys(true);
        }else{
            device.setReloadKeys(false);
        }


        InitializeRequest request = new InitializeRequest();
        request.setHeader(header);
        request.setDevice(device);

        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.post(DOMAIN + "api.terminal/v3/culqi/management/initialize")
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", authorization)
                .addApplicationJsonBody(request)
                .setTag("initialize")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(InitializeResponse.class, new ParsedRequestListener<InitializeResponse>() {
                    @Override
                    public void onResponse(InitializeResponse response) {

                        SharedPreferences.Editor editor = prefsPax.edit();
                        editor.putBoolean("reloadkeys", false);
                        editor.apply();

                        layoutProgress.setVisibility(View.GONE);

                        if (response.getKeys() != null) {

                            UtilOtc.writeWorkKeys(
                                    response.getKeys().getEwkDataHex(),
                                    response.getKeys().getEwkPinHex(),
                                    response.getKeys().getEwkMacSignature());
                        }

                        Intent intent = new Intent(BasketActivity.this, SwingCardActivity.class);
                        intent.putExtra(REQUEST_TENANT, "culqi");
                        intent.putExtra(REQUEST_AMOUNT, SUBTOTAL +"");
                        intent.putExtra(REQUEST_INITIALIZE, response);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(ANError error) {

                        layoutProgress.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        UtilOtc.getInstance().dialogResult(BasketActivity.this, error.getErrorBody());

                        // handle error
                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);
                    }
                });
    }

}