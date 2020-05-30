package com.otc.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.culqi.MainCulqiActivity;
import com.otc.manager.PrinterManager;
import com.otc.ui.util.UtilOtc;
import com.pax.jemv.demo.R;
import com.pax.tradepaypw.SwingCardActivity;

import static com.culqi.SalesActivity.REQUEST_AMOUNT;

public class DemoActivity extends AppCompatActivity {

    private static final String TAG = "DemoActivity";

    public static final String REQUEST_TENANT = "tenant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        Button btnDemoRestaurant = findViewById(R.id.btn_demo);
        btnDemoRestaurant.setOnClickListener( v -> {
            Log.i(TAG, ": btnDemoRestaurant");
            //startActivity(new Intent(DemoActivity.this, MainOtcActivity.class));
            startActivity(new Intent(DemoActivity.this, OrderActivity.class));
        });

        Button btnOtc = findViewById(R.id.btn_otc);
        btnOtc.setOnClickListener( v -> {
            Log.i(TAG, ": btnDemoRestaurant");
            Intent intent = new Intent(this, MainCulqiActivity.class);
            intent.putExtra(REQUEST_TENANT, "otc");
            startActivity(intent);
        });


        Button btnPrint = findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(v -> {
            Log.i(TAG, ": btnPrint");
            PrinterManager manager = new PrinterManager();
            manager.print(DemoActivity.this);
        });

        Button btnCulqui = findViewById(R.id.btn_culqi);
        btnCulqui.setOnClickListener(v -> {

            Intent intent = new Intent(this, MainCulqiActivity.class);
            intent.putExtra(REQUEST_TENANT, "culqi");
            startActivity(intent);
        });

        Button btnBbva = findViewById(R.id.btn_bbva);
        btnBbva.setOnClickListener(v -> {

            Intent intent = new Intent(this, MainCulqiActivity.class);
            intent.putExtra(REQUEST_TENANT, "bbva");
            startActivity(intent);
        });

        Button btnIzipay = findViewById(R.id.btn_izipay);
        btnIzipay.setOnClickListener(v -> {

            Intent intent = new Intent(this, MainCulqiActivity.class);
            intent.putExtra(REQUEST_TENANT, "izipay");
            startActivity(intent);
        });

        Button btnVendeMas = findViewById(R.id.btn_vendemas);
        btnVendeMas.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainCulqiActivity.class);
            intent.putExtra(REQUEST_TENANT, "vendemas");
            startActivity(intent);
        });

        Button btnInject = findViewById(R.id.btn_inject_keys);
        btnInject.setOnClickListener(v -> {
           UtilOtc.injectKeys();
        });

        Button btnCleanKeys= findViewById(R.id.btn_clean_keys);
        btnCleanKeys.setOnClickListener(v -> {
            UtilOtc.cleanKeys();
        });

        Button btnValidateKeys= findViewById(R.id.btn_validate_keys);
        btnValidateKeys.setOnClickListener(v -> {
            Intent intent = new Intent(this, KeyValidateActivity.class);
            startActivity(intent);

        });

        Button btnReadEMV = findViewById(R.id.btn_read_emv);
        btnReadEMV.setOnClickListener(v -> {
            readEMV();
        });

    }

    private void readEMV() {
        Intent intent = new Intent(this, SwingCardActivity.class);
        intent.putExtra(REQUEST_AMOUNT, "15.50");
        startActivity(intent);
    }

}
