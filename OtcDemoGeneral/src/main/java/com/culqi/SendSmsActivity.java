package com.culqi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.otc.model.request.send.Device;
import com.otc.model.request.send.Header;
import com.otc.model.request.send.Merchant;
import com.otc.model.request.send.Order;
import com.otc.model.request.send.SendSmsRequest;
import com.otc.model.request.send.Voucher;
import com.otc.model.response.InitializeResponse;
import com.otc.model.response.retrieve.TransactionsItem;
import com.otc.model.response.send.SendSmsResponse;
import com.pax.jemv.demo.R;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;
import static com.culqi.SalesDetailActivity.REQUEST_TRANSACTION;


public class SendSmsActivity extends AppCompatActivity {

    private static final String TAG = "SendSmsActivity";

    @BindView(R.id.layout_progress)
    LinearLayout layoutProgress;
    @BindView(R.id.et_number_phone)
    EditText etNumberPhone;
    @BindView(R.id.tv_aceptar)
    TextView tvAceptar;

    TransactionsItem transactionsItem;
    InitializeResponse initializeResponse;
    String authorization;
    String tenant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            transactionsItem = extras.getParcelable(REQUEST_TRANSACTION);
            initializeResponse = extras.getParcelable(REQUEST_INITIALIZE);
            tenant = extras.getString(REQUEST_TENANT);
        }

        SharedPreferences prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        authorization = prefsPax.getString("authorization", "");


        tvAceptar.setOnClickListener(view ->{
            sendSms();
        });

    }

    public void sendSms() {

        String number = etNumberPhone.getText().toString().trim();

        if (number.equals("")) {
            etNumberPhone.setError("Ingresa tú número");
            etNumberPhone.requestFocus();
            return;
        }

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        Merchant merchant = new Merchant();
        merchant.setMerchantId(initializeResponse.getMerchant().getMerchantId());

        Device device = new Device();
        device.setTerminalId(initializeResponse.getDevice().getTerminalId());

        Order order = new Order();
        order.setChannel("mpos");
        order.setPurchaseNumber(transactionsItem.getPurchaseNumber());

        Voucher voucher = new Voucher();
        voucher.setDocumentId("");
        voucher.setPhone(number);
        voucher.setEmail("");
        voucher.setSignature("");


        SendSmsRequest request = new SendSmsRequest();
        request.setHeader(header);
        request.setMerchant(merchant);
        request.setDevice(device);
        request.setOrder(order);
        request.setVoucher(voucher);

        String DOMAIN = "https://culqimpos.quiputech.com/";

        layoutProgress.setVisibility(View.VISIBLE);

        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.post(DOMAIN + "api.voucher/v3/culqi/management/send")
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", authorization)
                .addApplicationJsonBody(request)
                .setTag("send")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(SendSmsResponse.class, new ParsedRequestListener<SendSmsResponse>() {
                    @Override
                    public void onResponse(SendSmsResponse response) {
                        layoutProgress.setVisibility(View.GONE);

                        if (response.getHeader().getResponseMessage().equals("OK")) {

                            Intent intent = new Intent(SendSmsActivity.this, SendSmsResultActivity.class);
                            intent.putExtra(REQUEST_TENANT, tenant);
                            intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
                            startActivity(intent);

                        } else {

                        }


                    }

                    @Override
                    public void onError(ANError error) {
                        layoutProgress.setVisibility(View.GONE);
                        // handle error
                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);
                    }
                });


    }
}
