package com.otc.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.otc.model.request.Header;
import com.otc.model.request.InitializeRequest;
import com.otc.model.response.InitializeResponse;
import com.otc.ui.util.UtilOtc;
import com.pax.app.TradeApplication;
import com.pax.jemv.demo.R;
import com.pax.jemv.device.DeviceManager;
import com.pax.tradepaypw.DeviceImplNeptune;
import com.pax.tradepaypw.SwingCardActivity;
import com.pax.tradepaypw.VersionActivity;
import com.pax.tradepaypw.ViewParamActivity;
import com.pax.tradepaypw.device.Device;
import com.pax.tradepaypw.pay.Constants;
import com.pax.tradepaypw.utils.EnterAmountTextWatcher;
import com.pax.tradepaypw.utils.KeyBoardUtils;
import com.pax.tradepaypw.view.CustomEditText;
import com.pax.tradepaypw.view.SoftKeyboardPosStyle;
import com.pax.tradepaypw.view.dialog.AdsDialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Route;

public class MainOtcActivity extends AppCompatActivity {

    private static final String TAG = "MainOtcActivity";

    private static final int KEY_BOARD_CANCEL = 1;
    private static final int KEY_BOARD_OK = 2;
    private static final String AID_FILE = "aid.ini";
    private static final String CAPK_FILE = "capk.ini";

    private CustomEditText edtAmount; // 金额输入框
    private SoftKeyboardPosStyle softKeyboard; // 软键盘
    private FrameLayout flkeyBoardContainer;
    private LinearLayout llMenu;
    private VideoView videoView;
    private TextView purchaseNumberView;

    private AdsDialog dialog;

    //**** pax
    private int purchaseNumber = 0;
    SharedPreferences prefsPax;
    private LinearLayout layoutProgress;
    public InitializeResponse initializeResponse;


    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KEY_BOARD_OK:
                    String amount = edtAmount.getText().toString().trim();
                    if (amount != null && !amount.equals("0.00") && !amount.equals("")) {
                        KeyBoardUtils.hide(MainOtcActivity.this, flkeyBoardContainer);

                        Log.i(TAG, "handleMessage: IR A LEER CARD");

                        Intent intent = new Intent(MainOtcActivity.this, SwingCardActivity.class);
                        intent.putExtra("amount", amount);
                        intent.putExtra("initialize", initializeResponse);
                        intent.putExtra("purchase", purchaseNumber);
                        startActivity(intent);
                    }
                    break;
                case KEY_BOARD_CANCEL:
                    edtAmount.setText("");
                    KeyBoardUtils.hide(MainOtcActivity.this, flkeyBoardContainer);
                    llMenu.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_otc);

        DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());

