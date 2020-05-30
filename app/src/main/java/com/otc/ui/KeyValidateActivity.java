package com.otc.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.otc.ui.util.UtilOtc;
import com.pax.jemv.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KeyValidateActivity extends AppCompatActivity {

    private static final String TAG = "KeyValidateActivity";

    @BindView(R.id.tv_tlk_kcb)
    TextView tvTlkKcb;
    @BindView(R.id.et_tmk_index)
    EditText etTmkIndex;
    @BindView(R.id.tv_tmk_kcb)
    TextView tvTmkKcb;
    @BindView(R.id.et_tdk_index)
    EditText etTdkIndex;
    @BindView(R.id.tv_tdk_kcb)
    TextView tvTdkKcb;
    @BindView(R.id.et_aes_index)
    EditText etAesIndex;
    @BindView(R.id.tv_aes_kcb)
    TextView tvAesKcb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_validate);
        ButterKnife.bind(this);
    }

    public void keyValidate(View view) {

        tvTlkKcb.setText("KCV : ");
        tvTmkKcb.setText("KCV : ");

        int indexTMK = -1;
        int indexTDK = -1;
        int indexAES = -1;

        // validar tlk
        tvTlkKcb.setText(String.format("KCV : %s", UtilOtc.keyValidateTlk()));


        // validar tmk
        try {
            indexTMK = Integer.parseInt(etTmkIndex.getText().toString().trim());
        } catch (Exception ex) {
            Log.e(TAG, "keyValidate: ", ex);
            indexTMK = -1;
        }

        if (indexTMK > 0) {
            tvTmkKcb.setText(String.format("KCV : %s", UtilOtc.keyValidateTmk(indexTMK)));
        }

        // validar tdk
        try {
            indexTDK = Integer.parseInt(etTdkIndex.getText().toString().trim());
        } catch (Exception ex) {
            Log.e(TAG, "keyValidate: ", ex);
            indexTDK = -1;
        }

        if (indexTDK > 0) {
            tvTdkKcb.setText(String.format("KCV : %s", UtilOtc.keyValidateTdk(indexTMK)));
        }


        // validar AES
        try {
            indexAES = Integer.parseInt(etAesIndex.getText().toString().trim());
        } catch (Exception ex) {
            Log.e(TAG, "keyValidate: ", ex);
            indexAES = -1;
        }

        if (indexAES > 0) {
            tvAesKcb.setText(String.format("ENCRYPT = %s", UtilOtc.keyValidateAes(indexTMK)));
        }


    }
}
