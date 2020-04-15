package com.culqi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;

import com.pax.jemv.demo.R;
import com.pax.tradepaypw.SwingCardActivity;

public class SalesSummaryActivity extends AppCompatActivity {

    TextView tvTotalAmount;
    TextView tvConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_summary);
        
        initView();
        initData();

        tvConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(this, SwingCardActivity.class);
            startActivity(intent);
        });
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String amount = extras.getString(SalesActivity.REQUEST_AMOUNT);
            tvTotalAmount.setText(String.format("S/ %s", amount));
        }

    }

    private void initView() {
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvConfirm = findViewById(R.id.tv_confirm);
        tvConfirm.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }
}
