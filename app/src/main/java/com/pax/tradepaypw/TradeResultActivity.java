package com.pax.tradepaypw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.culqi.MainCulqiActivity;
import com.culqi.adapter.SalesVoucherActivity;
import com.google.gson.Gson;
import com.otc.manager.PrinterManager;
import com.otc.model.request.authorize.AuthorizeRequest;
import com.otc.model.request.authorize.Card;
import com.otc.model.request.authorize.Cryptography;
import com.otc.model.request.authorize.Header;
import com.otc.model.request.authorize.Merchant;
import com.otc.model.request.authorize.Order;
import com.otc.model.request.authorize.ScopesItem;
import com.otc.model.request.cancel.VoidRequest;
import com.otc.model.response.InitializeResponse;
import com.otc.model.response.authorize.AuthorizeResponse;
import com.otc.model.response.retrieve.TransactionsItem;
import com.otc.ui.DemoActivity;
import com.otc.ui.util.UtilOtc;
import com.pax.app.TradeApplication;
import com.pax.dal.entity.EReaderType;
import com.pax.jemv.amex.api.ClssAmexApi;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.pax.jemv.clssquickpass.trans.ClssQuickPass;
import com.pax.jemv.demo.BuildConfig;
import com.pax.jemv.demo.R;
import com.pax.jemv.dpas.api.ClssDPASApi;
import com.pax.jemv.emv.api.EMVCallback;
import com.pax.jemv.jcb.api.ClssJCBApi;
import com.pax.jemv.paypass.api.ClssPassApi;
import com.pax.jemv.paywave.api.ClssWaveApi;
import com.pax.jemv.pure.api.ClssPUREApi;
import com.pax.jemv.qpboc.api.ClssPbocApi;
import com.pax.tradepaypw.device.Device;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;
import static com.culqi.SalesActivity.REQUEST_AMOUNT;
import static com.culqi.SalesDetailActivity.REQUEST_OPERATION;
import static com.culqi.SalesTodayActivity.REQUEST_TRANSACTION;
import static com.pax.tradepaypw.utils.Utils.bcd2Str;

public class TradeResultActivity extends AppCompatActivity {
    private static final String TAG = "TradeResultActivity";
    public static final String REQUEST_AUTHORIZE = "authorize";
    public static final String REQUEST_PURCHASE_NUMBER = "purchaseNumber";
    private TextView tvAmount;
    private TextView tvCardNum;
    private TextView tvDate;

    private TextView tvArqc;
    private TextView tvApplable;
    private TextView tvAid;
    private TextView tvAppname;
    private TextView tvTsi;
    private TextView tvTc;
    private TextView tvAtc;
    private TextView tvTvr;

    private TextView tvAmountResult;
    private TextView tvCardNumber;
    private TextView tvResult;
    private ImageView ivLoadingTip;
    private TextView tvVoucher;


    private Handler handler = new Handler();
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();

