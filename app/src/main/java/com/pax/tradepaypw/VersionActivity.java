package com.pax.tradepaypw;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.pax.jemv.demo.R;

public class VersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
    }

    public void backClick(View view) {
        finish();
    }

    public void versionClick(View view) {
        Intent intent = new Intent(this, ReleaseNotesActivity.class);
        startActivity(intent);
    }
}
