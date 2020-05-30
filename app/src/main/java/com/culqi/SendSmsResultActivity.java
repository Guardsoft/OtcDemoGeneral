package com.culqi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.otc.model.response.InitializeResponse;
import com.pax.jemv.demo.R;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;

public class SendSmsResultActivity extends AppCompatActivity {

    InitializeResponse initializeResponse;
    String tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms_result);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initializeResponse = extras.getParcelable(REQUEST_INITIALIZE);
            tenant = extras.getString(REQUEST_TENANT);
        }
    }

    public void backHome(View view) {

        Intent intent = new Intent(this, HomeCulqiActivity.class);
        intent.putExtra(REQUEST_TENANT, tenant);
        intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
        startActivity(intent);
    }
}
