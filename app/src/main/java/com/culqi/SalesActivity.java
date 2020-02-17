package com.culqi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.otc.model.response.InitializeResponse;
import com.otc.ui.MainOtcActivity;
import com.otc.ui.SwingCardOtcActivity;
import com.pax.jemv.demo.R;
import com.pax.jemv.device.DeviceManager;
import com.pax.tradepaypw.DeviceImplNeptune;
import com.pax.tradepaypw.SwingCardActivity;
import com.pax.tradepaypw.pay.Constants;
import com.pax.tradepaypw.utils.EnterAmountTextWatcher;
import com.pax.tradepaypw.utils.KeyBoardUtils;
import com.pax.tradepaypw.view.CustomEditText;
import com.pax.tradepaypw.view.SoftKeyboardPosStyle;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;

public class SalesActivity extends AppCompatActivity {

    private static final String TAG = "SalesActivity";
    public static final String REQUEST_AMOUNT = "amount";


    private static final int KEY_BOARD_CANCEL = 1;
    private static final int KEY_BOARD_OK = 2;
    private FrameLayout flkeyBoardContainer;
    private SoftKeyboardPosStyle softKeyboard;
    CustomEditText etAmount;

    //***********
    InitializeResponse initializeResponse;
    String TENANT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());

        initView();
        initData();
        setListeners();

    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initializeResponse = extras.getParcelable(REQUEST_INITIALIZE);
            TENANT = extras.getString(REQUEST_TENANT);
            Log.i(TAG, "initData: " + initializeResponse.toString());
        }
    }

    private void initView() {

        etAmount = findViewById(R.id.et_amount);
        etAmount.setInputType(InputType.TYPE_NULL);
        etAmount.setIMEEnabled(false, true);
        flkeyBoardContainer = findViewById(R.id.fl_trans_softkeyboard);
        softKeyboard = findViewById(R.id.soft_keyboard_view);
    }


    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KEY_BOARD_OK:
                    String amount = etAmount.getText().toString().trim();
                    if (amount != null && !amount.equals("0.00") && !amount.equals("")) {
                        KeyBoardUtils.hide(SalesActivity.this, flkeyBoardContainer);

                        Intent intent = new Intent(SalesActivity.this, SwingCardActivity.class);
                        intent.putExtra(REQUEST_TENANT, TENANT);
                        intent.putExtra(REQUEST_AMOUNT, amount);
                        intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
                        startActivity(intent);
                    }
                    break;
                case KEY_BOARD_CANCEL:
                    etAmount.setText("");
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {

        softKeyboard.setOnItemClickListener((v, index) -> {
            if (index == KeyEvent.KEYCODE_ENTER) {
                handler.sendEmptyMessage(KEY_BOARD_OK);
            } else if (index == Constants.KEY_EVENT_CANCEL) {
                handler.sendEmptyMessage(KEY_BOARD_CANCEL);
            }
        });

        etAmount.setOnTouchListener((v, event) -> {
            etAmount.setFocusable(true);
            KeyBoardUtils.show(SalesActivity.this, flkeyBoardContainer);
            return false;
        });

        etAmount.addTextChangedListener(new EnterAmountTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
            }
        });

    }
}
