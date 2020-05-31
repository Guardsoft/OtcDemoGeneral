package com.culqi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.culqi.adapter.SalesTodayAdapter;
import com.otc.model.request.cancel.ScopesItem;
import com.otc.model.request.retrieve.Card;
import com.otc.model.request.retrieve.Cryptography;
import com.otc.model.request.retrieve.Device;
import com.otc.model.request.retrieve.Header;
import com.otc.model.request.retrieve.Merchant;
import com.otc.model.request.retrieve.Paging;
import com.otc.model.request.retrieve.RetrieveRequest;
import com.otc.model.response.InitializeResponse;
import com.otc.model.response.retrieve.RetrieveResponse;
import com.otc.model.response.retrieve.TransactionsItem;
import com.otc.ui.util.UtilOtc;
import com.pax.jemv.demo.BuildConfig;
import com.pax.jemv.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;
import static com.culqi.SalesDetailActivity.REQUEST_OPERATION;

public class SalesTodayActivity extends AppCompatActivity {

    private static final String TAG = "SalesTodayActivity";
    public static final String REQUEST_TRANSACTION = "transaction";

    private ListView lvSalesToday;
    private Toolbar toolbar;
    private LinearLayout layoutProgress;

    //***********
    InitializeResponse initializeResponse;
    String TENANT;
    String OPERATION;
    String TRACK2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_today);

        init();
        initData();

        lvSalesToday.setOnItemClickListener((adapterView, view, i, l) -> {

            TransactionsItem item = (TransactionsItem)adapterView.getItemAtPosition(i);

            if (item.getStatus().equals("AUTHORIZED")) {
                Intent intent = new Intent(SalesTodayActivity.this, SalesDetailActivity.class);
                intent.putExtra(REQUEST_TENANT, TENANT);
                intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
                intent.putExtra(REQUEST_TRANSACTION, item);
                startActivity(intent);
            }else{
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                item.getStatus(), Toast.LENGTH_SHORT);
                toast1.show();
            }

        });

    }


    private void init(){
        lvSalesToday = findViewById(R.id.lv_sales_today);
        toolbar = findViewById(R.id.toolbar);
        layoutProgress = findViewById(R.id.layout_progress);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("TUS VENTAS DE HOY");
        }

        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),HomeCulqiActivity.class));
            finish();
        });
    }


    private void initData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initializeResponse = extras.getParcelable(REQUEST_INITIALIZE);
            TENANT = extras.getString(REQUEST_TENANT);
            OPERATION = extras.getString(REQUEST_OPERATION);
            TRACK2 = getIntent().getStringExtra("track2");
            Log.i(TAG, "initData: " + initializeResponse.toString());
        }

        SharedPreferences prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        String authorization = prefsPax.getString("authorization", "");


        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        Merchant merchant = new Merchant();
        merchant.setMerchantId(initializeResponse.getMerchant().getMerchantId());

        Device device = new Device();
        device.setTerminalId(initializeResponse.getDevice().getTerminalId());

        Paging paging = new Paging();
        paging.setPageNumber(1);
        paging.setPageSize(100);

        RetrieveRequest request = new RetrieveRequest();
        request.setHeader(header);
        request.setMerchant(merchant);
        request.setDevice(device);
        request.setPaging(paging);

        if (OPERATION.equals("search")) {

            String track2Encrypt = TRACK2;

            // crypto ------------------------------------------------------------------------------

            Cryptography crypt = null;

            if (BuildConfig.CRYPTOGRAPHY) {

                track2Encrypt = encryptDataAes(TRACK2);

                crypt = new Cryptography();
                crypt.setOwner(UtilOtc.getSerialNumber());
                crypt.setMode("DEVICE");

                List<com.otc.model.request.retrieve.ScopesItem> scopes = new ArrayList<>();

                //** scope para track2
                com.otc.model.request.retrieve.ScopesItem scopesItemTrack2
                        = new com.otc.model.request.retrieve.ScopesItem();

                scopesItemTrack2.setKeyId("data");
                scopesItemTrack2.setKeyType("DATA");
                List<String> elementstrack2 = new ArrayList<>();
                elementstrack2.add("card.track2");
                scopesItemTrack2.setElements(elementstrack2);

                scopes.add(scopesItemTrack2);

                crypt.setScopes(scopes);
            }

            request.setCryptography(crypt);

            // card --------------------------------------------------------------------------------
            Card card = new Card();
            card.setSequenceNumber("001");
            card.setTrack2(track2Encrypt);

            request.setCard(card);
        }

        layoutProgress.setVisibility(View.VISIBLE);


        String DOMAIN = "https://culqimpos.quiputech.com/";

        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.post(DOMAIN + "api.authorization/v3/culqi/retrieve/list")
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", authorization)
                .addApplicationJsonBody(request)
                .setTag("retrieve")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(RetrieveResponse.class, new ParsedRequestListener<RetrieveResponse>() {
                    @Override
                    public void onResponse(RetrieveResponse response) {

                        layoutProgress.setVisibility(View.GONE);

                        if (response.getTransactions() != null) {
                            Log.i(TAG, "onResponse: " + response.getTransactions().size());

                            if (response.getTransactions().size() > 0) {

                                SalesTodayAdapter adapter = new SalesTodayAdapter(SalesTodayActivity.this, response.getTransactions());
                                lvSalesToday.setAdapter(adapter);
                            }
                        }else{
                            Toast toast1 =
                                    Toast.makeText(getApplicationContext(),
                                            "Sin datos", Toast.LENGTH_SHORT);
                            toast1.show();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        layoutProgress.setVisibility(View.GONE);
                        // handle error
                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);
                    }
                });

    }

    private String encryptDataAes(String dataIn){
//        dataIn = String.format("%-64s", dataIn ).replace(' ', '0');
        dataIn = dataIn + "0D0D0D0D0D0D0D0D0D0D0D0D0D";
        Log.i(TAG, "*** encryptDataAes Track2 : " + dataIn);
        int slotDataTAESK10 = 10;
        return com.pax.tradepaypw.device.Device.encryptAES(dataIn, slotDataTAESK10);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),HomeCulqiActivity.class));
        finish();
    }
}
