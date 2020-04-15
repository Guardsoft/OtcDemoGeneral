package com.culqi.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.culqi.MainCulqiActivity;
import com.culqi.SendSmsActivity;
import com.otc.model.response.InitializeResponse;
import com.otc.model.response.authorize.AuthorizeResponse;
import com.otc.model.response.retrieve.TransactionsItem;
import com.pax.jemv.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;
import static com.culqi.SalesActivity.REQUEST_AMOUNT;
import static com.culqi.SalesDetailActivity.REQUEST_OPERATION;
import static com.culqi.SalesDetailActivity.REQUEST_TRANSACTION;
import static com.pax.tradepaypw.TradeResultActivity.REQUEST_AUTHORIZE;
import static com.pax.tradepaypw.TradeResultActivity.REQUEST_PURCHASE_NUMBER;

public class SalesVoucherActivity extends AppCompatActivity {

    @BindView(R.id.rb_voucher_true)
    RadioButton rbVoucherTrue;
    @BindView(R.id.rb_voucher_false)
    RadioButton rbVoucherFalse;

    //***********
    InitializeResponse initializeResponse;
    TransactionsItem transactionsItem;
    AuthorizeResponse authorizeResponse;
    String TENANT;
    String OPERATION;
    String TRACK2;
    String amount;
    String purchaseNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_voucher);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initializeResponse = extras.getParcelable(REQUEST_INITIALIZE);
            transactionsItem = extras.getParcelable(REQUEST_TRANSACTION);
            authorizeResponse = extras.getParcelable(REQUEST_AUTHORIZE);
            TENANT = extras.getString(REQUEST_TENANT);
            OPERATION = extras.getString(REQUEST_OPERATION);
            TRACK2 = getIntent().getStringExtra("track2");
            amount = extras.getString(REQUEST_AMOUNT);
            purchaseNumber  = extras.getString(REQUEST_PURCHASE_NUMBER);
        }

        SharedPreferences prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        String authorization = prefsPax.getString("authorization", "");

    }

    public void sendVoucher(View view) {

        if (rbVoucherTrue.isChecked()) {
            Intent intent = new Intent(this, SendSmsActivity.class);
            intent.putExtra(REQUEST_TENANT, "culqi");
            intent.putExtra(REQUEST_AMOUNT, amount);
            intent.putExtra(REQUEST_TRANSACTION, transactionsItem);
            intent.putExtra(REQUEST_PURCHASE_NUMBER, purchaseNumber);
            intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
            startActivity(intent);
        }

        if (rbVoucherFalse.isChecked()) {
            Intent intent = new Intent(this, MainCulqiActivity.class);
            intent.putExtra(REQUEST_TENANT, "culqi");
            startActivity(intent);
        }


    }

}
