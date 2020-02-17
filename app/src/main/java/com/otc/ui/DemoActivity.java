package com.otc.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.culqi.MainCulqiActivity;
import com.otc.manager.PrinterManager;
import com.otc.ui.util.UtilOtc;
import com.pax.app.IConvert;
import com.pax.app.TradeApplication;
import com.pax.dal.IPed;
import com.pax.dal.entity.EPedDesMode;
import com.pax.dal.entity.EPedType;
import com.pax.dal.exceptions.PedDevException;
import com.pax.jemv.demo.R;
import com.pax.jemv.device.DeviceManager;
import com.pax.tradepaypw.DeviceImplNeptune;
import com.pax.tradepaypw.SwingCardActivity;
import com.pax.tradepaypw.device.Device;

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
            startActivity(new Intent(DemoActivity.this, MainOtcActivity.class));
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

        Button btnValidateKeys= findViewById(R.id.btn_validate_keys);
        btnValidateKeys.setOnClickListener(v -> {
            if (UtilOtc.validateKeys()) {
                Toast.makeText(this,"LLAVES REGISTRADAS",
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"SIN REGISTRADAS",
                        Toast.LENGTH_SHORT).show();
            }
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


    public static byte[] enCBC(String dataInStr, int slotTdk) {

        byte[] dataIn = TradeApplication
                .getConvert()
                .strToBcd(dataInStr, IConvert.EPaddingPosition.PADDING_LEFT);

        try {
            //llave tdk
            IConvert convert = TradeApplication.getConvert();
            IPed iPed = TradeApplication.getDal().getPed(EPedType.INTERNAL);
            byte[] plianBlock = iPed.calcDes((byte) slotTdk, dataIn, EPedDesMode.ENCRYPT);

            Log.i(TAG, "plainBlock: " + convert.bcdToStr(plianBlock));
            return plianBlock;

        } catch (PedDevException e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    public static byte[] desCBC(String dataInStr, int slotTdk) {

        byte[] dataIn = TradeApplication
                .getConvert()
                .strToBcd(dataInStr, IConvert.EPaddingPosition.PADDING_LEFT);

        try {
            //llave tdk
            IConvert convert = TradeApplication.getConvert();
            IPed iPed = TradeApplication.getDal().getPed(EPedType.INTERNAL);
            byte[] plianBlock = iPed.calcDes((byte) slotTdk, dataIn, EPedDesMode.DECRYPT);

            Log.i(TAG, "plainBlock: " + convert.bcdToStr(plianBlock));
            return plianBlock;

        } catch (PedDevException e) {
            Log.e(TAG, "", e);
        }

        return null;
    }


}
