package com.culqi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.otc.model.request.Header;
import com.otc.model.request.InitializeRequest;
import com.otc.model.response.InitializeResponse;
import com.otc.ui.DemoActivity;
import com.otc.ui.MainOtcActivity;
import com.otc.ui.util.UtilOtc;
import com.pax.app.TradeApplication;
import com.pax.jemv.demo.R;
import com.pax.jemv.device.DeviceManager;
import com.pax.tradepaypw.DeviceImplNeptune;
import com.pax.tradepaypw.device.Device;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Route;

public class MainCulqiActivity extends AppCompatActivity {

    private static final String TAG = "MainCulqiActivity";
    public static final String REQUEST_INITIALIZE = "initialize";
    public static final String REQUEST_TENANT = "tenant";


    private ImageView ivLogo;
    private ImageView ivLogo2;
    private ImageView ivLogoLoading;
    private TextView tvStart;
    private RelativeLayout layoutInitial;
    private LinearLayout layoutProgress;
    private String TENANT = "";

    //**** pax
    private long purchaseNumber = 0L;
    SharedPreferences prefsPax;

    public InitializeResponse initializeResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_culqi);

        initView();

        initData();

        accessToken();

        tvStart.setOnClickListener(v -> {

            Intent intent = new Intent(this, HomeCulqiActivity.class);
            intent.putExtra(REQUEST_TENANT, TENANT);
            intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
            startActivity(intent);
        });

    }

    private void initView() {
        tvStart = findViewById(R.id.tv_start);
        tvStart.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        layoutInitial = findViewById(R.id.layout_1);
        layoutProgress = findViewById(R.id.layout_progress);
        ivLogoLoading = findViewById(R.id.iv_logo_loading);
        ivLogo = findViewById(R.id.iv_logo);
        ivLogo2 = findViewById(R.id.iv_logo_2);

    }

    private void initData() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long time = timestamp.getTime()/1000;

        AndroidNetworking.initialize(getApplicationContext());
        prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE| Context.MODE_MULTI_PROCESS);
        purchaseNumber = prefsPax.getLong("purchase_number", time);
        purchaseNumber++;

        SharedPreferences.Editor editor = prefsPax.edit();
        editor.putLong("purchase_number", purchaseNumber);
        editor.apply();


        ivLogo.setImageResource(R.drawable.ic_logo_culqi);
        layoutInitial.setBackgroundColor(getResources().getColor(R.color.culqi_blue));

    }

    private void accessToken() {
        String DOMAIN = "https://culqimpos.quiputech.com/";

        layoutProgress.setVisibility(View.VISIBLE);
        layoutInitial.setVisibility(View.GONE);

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

                        layoutProgress.setVisibility(View.GONE);
                        layoutInitial.setVisibility(View.VISIBLE);

                        SharedPreferences.Editor editor = prefsPax.edit();
                        editor.putString("authorization", response);
                        editor.apply();

                        initialize(response);
                    }

                    @Override
                    public void onError(ANError error) {

                        layoutProgress.setVisibility(View.GONE);
                        layoutInitial.setVisibility(View.VISIBLE);

                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);

                        UtilOtc.getInstance().dialogResult(MainCulqiActivity.this, error.getErrorDetail());
                    }
                });

    }

    private void initialize(String authorization) {

        String DOMAIN = "https://culqimpos.quiputech.com/";

        layoutProgress.setVisibility(View.VISIBLE);
        layoutInitial.setVisibility(View.GONE);

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        com.otc.model.request.Device device = new com.otc.model.request.Device();

        String serialNumber = UtilOtc.getSerialNumber();
        Log.i(TAG, "getSerialNumber: " + serialNumber);

        device.setSerialNumber(serialNumber);

        //slot para pinblock
        device.setReloadKeys(true);

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

                        layoutProgress.setVisibility(View.GONE);
                        layoutInitial.setVisibility(View.VISIBLE);

                        initializeResponse = response;
                        if (response.getKeys() != null) {

                            // en el caso que se pida inicializar
                            Log.i(TAG, "*** Track2 getEwkDataHex Encrypt : " + response.getKeys().getEwkDataHex());
                            UtilOtc.writeKeysDataPin(response.getKeys().getEwkDataHex(), response.getKeys().getEwkPinHex());
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        layoutProgress.setVisibility(View.GONE);
                        layoutInitial.setVisibility(View.VISIBLE);

                        UtilOtc.getInstance().dialogResult(MainCulqiActivity.this, error.getErrorBody());

                        // handle error
                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);
                    }
                });

    }

}
