package com.otc.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.otc.model.request.Device;
import com.otc.model.request.Header;
import com.otc.model.request.InitializeRequest;
import com.otc.model.response.InitializeResponse;
import com.otc.ui.util.UtilOtc;
import com.pax.jemv.demo.R;
import com.pax.jemv.device.DeviceManager;
import com.pax.tradepaypw.DeviceImplNeptune;
import com.pax.tradepaypw.SwingCardActivity;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;


public class OrderActivity extends AppCompatActivity {

    private static final String TAG = "OrderActivity";

    @BindView(R.id.btn_order1_negative)
    Button btnOrder1Negative;
    @BindView(R.id.et_count1)
    EditText etCount1;
    @BindView(R.id.btn_order1_plus)
    Button btnOrder1Plus;
    @BindView(R.id.btn_order2_negative)
    Button btnOrder2Negative;
    @BindView(R.id.et_count2)
    EditText etCount2;
    @BindView(R.id.btn_order2_plus)
    Button btnOrder2Plus;
    @BindView(R.id.btn_pagar)
    Button btnPagar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.layout_progress)
    LinearLayout layoutProgress;
    @BindView(R.id.btn_order3_negative)
    Button btnOrder3Negative;
    @BindView(R.id.et_count3)
    EditText etCount3;
    @BindView(R.id.btn_order3_plus)
    Button btnOrder3Plus;
    @BindView(R.id.btn_order4_negative)
    Button btnOrder4Negative;
    @BindView(R.id.et_count4)
    EditText etCount4;
    @BindView(R.id.btn_order4_plus)
    Button btnOrder4Plus;

    private double precioOrder1 = 25.0;
    private double precioOrder2 = 46.0;
    private double precioOrder3 = 6.0;
    private double precioOrder4 = 4.0;

    private static double precioTotal = 0.0;
    private int order1 = 0;
    private int order2 = 0;
    private int order3 = 0;
    private int order4 = 0;

    //**** pax
    private int purchaseNumber = 0;
    SharedPreferences prefsPax;
    public InitializeResponse initializeResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        initData();
        accessToken();

        btnOrder1Negative.setOnClickListener(view -> {
            if (order1 > 0) {
                order1--;
            }

            precioTotal = (precioOrder1 * order1) + (precioOrder2 * order2) + (precioOrder3 * order3)+ (precioOrder4 * order4);

            etCount1.setText(order1 + "");
            btnPagar.setText(String.format("Pagar S/ %s", UtilOtc.formatAmount(precioTotal)));
        });

        btnOrder1Plus.setOnClickListener(view -> {
            order1++;

            precioTotal = (precioOrder1 * order1) + (precioOrder2 * order2) + (precioOrder3 * order3)+ (precioOrder4 * order4);

            etCount1.setText(order1 + "");
            btnPagar.setText(String.format("Pagar S/ %s", UtilOtc.formatAmount(precioTotal)));
        });

        //---------------------------------------------

        btnOrder2Negative.setOnClickListener(view -> {
            if (order2 > 0) {
                order2--;
            }

            precioTotal = (precioOrder1 * order1) + (precioOrder2 * order2) + (precioOrder3 * order3)+ (precioOrder4 * order4);

            etCount2.setText(order2 + "");
            btnPagar.setText(String.format("Pagar S/ %s", UtilOtc.formatAmount(precioTotal)));
        });

        btnOrder2Plus.setOnClickListener(view -> {
            order2++;

            precioTotal = (precioOrder1 * order1) + (precioOrder2 * order2) + (precioOrder3 * order3)+ (precioOrder4 * order4);

            etCount2.setText(order2 + "");
            btnPagar.setText(String.format("Pagar S/ %s", UtilOtc.formatAmount(precioTotal)));
        });

        //----------------------------------------------

        btnOrder3Negative.setOnClickListener(view -> {
            if (order3 > 0) {
                order3--;
            }

            precioTotal = (precioOrder1 * order1) + (precioOrder2 * order2) + (precioOrder3 * order3)+ (precioOrder4 * order4);

            etCount3.setText(order3+ "");
            btnPagar.setText(String.format("Pagar S/ %s", UtilOtc.formatAmount(precioTotal)));
        });

        btnOrder3Plus.setOnClickListener(view -> {
            order3++;

            precioTotal = (precioOrder1 * order1) + (precioOrder2 * order2) + (precioOrder3 * order3)+ (precioOrder4 * order4);

            etCount3.setText(order3 + "");
            btnPagar.setText(String.format("Pagar S/ %s", UtilOtc.formatAmount(precioTotal)));
        });

        //----------------------------------------------

        btnOrder4Negative.setOnClickListener(view -> {
            if (order4 > 0) {
                order4--;
            }

            precioTotal = (precioOrder1 * order1) + (precioOrder2 * order2) + (precioOrder3 * order3)+ (precioOrder4 * order4);

            etCount4.setText(order4+ "");
            btnPagar.setText(String.format("Pagar S/ %s", UtilOtc.formatAmount(precioTotal)));
        });

        btnOrder4Plus.setOnClickListener(view -> {
            order4++;

            precioTotal = (precioOrder1 * order1) + (precioOrder2 * order2) + (precioOrder3 * order3)+ (precioOrder4 * order4);

            etCount4.setText(order4 + "");
            btnPagar.setText(String.format("Pagar S/ %s", UtilOtc.formatAmount(precioTotal)));
        });

        //----------------------------------------------

        btnPagar.setOnClickListener(view -> {
            pagar();
        });


    }

    private void pagar() {

        if (precioTotal > 0.0) {
            Intent intent = new Intent(this, SwingCardActivity.class);
            intent.putExtra("amount", precioTotal + "");
            intent.putExtra("initialize", initializeResponse);
            intent.putExtra("purchase", purchaseNumber);
            startActivity(intent);
        }

    }

    private void initData() {
        AndroidNetworking.initialize(getApplicationContext());
        DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());
        prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        purchaseNumber = prefsPax.getInt("purchase_number", 1006000);
        purchaseNumber++;

        SharedPreferences.Editor editor = prefsPax.edit();
        editor.putInt("purchase_number", purchaseNumber);
        editor.apply();
    }

    private void accessToken() {
        String DOMAIN = "https://culqimpos.quiputech.com/";

        layoutProgress.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
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

                        SharedPreferences.Editor editor = prefsPax.edit();
                        editor.putString("authorization", response);
                        editor.apply();

                        initialize(response);
                    }

                    @Override
                    public void onError(ANError error) {

                        layoutProgress.setVisibility(View.GONE);

                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);

                        UtilOtc.getInstance().dialogResult(OrderActivity.this, error.getErrorDetail());
                    }
                });

    }

    private void initialize(String authorization) {

        String DOMAIN = "https://culqimpos.quiputech.com/";

        layoutProgress.setVisibility(View.VISIBLE);

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        Device device = new Device();

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

                        initializeResponse = response;

                        layoutProgress.setVisibility(View.GONE);

                        if (response.getKeys() != null) {

                            // en el caso que se pida inicializar
                            Log.i(TAG, "*** Track2 getEwkDataHex Encrypt : " + response.getKeys().getEwkDataHex());
                            UtilOtc.writeKeysDataPin(response.getKeys().getEwkDataHex(), response.getKeys().getEwkPinHex());
//
                        }

                    }

                    @Override
                    public void onError(ANError error) {

                        layoutProgress.setVisibility(View.GONE);

                        UtilOtc.getInstance().dialogResult(OrderActivity.this, error.getErrorBody());

                        // handle error
                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);
                    }
                });

    }


}
