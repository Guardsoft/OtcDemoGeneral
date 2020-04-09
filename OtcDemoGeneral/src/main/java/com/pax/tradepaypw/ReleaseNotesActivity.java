package com.pax.tradepaypw;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.pax.jemv.demo.R;


public class ReleaseNotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

    }


    public void backClick(View view) {
        finish();
    }

}