    //**
    private RelativeLayout layout_1;
    private LinearLayout layout10;
    private LinearLayout layoutProgress;
    public InitializeResponse initializeResponse;
    public TransactionsItem transactionsItem;
    private AuthorizeResponse authorizeResponse;
    private String amount;
    private ScrollView layoutTest;
    private String TENANT;
    private String OPERATION;
    private String TRACK2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initView();
        initData();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TradeResultActivity.this, MainCulqiActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        }, 80000);

    }

    //here1
    private void initData() {
        String arqc = null;
        String tvr = null;
        String aid = null;
        String appLable = null;
        String appName = null;
        String tsi = null;
        String tc = null;
        String atc = null;
        int iRet;

        String emvJoined ="";


        ByteArray byteArray = new ByteArray();

        Log.i(TAG, "entryPoint.getOutParam().ucKernType = " + entryPoint.getOutParam().ucKernType);

        if (SwingCardActivity.getReadType() == EReaderType.PICC) {

            Log.i(TAG, "SwingCardActivity.getReadType() == EReaderType.PICC");

            if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_MC) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_MC");

                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                iRet = ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                Log.i("Clss_TLV_MC iRet 0x95", Integer.toString(iRet));
                Log.i("Clss_GetTLV_MC TVR 0x95", tvr + "");
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);


                //*****************************************************
                String emv1 = printEmvMC_Contactless(0x5F,0x2A, "0x5F2A");
                String emv2 = printEmvMC_Contactless(0x82,0, "0x82");
                String emv3 = printEmvMC_Contactless(0x95, 0,"0x95");
                String emv4 = printEmvMC_Contactless(0x9A, 0,"0x9A");
                String emv5 = printEmvMC_Contactless(0x9C, 0,"0x9C");
                String emv6 = printEmvMC_Contactless(0x9F,0x02, "0x9F02");
                String emv7 = printEmvMC_Contactless(0x9F03,0x03, "0x9F03");
                String emv8 = printEmvMC_Contactless(0x9F, 0x10, "0x9F10");
                String emv9 = printEmvMC_Contactless(0x9F, 0x1A, "0x9F1A");
                String emv10 = printEmvMC_Contactless(0x9F, 0x26,"0x9F26");
                String emv11 = printEmvMC_Contactless(0x9F, 0x27,"0x9F27");
                String emv12 = printEmvMC_Contactless(0x9F, 0x33,"0x9F33");
                String emv13 = printEmvMC_Contactless(0x9F, 0x34,"0x9F34");
                String emv14 = printEmvMC_Contactless(0x9F, 0x35,"0x9F35");
                String emv15 = printEmvMC_Contactless(0x9F, 0x36,"0x9F36");
                String emv16 = printEmvMC_Contactless(0x9F, 0x37,"0x9F37");
                String emv17 = printEmvMC_Contactless(0x9F, 0x40,"0x9F40");

                String emv18 = printEmvMC_Contactless(0x5F, 0x34,"0x5F34");
                String emv19 = printEmvMC_Contactless(0x84, 0,"0x84");

                emvJoined =
                        emv1 + emv2 +emv3 +emv4 +emv5
                                +emv6 +emv7 +emv8 +emv9 +emv10
                                +emv11 +emv12 +emv13 +emv14 +emv15
                                +emv16 +emv17 +emv18 +emv19;

                Log.i(TAG, "successProcess: " + emvJoined);
                //**********************************************************************************

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_VIS) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_VIS");

                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F26, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x95, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x4F, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x50, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F12, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9B, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F26, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F36, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

                //**********************************************************************************