//        Device.writeTMK(TradeApplication.getConvert().strToBcd("1234567890123456", IConvert.EPaddingPosition.PADDING_LEFT));
//        Device.writeTPK(TradeApplication.getConvert().strToBcd("1234567890123456", IConvert.EPaddingPosition.PADDING_LEFT), null);
//        Device.writeTIKFuc(TradeApplication.getConvert().strToBcd("1234567890123456", IConvert.EPaddingPosition.PADDING_LEFT), TradeApplication.getConvert().strToBcd("0000000001", IConvert.EPaddingPosition.PADDING_LEFT));
//        Log.i("writeKey", " load default KEY into PED");

        initView();
        setListeners();
        initData();
        accessToken();
    }

    private void initData() {
        AndroidNetworking.initialize(getApplicationContext());
        DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());
        prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE| Context.MODE_MULTI_PROCESS);
        purchaseNumber = prefsPax.getInt("purchase_number", 1006000);
        purchaseNumber++;

        SharedPreferences.Editor editor = prefsPax.edit();
        editor.putInt("purchase_number", purchaseNumber);
        editor.apply();

        purchaseNumberView.setText("Pedido N° " + purchaseNumber);
    }

    private void accessToken() {
        String DOMAIN = "https://culqimpos.quiputech.com/";

        layoutProgress.setVisibility(View.VISIBLE);

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

                        UtilOtc.getInstance().dialogResult(MainOtcActivity.this, error.getErrorDetail());
                    }
                });

    }

    private void initialize(String authorization) {

        String DOMAIN = "https://culqimpos.quiputech.com/";

        layoutProgress.setVisibility(View.VISIBLE);

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        com.otc.model.request.Device device = new com.otc.model.request.Device();

        String serialNumber = UtilOtc.getSerialNumber();
        Log.i(TAG, "getSerialNumber: " + serialNumber);

        device.setSerialNumber(serialNumber);
        //slot para pinblock
        if (Device.getKCV_TDK((byte)5) != null) {
            device.setReloadKeys(false);
        }else{
            device.setReloadKeys(true);
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

                        initializeResponse = response;

                        layoutProgress.setVisibility(View.GONE);

                        if (response.getKeys() != null) {

                            // en el caso que se pida inicializar

                            Log.i(TAG, "*** Track2 getEwkDataHex Encrypt : " + response.getKeys().getEwkDataHex());
                            UtilOtc.writeKeysDataPin(response.getKeys().getEwkDataHex(), response.getKeys().getEwkPinHex());
//
                        }else{

                            // en el caso de ya este inicializado
                        }

                    }

                    @Override
                    public void onError(ANError error) {

                        layoutProgress.setVisibility(View.GONE);

                        UtilOtc.getInstance().dialogResult(MainOtcActivity.this, error.getErrorBody());

                        // handle error
                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);
                    }
                });

    }

    private void initView() {
        edtAmount = (CustomEditText) findViewById(R.id.amount_edtext);
        // 金额输入框处理
        edtAmount.setInputType(InputType.TYPE_NULL);
        edtAmount.setIMEEnabled(false, true);
        flkeyBoardContainer = (FrameLayout) findViewById(R.id.fl_trans_softkeyboard);

        softKeyboard = (SoftKeyboardPosStyle) findViewById(R.id.soft_keyboard_view);

        llMenu = (LinearLayout) findViewById(R.id.ll_menu);
        videoView = (VideoView) findViewById(R.id.video);

        //**** pax
        layoutProgress = findViewById(R.id.layout_progress);
        purchaseNumberView = findViewById(R.id.purchaseNumber);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {

        softKeyboard.setOnItemClickListener((v, index) -> {
            if (index == KeyEvent.KEYCODE_ENTER) {
                handler.sendEmptyMessage(KEY_BOARD_OK);
            } else if (index == Constants.KEY_EVENT_CANCEL) {
                handler.sendEmptyMessage(KEY_BOARD_CANCEL);
            }
        });

        edtAmount.setOnTouchListener((v, event) -> {
            edtAmount.setFocusable(true);
            KeyBoardUtils.show(MainOtcActivity.this, flkeyBoardContainer);
            llMenu.setVisibility(View.GONE);
            return false;
        });

        edtAmount.addTextChangedListener(new EnterAmountTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        resetUI();
    }

    private void resetUI() {
        edtAmount.setText("");
        edtAmount.setFocusable(false);
        edtAmount.setFocusableInTouchMode(true);
        edtAmount.requestFocus();
//        KeyBoardUtils.hide(MainOtcActivity.this, flkeyBoardContainer);
        llMenu.setVisibility(View.VISIBLE);
    }

    public void aidClick(View view) {
        readData(AID_FILE, "aid");
    }

    public void capkClick(View view) {
        readData(CAPK_FILE, "capk");
    }

    public void versionClick(View view) {
        Intent intent = new Intent(this, VersionActivity.class);
        startActivity(intent);
    }

    private void readData(String fileName, String key) {

        byte[] bytes = new byte[1024];
        int len = -1;
        StringBuffer buffer = new StringBuffer();

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open(fileName);
            while ((len = inputStream.read(bytes)) != -1) {
                buffer.append(new String(bytes, 0, len));
            }
            inputStream.close();

            String datas = String.valueOf(buffer);

            Intent intent = new Intent(this, ViewParamActivity.class);
            intent.putExtra(key, datas);
            startActivity(intent);
        } catch (IOException e) {
            Log.e("readData", e.getMessage());
        }
    }
}
