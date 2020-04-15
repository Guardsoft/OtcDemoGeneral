package com.culqi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.otc.model.response.InitializeResponse;
import com.pax.jemv.demo.R;
import com.pax.tradepaypw.SwingCardActivity;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;
import static com.culqi.SalesActivity.REQUEST_AMOUNT;
import static com.culqi.SalesDetailActivity.REQUEST_OPERATION;

public class HomeCulqiActivity extends AppCompatActivity {

    private static final String TAG = "HomeCulqiActivity";

    ImageView btnSales;
    ImageView btnQueries;
    ImageView btnCancellations;
    ConstraintLayout layoutHome;

    //***********
    InitializeResponse initializeResponse;
    String TENANT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_culqi);

        initView();
        initData();

        btnSales.setOnClickListener(v -> {
            startActivity(SalesActivity.class , "sales");
        });

        btnQueries.setOnClickListener(v -> {
            startActivity(SalesTodayActivity.class , "report");
        });


        btnCancellations.setOnClickListener(view -> {
            startActivity(SwingCardActivity.class , "search");
        });
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initializeResponse = extras.getParcelable(REQUEST_INITIALIZE);
            TENANT = extras.getString(MainCulqiActivity.REQUEST_TENANT);
            Log.i(TAG, "initData: " + initializeResponse.toString());

            layoutHome.setBackgroundResource(R.color.culqi_blue);
            btnSales.setImageResource(R.drawable.ic_sales);
            btnQueries.setImageResource(R.drawable.ic_queries);
            btnCancellations.setImageResource(R.drawable.ic_cancellations);
        }
    }

    private void initView() {
        layoutHome = findViewById(R.id.layout_home);
        btnSales = findViewById(R.id.btn_sales);
        btnQueries = findViewById(R.id.btn_queries);
        btnCancellations = findViewById(R.id.btn_cancellations);
    }

    private void startActivity(Class activity, String operation){
        Intent intent = new Intent(this, activity);
        intent.putExtra(REQUEST_TENANT, TENANT);
        intent.putExtra(REQUEST_OPERATION, operation);
        intent.putExtra(REQUEST_AMOUNT, "0.0");
        intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
        startActivity(intent);
    }

}