//                String emv20 = printEmvWape(0x71, "0x71");
//                String emv21 = printEmvWape(0x72, "0x72");
//                String emv22 = printEmvWape(0xBF10, "0xBF10");
//                String emv23 = printEmvWape(0x9F1E, "0x9F1E");
//                String emv24 = printEmvWape(0x4F, "0x4F");
//                String emv25 = printEmvWape(0x9F09, "0x9F09");
//                String emv26 = printEmvWape(0x9F41, "0x9F41");
//                String emv27 = printEmvWape(0x91, "0x91");
//                String emv28 = printEmvWape(0x8A, "0x8A");
//                String emv29 = printEmvWape(0x5A, "0x5A");
//                String emv30 = printEmvWape(0x5F34, "0x5F34");
//                String emv31 = printEmvWape(0x9F6E, "0x9F6E");

                //*****************************************************
                String emv1 = printEmvWape(0x5F2A, "0x5F2A");
                String emv2 = printEmvWape(0x82, "0x82");
                String emv3 = printEmvWape(0x95, "0x95");
                String emv4 = printEmvWape(0x9A, "0x9A");
                String emv5 = printEmvWape(0x9C, "0x9C");
                String emv6 = printEmvWape(0x9F02, "0x9F02");
                String emv7 = printEmvWape(0x9F03, "0x9F03");
                String emv8 = printEmvWape(0x9F10, "0x9F10");
                String emv9 = printEmvWape(0x9F1A, "0x9F1A");
                String emv10 = printEmvWape(0x9F26, "0x9F26");
                String emv11 = printEmvWape(0x9F27, "0x9F27");
                String emv12 = printEmvWape(0x9F33, "0x9F33");
                String emv13 = printEmvWape(0x9F34, "0x9F34");
                String emv14 = printEmvWape(0x9F35, "0x9F35");
                String emv15 = printEmvWape(0x9F36, "0x9F36");
                String emv16 = printEmvWape(0x9F37, "0x9F37");
                String emv17 = printEmvWape(0x9F40, "0x9F40");

                String emv18 = printEmvWape(0x5F34, "0x5F34");
                String emv19 = printEmvWape(0x84, "0x84");

                emvJoined =
                        emv1 + emv2 +emv3 +emv4 +emv5
                                +emv6 +emv7 +emv8 +emv9 +emv10
                                +emv11 +emv12 +emv13 +emv14 +emv15
                                +emv16 +emv17 +emv18 +emv19;

                Log.i(TAG, "successProcess: " + emvJoined);
                //**********************************************************************************

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_AE) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_AE");

                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F26, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x95, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x4F, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x50, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F12, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9B, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F26, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F36, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_ZIP) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_ZIP");

                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if ((entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_PBOC) &&
                    (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_QVSDC)) {

                Log.i(TAG, "initData: (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_PBOC) &&\n" +
                        "                    (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_QVSDC)");

                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F26, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x95, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x4F, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x50, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F12, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9B, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F26, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F36, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_JCB) {


                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_JCB");

                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                byteArray = new ByteArray();
                iRet = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                Log.i("Clss_TLV_MC iRet 0x95", Integer.toString(iRet));
                Log.i("Clss_GetTLV_MC TVR 0x95", tvr + "");
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_PURE) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_PURE");

                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                byteArray = new ByteArray();
                iRet = ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                Log.i("Clss_TLV_MC iRet 0x95", Integer.toString(iRet));
                Log.i("Clss_GetTLV_MC TVR 0x95", tvr + "");
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);
            }
        }

        if ((SwingCardActivity.getReadType() == EReaderType.ICC) ||
                ((ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PBOC) && (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC))) { // contact

            Log.i(TAG, "initData: (SwingCardActivity.getReadType() == EReaderType.ICC) ||\n" +
                    "                ((ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PBOC) && (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC))");

            EMVCallback.EMVGetTLVData((short) 0x9F26, byteArray);
            byte[] a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            arqc = bcd2Str(a);

            EMVCallback.EMVGetTLVData((short) 0x95, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            tvr = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x4F, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            aid = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x50, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            appLable = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9F12, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            appName = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9B, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            tsi = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9F26, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            tc = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9F36, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            atc = bcd2Str(a);


            //**************************************************************************************
//            String emv20 = printEmv2(0x71, "0x71");
//            String emv21 = printEmv2(0x72, "0x72");
//            String emv22 = printEmv2(0xBF10, "0xBF10");
//            String emv23 = printEmv2(0x9F1E, "0x9F1E");
//            String emv24 = printEmv2(0x4F, "0x4F");
//            String emv25 = printEmv2(0x9F09, "0x9F09");
//            String emv26 = printEmv2(0x9F41, "0x9F41");
//            String emv27 = printEmv2(0x91, "0x91");
//            String emv28 = printEmv2(0x8A, "0x8A");
//            String emv29 = printEmv2(0x5A, "0x5A");
//            String emv30 = printEmv2(0x5F34, "0x5F34");
//            String emv31 = printEmv2(0x9F6E, "0x9F6E");

            //*****************************************************
            String emv1 = printEmv2(0x5F2A, "0x5F2A");
            String emv2 = printEmv2(0x82, "0x82");
            String emv3 = printEmv2(0x95, "0x95");
            String emv4 = printEmv2(0x9A, "0x9A");
            String emv5 = printEmv2(0x9C, "0x9C");
            String emv6 = printEmv2(0x9F02, "0x9F02");
            String emv7 = printEmv2(0x9F03, "0x9F03");
            String emv8 = printEmv2(0x9F10, "0x9F10");
            String emv9 = printEmv2(0x9F1A, "0x9F1A");
            String emv10 = printEmv2(0x9F26, "0x9F26");
            String emv11 = printEmv2(0x9F27, "0x9F27");
            String emv12 = printEmv2(0x9F33, "0x9F33");
            String emv13 = printEmv2(0x9F34, "0x9F34");
            String emv14 = printEmv2(0x9F35, "0x9F35");
            String emv15 = printEmv2(0x9F36, "0x9F36");
            String emv16 = printEmv2(0x9F37, "0x9F37");
            String emv17 = printEmv2(0x9F40, "0x9F40");

            String emv18 = printEmv2(0x5F34, "0x5F34");
            String emv19 = printEmv2(0x84, "0x84");


            emvJoined =
                    emv1 + emv2 +emv3 +emv4 +emv5
                            +emv6 +emv7 +emv8 +emv9 +emv10
                            +emv11 +emv12 +emv13 +emv14 +emv15
                            +emv16 +emv17 +emv18 +emv19 ;
            //**************************************************************************************

        }

        amount = getIntent().getStringExtra("amount");
        TRACK2 = getIntent().getStringExtra("track2");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tvDate.setText(dateFormat.format(new Date()));
        tvCardNum.setText(getIntent().getStringExtra("pan"));
        tvAmount.setText(amount);


        /*if (getIntent().getStringExtra("pin") != null) {
            Toast.makeText(getApplicationContext(),getIntent().getStringExtra("pin"),Toast.LENGTH_SHORT).show();
        }*/

        String pinBlockEncrypt = getIntent().getStringExtra("pinBlock");
        String pin = getIntent().getStringExtra("pin");
        initializeResponse = getIntent().getExtras().getParcelable(REQUEST_INITIALIZE);
        transactionsItem = getIntent().getExtras().getParcelable(REQUEST_TRANSACTION);

        OPERATION = getIntent().getStringExtra(REQUEST_OPERATION);

        String type = getIntent().getStringExtra("type");

        tvArqc.setText(arqc);
        tvApplable.setText(appLable);
        tvAid.setText(aid);
        tvAppname.setText(appName);
        tvTsi.setText(tsi);
        tvTc.setText(tc);
        tvAtc.setText(atc);
        tvTvr.setText(tvr);

        layout_1.setVisibility(View.GONE);
        layoutTest.setVisibility(View.VISIBLE);

        if (initializeResponse != null) {

            layout_1.setVisibility(View.VISIBLE);
            layoutTest.setVisibility(View.GONE);

            Log.i(TAG, "initData: " + type);
            Log.i(TAG, "initData: " + amount);
            Log.i(TAG, "initData: " + pin);
            Log.i(TAG, "initData: " + pinBlockEncrypt);
            Log.i(TAG, "initData: " + TRACK2);
            Log.i(TAG, "initData: " + initializeResponse);
            Log.i(TAG, "initData: " + OPERATION);

            if (OPERATION.equals("cancel")) {

                tvVoucher.setVisibility(View.GONE);

                Log.i(TAG, "initData: ANULAR");
                voidCancel(initializeResponse, type, TRACK2);
                
            } else if (OPERATION.equals("search")) {

                Log.i(TAG, "initData: ANULAR");
                voidCancel(initializeResponse, type, TRACK2);

            }else {
                Log.i(TAG, "initData: AUTORIZAR");
                authorize(initializeResponse, type, amount, TRACK2, pinBlockEncrypt, emvJoined);
            }


        }else{
            GetPinEmv.getInstance().setPinDataEncrypt("");
            GetPinEmv.getInstance().setPinData("");
        }

        //******************************************************************************************
        TENANT = getIntent().getStringExtra(REQUEST_TENANT);

    }

    private void initView() {
        tvAmount = (TextView) findViewById(R.id.ecash_amount);
        tvCardNum = (TextView) findViewById(R.id.ecash_bankcardNo);
        tvDate = (TextView) findViewById(R.id.ecash_time);

        tvArqc = (TextView) findViewById(R.id.ecash_arqc);
        tvApplable = (TextView) findViewById(R.id.ecash_applable);
        tvAid = (TextView) findViewById(R.id.ecash_aid);
        tvAppname = (TextView) findViewById(R.id.ecash_appname);
        tvTsi = (TextView) findViewById(R.id.ecash_tsi);
        tvTc = (TextView) findViewById(R.id.ecash_tc);
        tvAtc = (TextView) findViewById(R.id.ecash_atc);
        tvTvr = (TextView) findViewById(R.id.ecash_tvr);

        //**** pax
        layout_1 = findViewById(R.id.layout_1);
        layoutTest = findViewById(R.id.layout_2);
        layoutProgress = findViewById(R.id.layout_progress);
        tvCardNumber = findViewById(R.id.tv_card_number);
        tvAmountResult = findViewById(R.id.tv_amount);
        layout10 = findViewById(R.id.layout_10);
        tvResult = findViewById(R.id.tv_result);
        tvVoucher = findViewById(R.id.tv_voucher);
    }

    public void enterClick(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        handler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainCulqiActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }

        return super.onKeyDown(keyCode, event);
    }


    private void authorize(InitializeResponse response, String type, String amount, String track2, String pinBlockEncrypt, String emv) {

        Log.i(TAG, "authorize Initialize : " + response.toString());
        Log.i(TAG, "authorize track2 : " + track2);
        Log.i(TAG, "authorize pinBlockEncrypt : " + pinBlockEncrypt);
        Log.i(TAG, "authorize emv : " + emv);

        layoutProgress.setVisibility(View.VISIBLE);

        long purchaseNumber = 0L;
        String authorization = "";
        ///*******************
        SharedPreferences prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        authorization = prefsPax.getString("authorization", "");
        purchaseNumber = prefsPax.getLong("purchase_number", 0);

        purchaseNumber++;
        SharedPreferences.Editor editor = prefsPax.edit();
        editor.putLong("purchase_number", purchaseNumber);
        editor.apply();
        ///*******************

        Log.i(TAG, "purchaseNumber: " + purchaseNumber);

        String DOMAIN = "https://culqimpos.quiputech.com/";

        Log.i(TAG, "*** authorize Track2 trackData2_38: " + track2);
        String TRACK = track2;

        String terminalId = response.getDevice().getTerminalId();
        String serialNumber = response.getDevice().getSerialNumber();

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        Cryptography crypt = null;

        if (BuildConfig.CRYPTOGRAPHY) {

            TRACK = encryptDataAes(track2);

            crypt = new Cryptography();
            crypt.setOwner(UtilOtc.getSerialNumber());
            crypt.setMode("DEVICE");

            List<ScopesItem> scopes = new ArrayList<>();

            //** scope para track2
            ScopesItem scopesItemTrack2 = new ScopesItem();
            scopesItemTrack2.setKeyId("data");
            scopesItemTrack2.setKeyType("DATA");
            List<String> elementstrack2 = new ArrayList<>();
            elementstrack2.add("card.track2");
            scopesItemTrack2.setElements(elementstrack2);

            // agregar objeto a scopes
            scopes.add(scopesItemTrack2);

            pinBlockEncrypt = pinBlockEncrypt == null ? "": pinBlockEncrypt;

            if (!pinBlockEncrypt.equals("")) {
                //** scope para pinBlock
                ScopesItem scopesItemPin = new ScopesItem();
                scopesItemPin.setKeyId("pin");
                scopesItemPin.setKeyType("PIN");
                List<String> elementspinBlock = new ArrayList<>();
                elementspinBlock.add("card.pinBlock");
                scopesItemPin.setElements(elementspinBlock);

                // agregar objeto a scopes
                scopes.add(scopesItemPin);
            }

            crypt.setScopes(scopes);
        }else{
            pinBlockEncrypt = "";
        }


        Merchant merchant = new Merchant();
        merchant.setMerchantId(response.getMerchant().getMerchantId());

        com.otc.model.request.authorize.Device deviceAut
                = new com.otc.model.request.authorize.Device();

        deviceAut.setTerminalId(terminalId);
        deviceAut.setCaptureType(type);//chip contactless / band
        deviceAut.setUnattended(false);

        Order order = new Order();
        order.setChannel("mpos");
        order.setPurchaseNumber(purchaseNumber+"");
        order.setAmount(Double.parseDouble(amount));
        order.setCurrency("PEN");
        order.setCountable(true);
        order.setInstallment(0);

        Card card = new Card();
        card.setSequenceNumber("001");
        card.setTrack2(TRACK);
        card.setPinBlock(pinBlockEncrypt);

        if (emv != null) {
            card.setEmv(emv);
        }

        AuthorizeRequest request = new AuthorizeRequest();
        request.setHeader(header);
        request.setCryptography(crypt);
        request.setMerchant(merchant);
        request.setDevice(deviceAut);
        request.setOrder(order);
        request.setCard(card);

        // ------------------------------------  HEADERS -------------------------------------------
        Map<String, String> headerMap = null;
        try {
            headerMap = UtilOtc.getSignatureRequest(request);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        // ------------------------------------  HEADERS -------------------------------------------

        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.post(DOMAIN + "api.authorization/v3/culqi/authorize")
                .setContentType("application/json;charset=utf-8")
                .addHeaders(headerMap)
                .addStringBody(UtilOtc.toJsonPretty(request))
                .setTag("initialize")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(AuthorizeResponse.class, new ParsedRequestListener<AuthorizeResponse>() {
                    @Override
                    public void onResponse(AuthorizeResponse response) {
                        GetPinEmv.getInstance().setPinDataEncrypt("");
                        GetPinEmv.getInstance().setPinData("");

                        layoutProgress.setVisibility(View.GONE);

                        tvAmountResult.setText(String.format("S/ %s", UtilOtc.formatAmount(response.getOrder().getAmount())));
                        tvCardNumber.setText(UtilOtc.getCardNumber(track2));

                        authorizeResponse = response;

                        PrinterManager manager = new PrinterManager();
                        manager.printDemo(TradeResultActivity.this, response, TENANT, track2);

//                        UtilOtc.getInstance().dialogResult(TradeResultActivity.this, response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        GetPinEmv.getInstance().setPinDataEncrypt("");
                        GetPinEmv.getInstance().setPinData("");

                        layoutProgress.setVisibility(View.GONE);

                        layout10.setBackgroundColor(getResources().getColor(R.color.color_toolbar));
                        tvResult.setText("TRANSACCIÓN \nDENEGADA");
                        tvCardNumber.setText(UtilOtc.getCardNumber(track2));

//                        if (!error.getErrorBody().equals("")) {
//                            UtilOtc.getInstance().dialogResult(TradeResultActivity.this, error.getErrorBody());
//                        }else{
//                            Log.i(TAG, "onError: " + error.getErrorDetail());
//                        }

//                        OtcUtil.getInstance().dialogResult(SwingCardActivity.this, error.getErrorBody());
                        Gson gson = new Gson();

                        String requestAut = error.getErrorBody();
                        
                        AuthorizeResponse errorAut = gson.fromJson(requestAut, AuthorizeResponse.class);

                        // handle error
                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);
                    }
                });

    }

    private void voidCancel(InitializeResponse response, String type, String track2) {

        layoutProgress.setVisibility(View.VISIBLE);

        SharedPreferences prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        String authorization = prefsPax.getString("authorization", "");


        String DOMAIN = "https://culqimpos.quiputech.com/";

        String track2Encrypt = track2;

        if (BuildConfig.CRYPTOGRAPHY) {
            track2Encrypt = encryptDataAes(track2);
        }

        //request  +++++++++++++++++++++++++++++++++++++++++++++
        com.otc.model.request.cancel.Header header = new com.otc.model.request.cancel.Header();
        header.setExternalId(UUID.randomUUID().toString());

        com.otc.model.request.cancel.Merchant merchant = new com.otc.model.request.cancel.Merchant();
        merchant.setMerchantId(response.getMerchant().getMerchantId());

        com.otc.model.request.cancel.Device device = new com.otc.model.request.cancel.Device();
        device.setCaptureType(type);
        device.setTerminalId(response.getDevice().getTerminalId());
        device.setUnattended(false);

        com.otc.model.request.cancel.Order order = new com.otc.model.request.cancel.Order();
        order.setChannel("mpos");
        order.setPurchaseNumber(transactionsItem.getPurchaseNumber());
        order.setAmount(transactionsItem.getOrderAmount());
        order.setCurrency(transactionsItem.getCurrency());
        order.setCountable(true);

        com.otc.model.request.cancel.Card card = new com.otc.model.request.cancel.Card();
        card.setTrack2(track2Encrypt);

        com.otc.model.request.cancel.Cryptography crypt = null;

        if (BuildConfig.CRYPTOGRAPHY) {
            track2Encrypt = encryptDataAes(track2);

            crypt = new com.otc.model.request.cancel.Cryptography();
            crypt.setOwner(UtilOtc.getSerialNumber());
            crypt.setMode("DEVICE");

            List<com.otc.model.request.cancel.ScopesItem> scopes = new ArrayList<>();

            //** scope para track2
            com.otc.model.request.cancel.ScopesItem scopesItemTrack2 = new com.otc.model.request.cancel.ScopesItem();
            scopesItemTrack2.setKeyId("data");
            scopesItemTrack2.setKeyType("DATA");
            List<String> elementstrack2 = new ArrayList<>();
            elementstrack2.add("card.track2");
            scopesItemTrack2.setElements(elementstrack2);

            // agregar objeto a scopes
            scopes.add(scopesItemTrack2);

            crypt.setScopes(scopes);
        }

        VoidRequest request = new VoidRequest();
        request.setHeader(header);
        request.setMerchant(merchant);
        request.setDevice(device);
        request.setOrder(order);
        request.setCard(card);
        request.setCryptography(crypt);

        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.post(DOMAIN + "api.authorization/v3/culqi/void")
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", authorization)
                .addApplicationJsonBody(request)
                .setTag("void_cancel")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        GetPinEmv.getInstance().setPinDataEncrypt("");
                        GetPinEmv.getInstance().setPinData("");

                        layoutProgress.setVisibility(View.GONE);

                        Log.i(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onError(ANError error) {
                        GetPinEmv.getInstance().setPinDataEncrypt("");
                        GetPinEmv.getInstance().setPinData("");

                        layoutProgress.setVisibility(View.GONE);

                        layout10.setBackgroundColor(getResources().getColor(R.color.color_toolbar));
                        tvResult.setText("TRANSACCIÓN \nDENEGADA");
                        tvCardNumber.setText(UtilOtc.getCardNumber(track2));

                        // handle error
                        Log.i(TAG, "onError: " + error.getErrorCode());
                        Log.i(TAG, "onError: " + error.getErrorDetail());
                        Log.i(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: ", error);
                    }
                });

    }

    private String encryptDataAes(String dataIn){
//        dataIn = String.format("%-64s", dataIn ).replace(' ', '0');
        dataIn = dataIn + "0D0D0D0D0D0D0D0D0D0D0D0D0D";
        Log.i(TAG, "*** encryptDataAes Track2 : " + dataIn);
        int slotDataTAESK10 = 10;
        return Device.encryptAES(dataIn, slotDataTAESK10);
    }

    private String getContactEmv() {

        String emv20 = printEmv2(0x71, "0x71");
        String emv21 = printEmv2(0x72, "0x72");
        String emv22 = printEmv2(0xBF10, "0xBF10");
        String emv23 = printEmv2(0x9F1E, "0x9F1E");
        String emv24 = printEmv2(0x4F, "0x4F");
        String emv25 = printEmv2(0x9F09, "0x9F09");
        String emv26 = printEmv2(0x9F41, "0x9F41");
        String emv27 = printEmv2(0x91, "0x91");
        String emv28 = printEmv2(0x8A, "0x8A");
        String emv29 = printEmv2(0x5A, "0x5A");
        String emv30 = printEmv2(0x5F34, "0x5F34");
        String emv31 = printEmv2(0x9F6E, "0x9F6E");

        //*****************************************************
        String emv1 = printEmv2(0x5F2A, "0x5F2A");
        String emv2 = printEmv2(0x82, "0x82");
        String emv3 = printEmv2(0x95, "0x95");
        String emv4 = printEmv2(0x9A, "0x9A");
        String emv5 = printEmv2(0x9C, "0x9C");
        String emv6 = printEmv2(0x9F02, "0x9F02");
        String emv7 = printEmv2(0x9F03, "0x9F03");
        String emv8 = printEmv2(0x9F10, "0x9F10");
        String emv9 = printEmv2(0x9F1A, "0x9F1A");
        String emv10 = printEmv2(0x9F26, "0x9F26");
        String emv11 = printEmv2(0x9F27, "0x9F27");
        String emv12 = printEmv2(0x9F33, "0x9F33");
        String emv13 = printEmv2(0x9F34, "0x9F34");
        String emv14 = printEmv2(0x9F35, "0x9F35");
        String emv15 = printEmv2(0x9F36, "0x9F36");
        String emv16 = printEmv2(0x9F37, "0x9F37");
        String emv17 = printEmv2(0x9F40, "0x9F40");

        String emv18 = printEmv2(0x5F34, "0x5F34");
        String emv19 = printEmv2(0x84, "0x84");


//        String emvConcat = emv1 + emv2 +emv3 +emv4 +emv5
//                +emv6 +emv7 +emv8 +emv9 +emv10
//                +emv11 +emv12 +emv13 +emv14 +emv15
//                +emv16 +emv17 + emv18 + emv19 + emv20
//                +emv21 +emv22 + emv23 + emv24 + emv25
//                +emv26 +emv27 + emv28 + emv29 + emv30
//                +emv31 +emv32;

        String emvConcat =
                emv1 + emv2 +emv3 +emv4 +emv5
                        +emv6 +emv7 +emv8 +emv9 +emv10
                        +emv11 +emv12 +emv13 +emv14 +emv15
                        +emv16 +emv17 +emv18 +emv19 +emv20
                        +emv21 +emv22 +emv23 +emv24 +emv25
                        +emv26 +emv27 +emv28 +emv29 +emv30 +emv31;

        Log.i(TAG, "Emv concatenado : " + emvConcat);
        return emvConcat;
    }

    private String printEmvWape(int tag, String tagName) {

        tagName = tagName.replace("0x", "");
        String concatEmv = "";
        String tagValue;

        try {
            ByteArray dataArray = new ByteArray();
            ClssWaveApi.Clss_GetTLVData_Wave((short) tag, dataArray);
            byte[] dataCard = Arrays.copyOfRange(dataArray.data, 0, dataArray.length);

            if (dataArray.length == 256) {

                if (tagName.equals("9F03")) {
                    concatEmv = tagName + "06" + "000000000000";
                }else if (tagName.equals("9F34")){
                    concatEmv = tagName + "03" + "000000";
                }else if (tagName.equals("9F35")){
                    concatEmv = tagName + "01" + "00";
                }else if (tagName.equals("9F40")){
                    concatEmv = tagName + "05" + "0000000000";
                }

                Log.i(TAG, "printEmvWape : * "  + tagName + " = vacio");
            }else{
                tagValue = TradeApplication.getConvert().bcdToStr(dataCard);
                Log.i(TAG, "printEmvWape : "  + tagName + " = " + calculateSizeEmv(tagValue.length()) + " - "+ tagValue);
                concatEmv = tagName + calculateSizeEmv(tagValue.length()) + tagValue;
            }
        }catch(Exception e) {
            Log.i(TAG, "printEmvWape : * "  + tagName + " = vacio");

        }
        return concatEmv;
    }

    private String printEmv2(int tag, String tagName) {
        tagName = tagName.replace("0x", "");
        String concatEmv;
        String tagValue;

        try {
            tagValue = TradeApplication.getConvert().bcdToStr(ImplEmv.getTlv(tag));
            Log.i(TAG, "printEmv2 : "  + tagName + " = " + calculateSizeEmv(tagValue.length()) + " - "+ tagValue);
            concatEmv = tagName + calculateSizeEmv(tagValue.length()) + tagValue;
        }catch(Exception e) {

            if (tagName.equals("9F03")) {
                concatEmv = tagName + "06" + "000000000000";
            }else{
                concatEmv = "";
            }
            Log.i(TAG, "printEmv2 : * "  + tagName + " = vacio");
        }

        return concatEmv;
    }

    private String printEmvMC(int tag, String tagName) {

        tagName = tagName.replace("0x", "");
        String concatEmv;
        String tagValue;

        try {
            ByteArray dataArray = new ByteArray();
            ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte)tag}, (byte) 1, 60, dataArray);

            byte[] dataCard = Arrays.copyOfRange(dataArray.data, 0, dataArray.length);

            tagValue = TradeApplication.getConvert().bcdToStr(dataCard);
            Log.i(TAG, "printEmvMC : "  + tagName + " = " + calculateSizeEmv(tagValue.length()) + " - "+ tagValue);

        }catch(Exception e) {
            tagValue = "000000000000";
            Log.i(TAG, "printEmvMC : * "  + tagName + " = " + calculateSizeEmv(tagValue.length()) + " - "+ tagValue);

        }
        concatEmv = tagName + calculateSizeEmv(tagValue.length()) + tagValue;
        return concatEmv;
    }

    private String printEmvMC_Contactless(int tag1, int tag2, String tagName) {

        tagName = tagName.replace("0x", "");
        String concatEmv;
        String tagValue = "";
        byte[] byteArrayNull = new byte[256];

        try {
            ByteArray dataArray = new ByteArray();
            if (tag2 == 0) {
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) tag1}, (byte) 1, 10, dataArray);
            }else{
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) tag1, (byte)tag2}, (byte) 2, 10, dataArray);
            }

            byte[] dataCard = new byte[dataArray.length];
            System.arraycopy(dataArray.data, 0, dataCard, 0, dataArray.length);

            if (Arrays.equals(byteArrayNull,dataCard)) {

                if (tagName.equals("9F03")) {
                    tagValue = "000000000000";
                }else if (tagName.equals("9F10")){
                    tagValue = "000000000000000000000000000000000000";
                }

                Log.i(TAG, "printEmvMC : "  + tagName + " = VACIO");
            }else{
                tagValue = TradeApplication.getConvert().bcdToStr(dataCard);
                Log.i(TAG, "printEmvMC : "  + tagName + " = " + calculateSizeEmv(tagValue.length()) + " - "+ tagValue);
            }

        }catch(Exception e) {
            Log.i(TAG, "printEmvMC : * "  + tagName + " = " + calculateSizeEmv(tagValue.length()) + " - "+ tagValue);

        }

        concatEmv = tagName + calculateSizeEmv(tagValue.length()) + tagValue;
        return concatEmv;
    }

    private String calculateSizeEmv(int value){

        value = value/2;
        String temp = Integer.toHexString(value);
        value = Integer.parseInt(temp);
        String result = "";
        if(value < 10){
            result = "0" +  value;
        }else{
            result = value + "";
        }
        return result;
    }

    public void backClick(View view) {
        backHome();
    }

    @Override
    public void onBackPressed() {
        backHome();
    }

    private void backHome(){
        Intent intent = new Intent(this, MainCulqiActivity.class);
        intent.putExtra(REQUEST_TENANT, TENANT);
        startActivity(intent);
    }

    public void  sendVoucherActivity(View view) {

        if (authorizeResponse == null) {
            backHome();
        }else{

            Intent intent = new Intent(this, SalesVoucherActivity.class);
            intent.putExtra(REQUEST_TENANT, "culqi");
            intent.putExtra(REQUEST_AMOUNT, amount);
            intent.putExtra(REQUEST_PURCHASE_NUMBER, authorizeResponse.getOrder().getPurchaseNumber());
            intent.putExtra(REQUEST_AUTHORIZE, authorizeResponse);
            intent.putExtra(REQUEST_INITIALIZE, initializeResponse);
            startActivity(intent);

        }

    }
}
