package com.pax.tradepaypw;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.pax.jemv.demo.R;

public class ViewParamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_param);

        initView();
    }

    private void initView() {
        TextView tvParam = (TextView) findViewById(R.id.tv_param);
        Intent intent = getIntent();
        String aid = intent.getStringExtra("aid");
        String capk = intent.getStringExtra("capk");
        if (aid != null) {
            tvParam.setText(aid);
        } else if (capk != null) {
            tvParam.setText(capk);
        }
    }

    public void backClick(View view) {
        finish();
    }
}
