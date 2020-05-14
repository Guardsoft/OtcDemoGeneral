package com.culqi;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kyanogen.signatureview.SignatureView;
import com.pax.jemv.demo.R;
import com.pax.tradepaypw.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignatureActivity extends AppCompatActivity {


    @BindView(R.id.signature_view)
    SignatureView signatureView;
    @BindView(R.id.tv_signature_aceptar)
    TextView tvSignatureAceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.bind(this);


        tvSignatureAceptar.setOnClickListener(view -> {

            Bitmap bitmap = signatureView.getSignatureBitmap();

            ToastUtil.showImageToast(this, bitmap, "FIRMA", "");

        });

    }
}
