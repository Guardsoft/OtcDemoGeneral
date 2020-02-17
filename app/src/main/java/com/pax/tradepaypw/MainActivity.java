package com.pax.tradepaypw;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.VideoView;

import com.pax.app.IConvert;
import com.pax.app.TradeApplication;
import com.pax.jemv.demo.R;
import com.pax.jemv.device.DeviceManager;
import com.pax.tradepaypw.device.Device;
import com.pax.tradepaypw.pay.Constants;
import com.pax.tradepaypw.utils.EnterAmountTextWatcher;
import com.pax.tradepaypw.utils.KeyBoardUtils;
import com.pax.tradepaypw.view.CustomEditText;
import com.pax.tradepaypw.view.SoftKeyboardPosStyle;
import com.pax.tradepaypw.view.dialog.AdsDialog;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int KEY_BOARD_CANCEL = 1;
    private static final int KEY_BOARD_OK = 2;
    private static final String AID_FILE = "aid.ini";
    private static final String CAPK_FILE = "capk.ini";

    private CustomEditText edtAmount; // 金额输入框
    private SoftKeyboardPosStyle softKeyboard; // 软键盘
    private FrameLayout flkeyBoardContainer;
    private LinearLayout llMenu;
    private VideoView videoView;

    private AdsDialog dialog;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KEY_BOARD_OK:
                    String amount = edtAmount.getText().toString().trim();
                    if (amount != null && !amount.equals("0.00") && !amount.equals("")) {
                        KeyBoardUtils.hide(MainActivity.this, flkeyBoardContainer);
                        Intent intent = new Intent(MainActivity.this, SwingCardActivity.class);
                        intent.putExtra("amount", amount);
                        startActivity(intent);
                    }
                    break;
                case KEY_BOARD_CANCEL:
                    edtAmount.setText("");
                    KeyBoardUtils.hide(MainActivity.this, flkeyBoardContainer);
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
        setContentView(R.layout.activity_main);

        DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());

        Device.writeTMK(TradeApplication.getConvert().strToBcd("1234567890123456", IConvert.EPaddingPosition.PADDING_LEFT));
        Device.writeTPK(TradeApplication.getConvert().strToBcd("1234567890123456", IConvert.EPaddingPosition.PADDING_LEFT), null);
        Device.writeTIKFuc(TradeApplication.getConvert().strToBcd("1234567890123456", IConvert.EPaddingPosition.PADDING_LEFT), TradeApplication.getConvert().strToBcd("0000000001", IConvert.EPaddingPosition.PADDING_LEFT));
        Log.i("writeKey", " load default KEY into PED");
        initView();
        setListeners();
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

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        softKeyboard.setOnItemClickListener(new SoftKeyboardPosStyle.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int index) {
                if (index == KeyEvent.KEYCODE_ENTER) {
                    handler.sendEmptyMessage(KEY_BOARD_OK);
                } else if (index == Constants.KEY_EVENT_CANCEL) {
                    handler.sendEmptyMessage(KEY_BOARD_CANCEL);
                }
            }
        });

        edtAmount.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtAmount.setFocusable(true);
                KeyBoardUtils.show(MainActivity.this, flkeyBoardContainer);
                llMenu.setVisibility(View.GONE);
                return false;
            }
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
        KeyBoardUtils.hide(MainActivity.this, flkeyBoardContainer);
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
