package com.culqi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.otc.model.request.send.Device;
import com.otc.model.request.send.Header;
import com.otc.model.request.send.Merchant;
import com.otc.model.request.send.Order;
import com.otc.model.request.send.SendSmsRequest;
import com.otc.model.request.send.Voucher;
import com.otc.model.response.InitializeResponse;
import com.otc.model.response.retrieve.TransactionsItem;
import com.pax.jemv.demo.R;
import com.pax.tradepaypw.SwingCardActivity;

import java.util.UUID;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;
import static com.culqi.SalesActivity.REQUEST_AMOUNT;


public class SalesDetailActivity extends AppCompatActivity {

    private static final String TAG = "SalesDetailActivity";

    Toolbar toolbar;
    TextView tvSalesDetailCardNumber;
    TextView tvSalesDetailBanck;
    TextView tvSalesDetailAmount;
    TextView tvSalesDetailSendSms;
    TransactionsItem transactionsItem;
    InitializeResponse initializeResponse;
    String tenant;
    Button btnSalesCancel;
    Button btnSignature;
    LinearLayout layoutProgress;


    public static final String REQUEST_TRANSACTION = "transaction";
    public static final String REQUEST_OPERATION = "operation";

    String authorization;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_detail);

        toolbar = findViewById(R.id.toolbar);
        tvSalesDetailCardNumber = findViewById(R.id.tv_sales_detail_card_number);
        tvSalesDetailBanck = findViewById(R.id.tv_sales_detail_bank);
        tvSalesDetailAmount = findViewById(R.id.tv_sales_detail_amount);
        tvSalesDetailSendSms = findViewById(R.id.tv_sales_detail_send_sms);

        btnSignature = findViewById(R.id.btn_signature);
        btnSalesCancel = findViewById(R.id.btn_sales_detail_cancel);
        layoutProgress = findViewById(R.id.layout_progress);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("DETALLE DE LA VENTA");
        }

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        SharedPreferences prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        authorization = prefsPax.getString("authorization", "");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            transactionsItem = extras.getParcelable(REQUEST_TRANSACTION);
            initializeResponse = extras.getParcelable(REQUEST_INITIALIZE);
            tenant = extras.getString(REQUEST_TENANT);
        }

        tvSalesDetailAmount.setText("S/ " + transactionsItem.getOrderAmount());
        tvSalesDetailCardNumber.setText(transactionsItem.getLast4digits());
        tvSalesDetailBanck.setText(transactionsItem.getAcquirer());

        btnSalesCancel.setOnClickListener(view -> {

            Intent intent = new Intent(this, SwingCardActivity.class);
            intent.putExtra(REQUEST_TENANT, tenant);
            intent.putExtra(REQUEST_OPERATION, "cancel");
            intent.putExtra(REQUEST_AMOUNT, transactionsItem.getOrderAmount() + "");
            intent.putExtra(REQUEST_TRANSACTION, transactionsItem);
            intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
            startActivity(intent);

        });


        tvSalesDetailSendSms.setOnClickListener(view ->{

            Intent intent = new Intent(this, SendSmsActivity.class);
            intent.putExtra(REQUEST_TENANT, tenant);
            intent.putExtra(REQUEST_AMOUNT, transactionsItem.getOrderAmount() + "");
            intent.putExtra(REQUEST_TRANSACTION, transactionsItem);
            intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
            startActivity(intent);

        });


        btnSignature.setOnClickListener(view ->{
            Intent intent = new Intent(this, SignatureActivity.class);
            startActivity(intent);
        });
    }


}
