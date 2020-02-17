package com.culqi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.otc.model.response.InitializeResponse;
import com.pax.jemv.demo.R;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;

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
            Intent intent = new Intent(this, SalesActivity.class);
            intent.putExtra(REQUEST_TENANT, TENANT);
            intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
            startActivity(intent);
        });
    }

    private void initData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initializeResponse = extras.getParcelable(REQUEST_INITIALIZE);
            TENANT = extras.getString(MainCulqiActivity.REQUEST_TENANT);
            Log.i(TAG, "initData: " + initializeResponse.toString());


            switch (TENANT){
                case "culqi":
                    layoutHome.setBackgroundResource(R.color.culqi_blue);
                    btnSales.setImageResource(R.drawable.ic_sales);
                    btnQueries.setImageResource(R.drawable.ic_queries);
                    btnCancellations.setImageResource(R.drawable.ic_cancellations);
                    break;

                case "izipay":

                    layoutHome.setBackgroundResource(R.color.izipay_pink1);
                    btnSales.setImageResource(R.drawable.pop_pos);
                    btnQueries.setImageResource(R.drawable.ic_card_report);
                    btnCancellations.setImageResource(R.drawable.ic_card_cancel);
                    break;

                case "vendemas":

                    layoutHome.setBackgroundResource(R.color.vendemas_yellow);
                    btnSales.setImageResource(R.drawable.ic_vendemas_sales);
                    btnQueries.setImageResource(R.drawable.ic_card_report);
                    btnCancellations.setImageResource(R.drawable.ic_card_cancel);
                    break;

                case "bbva":

                    layoutHome.setBackgroundResource(R.color.bbva_blue);
                    btnSales.setImageResource(R.drawable.ic_bbva_sales);
                    btnQueries.setImageResource(R.drawable.ic_card_report);
                    btnCancellations.setImageResource(R.drawable.ic_card_cancel);
                    break;

                default:
            }
        }

    }

    private void initView() {

        layoutHome = findViewById(R.id.layout_home);
        btnSales = findViewById(R.id.btn_sales);
        btnQueries = findViewById(R.id.btn_queries);
        btnCancellations = findViewById(R.id.btn_cancellations);

    }
}
