package com.pax.tradepaypw;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.culqi.SalesTodayActivity;
import com.otc.model.response.InitializeResponse;
import com.otc.model.response.retrieve.TransactionsItem;
import com.otc.ui.util.UtilOtc;
import com.pax.app.TradeApplication;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.PollingResult;
import com.pax.dal.entity.PollingResult.EOperationType;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PiccDevException;
import com.pax.jemv.amex.api.ClssAmexApi;
import com.pax.jemv.amex.model.CLSS_AEAIDPARAM;
import com.pax.jemv.amex.model.TransactionMode;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.ClssTmAidList;
import com.pax.jemv.clcommon.Clss_MCAidParam;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.Clss_VisaAidParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.clcommon.OnlineResult;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.clssDPAS.trans.ClssDPAS;
import com.pax.jemv.clssentrypoint.model.TransResult;
import com.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.pax.jemv.clssexpresspay.trans.ClssExpressPay;
import com.pax.jemv.clssjspeedy.ClssJSpeedy;
import com.pax.jemv.clssjspeedy.model.Clss_JcbAidParam;
import com.pax.jemv.clsspaypass.trans.ClssPayPass;
import com.pax.jemv.clsspaywave.trans.ClssPayWave;
import com.pax.jemv.clsspure.trans.CassPure;
import com.pax.jemv.clsspure.trans.model.Clss_PureAidParam;
import com.pax.jemv.clssquickpass.trans.ClssQuickPass;
import com.pax.jemv.demo.R;
import com.pax.jemv.device.DeviceManager;
import com.pax.jemv.dpas.api.ClssDPASApi;
import com.pax.jemv.jcb.api.ClssJCBApi;
import com.pax.jemv.paypass.api.ClssPassApi;
import com.pax.jemv.paywave.api.ClssWaveApi;
import com.pax.jemv.qpboc.api.ClssPbocApi;
import com.pax.jemv.qpboc.model.Clss_PbocAidParam;
import com.pax.tradepaypw.abl.core.utils.TrackUtils;
import com.pax.tradepaypw.device.Device;
import com.pax.tradepaypw.device.myCardReaderHelper;
import com.pax.tradepaypw.pay.constant.EUIParamKeys;
import com.pax.tradepaypw.pay.trans.action.ActionSearchCard.SearchMode;
import com.pax.tradepaypw.pay.trans.callback.TradeCallback;
import com.pax.tradepaypw.service.OtherDetectCard;
import com.pax.tradepaypw.service.serviceReadType;
import com.pax.tradepaypw.utils.FileParse;
import com.pax.tradepaypw.utils.PromptMsg;
import com.pax.tradepaypw.utils.Utils;
import com.pax.tradepaypw.view.CustomEditText;
import com.pax.tradepaypw.view.dialog.CustomAlertDialog;

import java.util.Arrays;

import static com.culqi.MainCulqiActivity.REQUEST_INITIALIZE;
import static com.culqi.MainCulqiActivity.REQUEST_TENANT;
import static com.culqi.SalesDetailActivity.REQUEST_OPERATION;
import static com.culqi.SalesTodayActivity.REQUEST_TRANSACTION;
import static com.pax.tradepaypw.utils.Utils.bcd2Str;
import static com.pax.tradepaypw.utils.Utils.str2Bcd;

public class SwingCardActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SwingCardActivity";


    private static final int READ_CARD_CANCEL = 2; // 取消读卡
    private static final int READ_CARD_ERR = 3; // 读卡失败
    private EReaderType readerType = null; // 读卡类型

    private boolean supportManual = false; // 是否支持手输
    private boolean startFlg = false;
    private boolean statusFlg = false;

    private int ret = RetCode.EMV_OK;
    private CustomAlertDialog promptDialog;
    private static EReaderType readerMode;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();
    private ImplEmv emv;

    ClssTmAidList[] tmAidList;
    Clss_PreProcInfo[] preProcInfo;
    Clss_TransParam transParam;

    Intent iDetectCard;


    String pan;
    String trackData2_38;
    String trackData1;
    String trackData2;
    String trackData3;

    private String amount;
    private String TENANT;
    private String OPERATION;
    private InitializeResponse initializeResponse;
    private TransactionsItem transactionsItem;
    private static int purchaseNumber;
    TextView tvAmount;
    TextView tvTitle;
    ImageView headerBack;
    Button okBtn;
    CustomEditText edtCardNo;
    LinearLayout layoutReadCulqi;
    LinearLayout layoutReadIzipay;
    LinearLayout layoutReadVendemas;

    /**
     * 支持的寻卡类型
     */
    private byte mode; // 寻卡模式
//    private serviceReadType serReadType = serviceReadType.getInstance();
    private serviceReadType serReadType = serviceReadType.getInstance();;

    private int magRet;


    public static void setReadType(EReaderType type) {
        readerMode = type;
    }

    public static EReaderType getReadType() {
        return readerMode;
    }

    public void setStatusFlg(boolean bstatusFlg) {
        statusFlg = bstatusFlg;
    }

    public boolean getStatusFlg() {
        return statusFlg;
    }

    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //PollingResult pollingResult = null;
            switch (msg.what) {
                case READ_CARD_CANCEL:
                    Log.i("TAG", "SEARCH CARD CANCEL");
                    try {
                        //TradeApplication.dal.getCardReaderHelper().setIsPause(true);
                        myCardReaderHelper.getInstance().setIsPause(true);
                        //TradeApplication.getDal().getCardReaderHelper().stopPolling();
                        myCardReaderHelper.getInstance().stopPolling();
                        TradeApplication.getDal().getPicc(EPiccType.INTERNAL).close();
                    } catch (PiccDevException e1) {
                        Log.e(TAG, e1.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void starMagTrans() {
        pan = TrackUtils.getPan(trackData2);
        trackData2_38 = UtilOtc.getTrack2(trackData2);
        magRet = 0;
        showPan();
        Log.i(TAG, "magRet = " + magRet);
        if (magRet == TransResult.EMV_ONLINE_APPROVED) {
            toOnlineProc();
            Log.i(TAG, "Start TradeResultActivity");
            Intent intent = new Intent(this, TradeResultActivity.class);
            intent.putExtra(REQUEST_TENANT, TENANT);
            intent.putExtra("amount", amount);
            intent.putExtra("pan", pan);
            intent.putExtra("pin", "70000");
            intent.putExtra("initialize", initializeResponse);
            intent.putExtra("purchase", purchaseNumber);

            intent.putExtra("type", "band");
            intent.putExtra("track2", trackData2_38);
            intent.putExtra("pinBlock", GetPinEmv.getInstance().getPinDataEncrypt());

            startActivity(intent);
        } else {
            finish();
        }

    }

    CustomAlertDialog panDialog = null;

    private void showPan() {
        while (true) {
            if ((magRet == 0) && (panDialog == null)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Start showPan");
                        String exp = TrackUtils.getExpDate(trackData2);
                        panDialog = new CustomAlertDialog(SwingCardActivity.this, CustomAlertDialog.SUCCESS_TYPE);
                        panDialog.setTitleText(pan);
                        panDialog.setTimeout(60);
                        panDialog.setContentText(exp);
                        panDialog.show();
                        Device.beepErr();
                        panDialog.showCancelButton(true);
                        panDialog.setCancelClickListener(new CustomAlertDialog.OnCustomClickListener() {
                            @Override
                            public void onClick(CustomAlertDialog alertDialog) {
                                magRet = TransResult.EMV_ABORT_TERMINATED;
                            }
                        });
                        panDialog.showConfirmButton(true);
                        panDialog.setConfirmClickListener(new CustomAlertDialog.OnCustomClickListener() {
                            @Override
                            public void onClick(CustomAlertDialog alertDialog) {
                                magRet = TransResult.EMV_ONLINE_APPROVED;
                            }
                        });
                    }
                });
            } else {
                if (magRet != 0) {
                    Log.i(TAG, "panDialog dismiss");
                    panDialog.dismiss();
                    break;
                }
            }
            SystemClock.sleep(3000);
        }
    }

    private void startEmvTrans() {

        Log.i(TAG, "startEmvTrans: CHIP *******************");
        
        emv = new ImplEmv(SwingCardActivity.this);
        emv.ulAmntAuth = Long.parseLong(amount.replace(".", ""));
        emv.amount = amount;
        Log.i(TAG, "transParam.ulAmntAuth:" + emv.ulAmntAuth);
        emv.ulAmntOther = 15;
        emv.ulTransNo = 1;
        emv.ucTransType = 0x00;

        Log.i(TAG, "ulAmntOther " + emv.ulAmntOther);

        int ret = emv.startContactEmvTrans();
        Log.i(TAG, "startContactEmvTrans ret= " + ret);

        if (ret == TransResult.EMV_ARQC) {
            toOnlineProc();
            ret = emv.CompleteContactEmvTrans();
        }
        if (ret == TransResult.EMV_ONLINE_APPROVED || ret == TransResult.EMV_OFFLINE_APPROVED || ret == TransResult.EMV_ONLINE_CARD_DENIED) {

            byte[] track2 = ImplEmv.getTlv(0x57);

            String strTrack2 = TradeApplication.getConvert().bcdToStr(track2);

            Log.i(TAG, "startEmvTrans strTrack2 -1: " + strTrack2);

            strTrack2 = strTrack2.split("F")[0];
            pan = strTrack2.split("D")[0];

            Log.i(TAG, "startEmvTrans PinData: " + GetPinEmv.getInstance().getPinData());
            Log.i(TAG, "startEmvTrans strTrack2: " + strTrack2);
            Log.i(TAG, "startEmvTrans strTrack2: " + UtilOtc.getTrack2(strTrack2));
            Log.i(TAG, "startEmvTrans PinData: " + pan);


            if (initializeResponse != null) {

                OPERATION = OPERATION == null ? "": OPERATION;

                if (OPERATION.equals("cancel")) {

                    Intent intent = new Intent(this, TradeResultActivity.class);
                    intent.putExtra(REQUEST_TENANT, TENANT);
                    intent.putExtra(REQUEST_OPERATION, "cancel");
                    intent.putExtra(REQUEST_TRANSACTION, transactionsItem);
                    intent.putExtra(REQUEST_INITIALIZE, initializeResponse);

                    intent.putExtra("amount", amount);
                    intent.putExtra("pan", pan);
                    intent.putExtra("track2", UtilOtc.getTrack2(strTrack2));
                    intent.putExtra("pinBlock", GetPinEmv.getInstance().getPinDataEncrypt());
                    intent.putExtra("type", "chip");
                    intent.putExtra("pin", "60000");
                    intent.putExtra("purchase", purchaseNumber);
                    startActivity(intent);

                } else if (OPERATION.equals("search")) {

                    Intent intent = new Intent(this, SalesTodayActivity.class);
                    intent.putExtra(REQUEST_TENANT, TENANT);
                    intent.putExtra(REQUEST_OPERATION, OPERATION);
                    intent.putExtra(REQUEST_TRANSACTION, transactionsItem);
                    intent.putExtra(REQUEST_INITIALIZE, initializeResponse);

                    intent.putExtra("amount", amount);
                    intent.putExtra("pan", pan);
                    intent.putExtra("track2", UtilOtc.getTrack2(strTrack2));
                    startActivity(intent);

                }else if (OPERATION.equals("report")) {

                    Intent intent = new Intent(this, SalesTodayActivity.class);
                    intent.putExtra(REQUEST_TENANT, TENANT);
                    intent.putExtra(REQUEST_OPERATION, OPERATION);
                    intent.putExtra(REQUEST_TRANSACTION, transactionsItem);
                    intent.putExtra(REQUEST_INITIALIZE, initializeResponse);

                    intent.putExtra("amount", amount);
                    intent.putExtra("pan", pan);
                    intent.putExtra("track2", UtilOtc.getTrack2(strTrack2));
                    startActivity(intent);

                }else{

                    Intent intent = new Intent(this, TradeResultActivity.class);
                    intent.putExtra(REQUEST_TENANT, TENANT);
                    intent.putExtra("amount", amount);
                    intent.putExtra("pan", pan);
                    intent.putExtra("track2", UtilOtc.getTrack2(strTrack2));
                    intent.putExtra("pinBlock", GetPinEmv.getInstance().getPinDataEncrypt());
                    intent.putExtra("type", "chip");
                    intent.putExtra("pin", "60001");
                    intent.putExtra("initialize", initializeResponse);
                    intent.putExtra("purchase", purchaseNumber);
                    startActivity(intent);
                }

            }else{
                    Intent intent = new Intent(this, TradeResultActivity.class);
                    intent.putExtra(REQUEST_TENANT, TENANT);
                    intent.putExtra("amount", amount);
                    intent.putExtra("pan", pan);
                    intent.putExtra("track2", UtilOtc.getTrack2(strTrack2));
                    intent.putExtra("pinBlock", GetPinEmv.getInstance().getPinDataEncrypt());
                    intent.putExtra("type", "chip");
                    intent.putExtra("pin", "60002");
                    startActivity(intent);
            }

        } else {
            showErr(ret);
        }
    }

    private void toOnlineProc() {
        while (true) {
            if (promptDialog == null) {
                Log.i(TAG, "toOnlineProc promptDialog == null");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        promptDialog = new CustomAlertDialog(SwingCardActivity.this, CustomAlertDialog.PROGRESS_TYPE);

                        promptDialog.show();
                        promptDialog.setCancelable(false);
                        promptDialog.setTitleText(getString(R.string.prompt_online));

                    }
                });
            } else {
                Log.i(TAG, "promptDialog dismiss");
                promptDialog.dismiss();
                break;
            }
            SystemClock.sleep(4000);
        }
    }

    private void starPiccTrans() {

        Log.i(TAG, "starPiccTrans: ready piccTrans");
        while (true) {
            setStatusFlg(false);
            serReadType.setrReadType(EReaderType.DEFAULT.getEReaderType());
            ret = entryPoint.entryProcess();
            if (ret != RetCode.EMV_OK) {
                if (ret == RetCode.CLSS_TRY_AGAIN) {
                    ret = entryPoint.setConfigParam((byte) /*0x37*/0x36, false, tmAidList, preProcInfo);
                    if (ret != RetCode.EMV_OK) {
                        showErr(ret);
                        Log.e(TAG, "setConfigParam ret = " + ret);
                        return;
                    }
                    ret = entryPoint.preEntryProcess(transParam);
                    if (ret != RetCode.EMV_OK) {
                        showErr(ret);
                        Log.e(TAG, "preEntryProcess ret = " + ret);
                        return;
                    }
                    continue;
                } else {
                    showErr(ret);
                    Log.e(TAG, "entryProcess ret = " + ret);
                    return;
                }
            }

            //**************************************************************************************
            Log.i(TAG, "starPiccTrans ucKernType : " + ClssEntryPoint.getInstance().getOutParam().ucKernType);
            ///************************************************************************************

            switch (ClssEntryPoint.getInstance().getOutParam().ucKernType) {
                case KernType.KERNTYPE_MC:
                    ret = startMC();
                    break;
                case KernType.KERNTYPE_VIS:
                    ret = startVIS();
                    break;
                case KernType.KERNTYPE_AE:
                    ret = startAE();
                    break;
                case KernType.KERNTYPE_ZIP:
                    ret = startDPAS();
                    break;
                case KernType.KERNTYPE_PBOC:
                    ret = startqpboc();
                    break;
                case KernType.KERNTYPE_JCB:
                    ret = startJCB();
                    break;
                case KernType.KERNTYPE_PURE:
                    ret = startPure();
                    break;
                default:
                    Log.e(TAG, "KernType error, type = " + entryPoint.getOutParam().ucKernType);
                    showErr(PromptMsg.ONLY_PAYPASS_PAYWAVE);
                    break;
            }
            if (ret == RetCode.CLSS_TRY_AGAIN || ret == RetCode.CLSS_REFER_CONSUMER_DEVICE) {
                ret = entryPoint.setConfigParam((byte) /*0x37*/0x36, false, tmAidList, preProcInfo);
                if (ret != RetCode.EMV_OK) {
                    showErr(ret);
                    Log.e(TAG, "setConfigParam ret = " + ret);
                    return;
                }
                ret = entryPoint.preEntryProcess(transParam);
                if (ret != RetCode.EMV_OK) {
                    showErr(ret);
                    Log.e(TAG, "preEntryProcess ret = " + ret);
                    return;
                }
                continue;
            } else if (ret != 0) {
                showErr(ret);
                return;
            }
            break;
        }
    }

    //Gillian end 20170522
    private void showErr(final int ret) {

        if (getReadType() != null) {
            SystemClock.sleep(300);
            Log.i(TAG, "getReadType=" + getReadType().getEReaderType());
            Log.i(TAG, "readType=" + serReadType.getrReadType());
            if (getReadType().getEReaderType() == EReaderType.PICC.getEReaderType()) {
                if (serReadType.getrReadType() == EReaderType.MAG.getEReaderType()) {
                    setReadType(EReaderType.MAG);
                    Log.i(TAG, " EReaderType.MAG");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            starMagTrans();
                        }
                    }).start();
                    return;
                } else if (serReadType.getrReadType() == EReaderType.ICC.getEReaderType()) {
                    setReadType(EReaderType.ICC);
                    Log.i(TAG, " EReaderType.ICC");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startEmvTrans();
                        }
                    }).start();
                    return;
                }
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = PromptMsg.getErrorMsg(ret);
                final CustomAlertDialog dialog = new CustomAlertDialog(SwingCardActivity.this, CustomAlertDialog.ERROR_TYPE);
                dialog.setTitleText(msg);
                dialog.show();
                Device.beepErr();
                dialog.showConfirmButton(true);
                //dialog.showCancelButton(true);
                dialog.setConfirmClickListener(new CustomAlertDialog.OnCustomClickListener() {
                    @Override
                    public void onClick(CustomAlertDialog alertDialog) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        });
    }

    private int startVIS() {
        Clss_PreProcInfo procInfo = null;
        TransResult transResult = new TransResult();
        ClssPayWave.getInstance().setCallback(new TradeCallback(this));
        //ClssPayWave.getInstance().coreInit();

        byte[] aucCvmReq = new byte[2];
        aucCvmReq[0] = CvmType.RD_CVM_REQ_SIG;
        aucCvmReq[1] = CvmType.RD_CVM_REQ_ONLINE_PIN;
        Clss_VisaAidParam visaAidParam = new Clss_VisaAidParam(100000, (byte) 0, (byte) 2, aucCvmReq, (byte) 0);
        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                procInfo = FileParse.getPreProcInfos()[i];
                break;
            }
        }
        ret = ClssPayWave.getInstance().setConfigParam(visaAidParam, procInfo);

        prnTime("startVIS set Param time = ");
        ret = ClssPayWave.getInstance().waveProcess(transResult);
        Log.i(TAG, "waveProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
//        endDate = new Date(System.currentTimeMillis());
//        diff = endDate.getTime() - startDate.getTime();
//        Log.e(TAG, "ClssPayWave setConfigParam diff = " + diff);

        if (ret == 0) {
            successProcess(ClssPayWave.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssPayWave.getInstance().getCVMType());
        }
        return ret;
    }
    
    private int startMC() {
        Clss_PreProcInfo procInfo = null;
        Clss_MCAidParam aidParam = null;
        TransResult transResult = new TransResult();
        ClssPayPass.getInstance().setCallback(new TradeCallback(this));
        //ClssPayPass.getInstance().setCallback(TradeCallback.getInstance(this));

        //ClssPayPass.getInstance().coreInit((byte) 1);

        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getMcAidParams()[i];
                break;
            }
        }
        ClssPayPass.getInstance().setConfigParam(aidParam, procInfo);

        Log.i(TAG, "startMC: Device.beepErr();");
        ret = ClssPayPass.getInstance().passProcess(transResult);
        Device.beepErr();
        Log.i(TAG, "passProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
        if (ret == 0) {
            successProcess(ClssPayPass.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssPayPass.getInstance().getCVMType());
        }
        return ret;
    }

    private int startConlssPBOC(TransResult transResult) {
        emv = new ImplEmv(SwingCardActivity.this);
        emv.ulAmntAuth = entryPoint.getTransParam().ulAmntAuth;
        emv.amount = amount;
        Log.i(TAG, "transParam.ulAmntAuth:" + emv.ulAmntAuth);
        emv.ulAmntOther = entryPoint.getTransParam().ulAmntOther;
        emv.ulTransNo = entryPoint.getTransParam().ulTransNo;
        emv.ucTransType = entryPoint.getTransParam().ucTransType;
        setReadType(EReaderType.ICC);
        int ret = emv.startClssPBOC(transResult);
        Log.i(TAG, "startConlessPBOC ret= " + ret);
        return ret;
    }

    private int startqpboc() {
        String ssAID;
        String listAID;
        int cvmType;

        Clss_PreProcInfo procInfo = null;
        Clss_PbocAidParam aidParam = null;
        TransResult transResult = new TransResult();

        ClssQuickPass.getInstance().setCallback(new TradeCallback(this));
        ssAID = bcd2Str(ClssEntryPoint.getInstance().getOutParam().sAID, ClssEntryPoint.getInstance().getOutParam().iAIDLen);
        //Log.i(TAG, "sAID  = " + ssAID);
        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            listAID = bcd2Str(FileParse.getPreProcInfos()[i].aucAID, FileParse.getPreProcInfos()[i].ucAidLen);
            if (ssAID.indexOf(listAID) != -1) {
                //Log.i(TAG, "ssAID.indexOf(listAID) OK");
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getPbocAidParams()[i];
                break;
            }
        }

        //Log.i(TAG, "aidParam.ucAETermCap  = " + Integer.toHexString(aidParam.ucAETermCap) );
        ret = ClssQuickPass.getInstance().setConfigParam(aidParam, procInfo);

        ret = ClssQuickPass.getInstance().qPbocProcess(transResult);
        Log.i(TAG, "ClssQuickPass ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
        if (ret == 0) {
            cvmType = ClssQuickPass.getInstance().getCVMType();
            if (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC) {   //Contact PBOC
                ret = startConlssPBOC(transResult);
                cvmType = CvmType.RD_CVM_NO;
            }
            if (ret == 0) {
                successProcess(cvmType, transResult.result);
                Log.i(TAG, "cvm = " + ClssQuickPass.getInstance().getCVMType());
            }
        }
        return ret;
    }

    private int startAE() {
        int ret;
        String ssAID;
        String listAID;

        Clss_PreProcInfo procInfo = null;
        CLSS_AEAIDPARAM aidParam = null;
        TransResult transResult = new TransResult();
        ClssExpressPay.getInstance().setCallback(new TradeCallback(this));

        ssAID = bcd2Str(ClssEntryPoint.getInstance().getOutParam().sAID, ClssEntryPoint.getInstance().getOutParam().iAIDLen);
        //Log.i(TAG, "sAID  = " + ssAID);
        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            listAID = bcd2Str(FileParse.getPreProcInfos()[i].aucAID, FileParse.getPreProcInfos()[i].ucAidLen);
            if (ssAID.indexOf(listAID) != -1) {
                //Log.i(TAG, "ssAID.indexOf(listAID) OK");
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getAeAidParams()[i];
                break;
            }
        }

        ClssExpressPay.getInstance().setConfigParam(aidParam, procInfo);

        ret = ClssExpressPay.getInstance().expressProcess(transResult);
        Log.i(TAG, "expressProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
        if (ret == 0) {
            successProcess(ClssExpressPay.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssExpressPay.getInstance().getCVMType());
        }
        return ret;
    }

    private int startDPAS() {
        // Clss_PreProcInfo procInfo = null;
        TransResult transResult = new TransResult();
        ClssDPAS.getInstance().setCallback(new TradeCallback(this));

    /*    for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                procInfo = FileParse.getPreProcInfos()[i];
                break;
            }
        }*/
        ClssDPAS.getInstance().setConfigParam();
        ret = ClssDPAS.getInstance().DPASProcess(transResult);
        Log.i(TAG, "DPASProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);
        if (ret == 0) {
            successProcess(ClssDPAS.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssDPAS.getInstance().getCVMType());
        }
        return ret;
    }

    private int startJCB() {
        Clss_PreProcInfo procInfo = null;
        Clss_JcbAidParam aidParam = null;
        TransResult transResult = new TransResult();
        ClssJSpeedy.getInstance().setCallback(new TradeCallback(this));
        //ClssPayWave.getInstance().coreInit();


        byte[] aucCvmReq = new byte[2];
        aucCvmReq[0] = CvmType.RD_CVM_REQ_SIG;
        aucCvmReq[1] = CvmType.RD_CVM_REQ_ONLINE_PIN;
        //Clss_VisaAidParam visaAidParam = new Clss_VisaAidParam(100000, (byte) 0, (byte) 2, aucCvmReq, (byte) 0);

        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                Log.i(TAG, "ClssEntryPoint.getInstance().getOutParam().sAID = " + bcd2Str(ClssEntryPoint.getInstance().getOutParam().sAID, ClssEntryPoint.getInstance().getOutParam().iAIDLen));
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getJcbAidParams()[i];
                break;
            }
        }
        ClssJSpeedy.getInstance().setConfigParam(aidParam, procInfo);
        ret = ClssJSpeedy.getInstance().jspeedyProcess(transResult);
        Log.i(TAG, "jspeedyProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);

        if (ret == 0) {
            successProcess(ClssJSpeedy.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + ClssJSpeedy.getInstance().getCVMType());
        }
        return ret;
    }

    private int startPure() {
        Clss_PreProcInfo procInfo = null;
        Clss_PureAidParam aidParam = null;
        TransResult transResult = new TransResult();
        CassPure.getInstance().setCallback(new TradeCallback(this));
        //ClssPayWave.getInstance().coreInit();


        byte[] aucCvmReq = new byte[2];
        aucCvmReq[0] = CvmType.RD_CVM_REQ_SIG;
        aucCvmReq[1] = CvmType.RD_CVM_REQ_ONLINE_PIN;
        //Clss_VisaAidParam visaAidParam = new Clss_VisaAidParam(100000, (byte) 0, (byte) 2, aucCvmReq, (byte) 0);

        for (int i = 0; i < FileParse.getPreProcInfos().length; i++) {
            if (Arrays.equals(ClssEntryPoint.getInstance().getOutParam().sAID,
                    FileParse.getPreProcInfos()[i].aucAID)) {
                //Log.i(TAG, "ClssEntryPoint.getInstance().getOutParam().sAID = " + bcd2Str(ClssEntryPoint.getInstance().getOutParam().sAID, ClssEntryPoint.getInstance().getOutParam().iAIDLen));
                procInfo = FileParse.getPreProcInfos()[i];
                aidParam = FileParse.getPureAidParams()[i];
                break;
            }
        }

        CassPure.getInstance().setConfigParam(aidParam, procInfo);
        ret = CassPure.getInstance().pureProcess(transResult);
        Log.i(TAG, "pureProcess ret = " + ret);
        Log.i(TAG, "transResult = " + transResult.result);

        if (ret == 0) {
            successProcess(CassPure.getInstance().getCVMType(), transResult.result);
            Log.i(TAG, "cvm = " + CassPure.getInstance().getCVMType());
        }
        return ret;
    }


    private void successProcess(int cvmType, int result) {
        ByteArray tk2 = new ByteArray();
        if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_MC) {
            Log.i(TAG, "successProcess: 1000");
            if (ClssPayPass.getInstance().getTransPath() == TransactionPath.CLSS_MC_MAG) {
                Log.i(TAG, "successProcess: 1100");
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9f, (byte) 0x6B}, (byte) 2, 60, tk2);
            } else if (ClssPayPass.getInstance().getTransPath() == TransactionPath.CLSS_MC_MCHIP) {
                Log.i(TAG, "successProcess: 1200");
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{0x57}, (byte) 1, 60, tk2);

            }

        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_VIS) {
            Log.i(TAG, "successProcess: 1300");

            ClssWaveApi.Clss_GetTLVData_Wave((short) 0x57, tk2);

        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_AE) {
            Log.i(TAG, "successProcess: 1400");
            if (ClssExpressPay.getInstance().getTransPath() == TransactionMode.AE_MAGMODE) {
                Log.i(TAG, "successProcess: 1500");
                ClssAmexApi.Clss_nGetTrackMapData_AE((byte) 0x02, tk2);
            }

            if (tk2.length == 0){
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x57, tk2);
                Log.i(TAG, "successProcess: 1600");
            }


        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_ZIP) {
            Log.i(TAG, "successProcess: 1700");
            ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{0x57}, (byte) 1, 60, tk2);

        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PBOC) {
            Log.i(TAG, "successProcess: 1800");
            if (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC) {
                Log.i(TAG, "successProcess: 1900");
                tk2.data = ImplEmv.getTlv(0x57);
                tk2.length = tk2.data.length;
            } else{
                Log.i(TAG, "successProcess: 2000");
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x57, tk2);
            }

        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_JCB) {
            Log.i(TAG, "successProcess: 2100");
            if (ClssJSpeedy.getInstance().getTransPath() == TransactionPath.CLSS_JCB_MAG) {
                Log.i(TAG, "successProcess: 2200");
                int ret = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x6B}, (byte) 1, 60, tk2);
                if (ret != RetCode.EMV_OK) {
                    Log.i(TAG, "successProcess: 2300");
                    ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{0x57}, (byte) 1, 60, tk2);
                }
            } else{
                Log.i(TAG, "successProcess: 2400");
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{0x57}, (byte) 1, 60, tk2);
            }

        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PURE) {
            Log.i(TAG, "successProcess: 2500");
            ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{0x57}, (byte) 1, 60, tk2);
        }
        //trk
        pan = TrackUtils.getPan(bcd2Str(tk2.data));
        trackData2_38 = UtilOtc.getTrack2(bcd2Str(tk2.data));


        //**********************


        Log.i(TAG, "successProcess trackData2_38 : " + trackData2_38);

        Log.i(TAG, "cvmType = " + cvmType);

        if (OPERATION.equals("search")) {

            toTradeResultActivity();

        }else{

            if (cvmType == CvmType.RD_CVM_ONLINE_PIN) {
                toConsumeActitivy(result, cvmType);
            } else if (cvmType == (CvmType.RD_CVM_ONLINE_PIN + CvmType.RD_CVM_SIG)) {
                toConsumeActitivy(result, cvmType);
            } else if (cvmType == CvmType.RD_CVM_NO) {
                if (result == TransResult.EMV_ARQC) {
                    toTradeResultActivity();
                } else if (result == TransResult.EMV_OFFLINE_APPROVED) {
                    toTradeResultActivityTc();
                }
            } else {
                if (result == TransResult.EMV_ARQC) {
                    toTradeResultActivity();
                } else if (result == TransResult.EMV_OFFLINE_APPROVED) {
                    toTradeResultActivityTc();
                }
            }

        }




    }

    private void toTradeResultActivity() {
        while (true) {
            if (promptDialog == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        promptDialog = new CustomAlertDialog(SwingCardActivity.this, CustomAlertDialog.PROGRESS_TYPE);

                        promptDialog.show();
                        promptDialog.setCancelable(false);
                        promptDialog.setTitleText(getString(R.string.prompt_online));

                    }
                });
            } else {
                Log.i(TAG, "promptDialog dismiss");
                promptDialog.dismiss();
                break;
            }
            SystemClock.sleep(3000);
        }
        if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_AE) {
            int result = OnlineResult.ONLINE_APPROVE;
            byte[] aucRspCode = "00".getBytes();
            byte[] aucAuthCode = "123456".getBytes();

            int sgAuthDataLen = 5;
            byte[] sAuthData = Utils.str2Bcd("1234567890");
            byte[] sIssuerScript = Utils.str2Bcd("9F1804AABBCCDD86098424000004AABBCCDD");
            int sgScriptLen = 18;
            ClssExpressPay.getInstance().amexFlowComplete(result, aucRspCode, aucAuthCode, sAuthData, sgAuthDataLen, sIssuerScript, sgScriptLen);
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_JCB) {
            if (ClssJSpeedy.getInstance().getTransPath() == TransactionPath.CLSS_JCB_EMV) {
                byte[] sIssuerScript = Utils.str2Bcd("9F1804AABBCCDD86098424000004AABBCCDD");
                int sgScriptLen = 18;
                ClssJSpeedy.getInstance().jcbFlowComplete(sIssuerScript, sgScriptLen);
            }
        } else if (ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PBOC) {
            if (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC) {   //Contact PBOC
                emv.CompleteContactEmvTrans();
                //TradeCallback.getInstance(SwingCardActivity.this).removeCardPrompt();
            }
        }

        //******************************************************************************************

        //******************************************************************************************
        Log.i(TAG, "Start TradeResultActivity");
        Intent intent = new Intent(this, TradeResultActivity.class);
        intent.putExtra(REQUEST_TENANT, TENANT);
        intent.putExtra("amount", amount);
        intent.putExtra("pan", pan);
        intent.putExtra("pin", "40000");
        intent.putExtra("type", "contactless");
        intent.putExtra("track2", trackData2_38);
        intent.putExtra("pinBlock", GetPinEmv.getInstance().getPinDataEncrypt());
        intent.putExtra("initialize", initializeResponse);
        intent.putExtra("purchase", purchaseNumber);
        startActivity(intent);
    }

    private void toTradeResultActivityTc() {
        while (true) {
            if (promptDialog == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        promptDialog = new CustomAlertDialog(SwingCardActivity.this, CustomAlertDialog.PROGRESS_TYPE);

                        promptDialog.show();
                        promptDialog.setCancelable(false);
                        promptDialog.setTitleText(getString(R.string.prompt_offline));

                    }
                });
            } else {
                Log.i(TAG, "promptDialog dismiss");
                promptDialog.dismiss();
                break;
            }
            SystemClock.sleep(3000);
        }
        Log.i(TAG, "Start TradeResultActivity");
        Intent intent = new Intent(this, TradeResultActivity.class);
        intent.putExtra(REQUEST_TENANT, TENANT);
        intent.putExtra("amount", amount);
        intent.putExtra("pan", pan);
        intent.putExtra("track2", trackData2_38);
        intent.putExtra("pinBlock", GetPinEmv.getInstance().getPinDataEncrypt());
        intent.putExtra("initialize", initializeResponse);
        intent.putExtra("type", "contactless");

        startActivity(intent);
    }

    private void toConsumeActitivy(int result, int cvmtype) {

        Intent intent = new Intent(SwingCardActivity.this, ConsumeActivity.class);
        intent.putExtra(REQUEST_TENANT, TENANT);
        intent.putExtra("amount", amount);
        intent.putExtra("pan", pan);
        intent.putExtra("result", result);
        intent.putExtra("cvmtype", cvmtype);
        intent.putExtra("pin", "2000");
        intent.putExtra("initialize", initializeResponse);
        intent.putExtra("purchase", purchaseNumber);
        startActivity(intent);
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte tmpType = intent.getByteExtra("TYPE", (byte) -1);
            Log.i(TAG, "BroadcastReceiver, readType=" + tmpType);
            serReadType.setrReadType(tmpType);
            if (tmpType == EReaderType.MAG.getEReaderType()) {
                Device.beepPromt();
                trackData1 = intent.getStringExtra("TRK1");
                trackData2 = intent.getStringExtra("TRK2");
                trackData3 = intent.getStringExtra("TRK3");
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcad_pay);

        Log.i(TAG, "onCreate: LEER TARJETA");

        initView();

        Intent intent = getIntent();
        TENANT = intent.getStringExtra(REQUEST_TENANT);
        OPERATION = intent.getStringExtra(REQUEST_OPERATION);
        amount = intent.getStringExtra("amount");
        initializeResponse = intent.getParcelableExtra(REQUEST_INITIALIZE);
        transactionsItem = intent.getParcelableExtra(REQUEST_TRANSACTION);
        purchaseNumber = intent.getIntExtra("purchase", 0);

        Log.i(TAG, "onCreate: " + OPERATION);

        tvAmount.setText(amount);
        headerBack.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        edtCardNo.setIMEEnabled(false, true);

        serReadType.setrReadType(EReaderType.DEFAULT.getEReaderType());
        loadParam();
        DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());

        Log.i(TAG, "readerType 1 = " + readerType.getEReaderType());
        initClssTrans();
        Log.i(TAG, "readerType 2 = " + readerType.getEReaderType());

        iDetectCard = new Intent(this, OtherDetectCard.class);
        iDetectCard.putExtra("readType", readerType.getEReaderType());
        iDetectCard.putExtra("iccSlot", (byte) 0);
        startService(iDetectCard);
        //接收器的动态注册，Action必须与Service中的Action一致
        registerReceiver(br, new IntentFilter("ACTION_DETECT"));
        startFlg = true;


        //*****************************************************************************************/
        OPERATION = (OPERATION != null ? OPERATION: "");

        if (OPERATION.equals("search")) {
            tvTitle.setText("Consulta");
        }

        TENANT = (TENANT != null ? TENANT: "");

        switch (TENANT){
            case "culqi":

                layoutReadCulqi.setVisibility(View.VISIBLE);
                layoutReadIzipay.setVisibility(View.GONE);
                layoutReadVendemas.setVisibility(View.GONE);

                break;

            case "izipay":

                layoutReadCulqi.setVisibility(View.GONE);
                layoutReadIzipay.setVisibility(View.VISIBLE);
                layoutReadIzipay.setBackgroundColor(getResources().getColor(R.color.izipay_pink2));
                layoutReadVendemas.setVisibility(View.GONE);
                break;

            case "vendemas":

                layoutReadCulqi.setVisibility(View.GONE);
                layoutReadIzipay.setVisibility(View.GONE);
                layoutReadVendemas.setVisibility(View.VISIBLE);
                layoutReadVendemas.setBackgroundColor(getResources().getColor(R.color.vendemas_yellow));
                break;

            default:
        }

    }

    private void initView() {
        tvTitle = findViewById(R.id.header_title);
        tvAmount = findViewById(R.id.tv_amount);
        headerBack = findViewById(R.id.header_back);
        okBtn = findViewById(R.id.ok_btn);
        edtCardNo = findViewById(R.id.bank_card_number);
        layoutReadCulqi = findViewById(R.id.layout_read_culqi);
        layoutReadIzipay = findViewById(R.id.layout_read_izipay);
        layoutReadVendemas = findViewById(R.id.layout_read_vendemas);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (startFlg) {
            new SearchCardThread().start();
            startFlg = false;
        }
    }


    private void initClssTrans() {
        transParam = new Clss_TransParam();
        transParam.ulAmntAuth = Long.parseLong(amount.replace(".", ""));
        transParam.ulAmntOther = 0;
        transParam.ulTransNo = 1;
        transParam.ucTransType = 0x00;

        String transDate = TradeApplication.getDal().getSys().getDate();
        System.arraycopy(str2Bcd(transDate.substring(2, 8)), 0, transParam.aucTransDate, 0, 3);
        String transTime = TradeApplication.getDal().getSys().getDate();
        System.arraycopy(str2Bcd(transTime.substring(8)), 0, transParam.aucTransTime, 0, 3);
        Log.i(TAG, "transParam.aucTransDate: " + bcd2Str(transParam.aucTransDate));
        Log.i(TAG, "transParam.aucTransTime: " + bcd2Str(transParam.aucTransTime));

        tmAidList = FileParse.getTmAidLists();
        preProcInfo = FileParse.getPreProcInfos();
        entryPoint.coreInit();
        ClssPayWave.getInstance().coreInit();
        ClssPayPass.getInstance().coreInit((byte) 1);
        ClssExpressPay.getInstance().coreInit();
        ClssDPAS.getInstance().coreInit();
        ClssQuickPass.getInstance().coreInit();
        ClssJSpeedy.getInstance().coreInit();
        CassPure.getInstance().coreInit();

        ret = entryPoint.setConfigParam((byte) /*0x37*/0x36, false, tmAidList, preProcInfo);
        if (ret != RetCode.EMV_OK) {
            showErr(ret);
            Log.e(TAG, "setConfigParam ret = " + ret);
            return;
        }
        ret = entryPoint.preEntryProcess(transParam);
        if (ret != RetCode.EMV_OK) {
            showErr(ret);
            Log.e(TAG, "preEntryProcess ret = " + ret);
        }

    }

    protected void loadParam() {
        Bundle bundle = getIntent().getExtras();
        // 寻卡方式，默认挥卡
        try {
            mode = bundle.getByte(EUIParamKeys.CARD_SEARCH_MODE.toString(), (byte) (SearchMode.INSERT_TAP | SearchMode.SWIPE));
            if ((mode & SearchMode.KEYIN) == SearchMode.KEYIN) { // 是否支持手输卡号
                supportManual = true;
            } else {
                supportManual = false;
            }

            readerType = toReaderType(mode);
        } catch (Exception e) {
            Log.e("loadParam", e.getMessage());
        }
    }

    /**
     * 获取ReaderType
     *
     * @param mode
     * @return
     */
    private EReaderType toReaderType(byte mode) {
        mode &= ~SearchMode.KEYIN;
        EReaderType[] types = EReaderType.values();
        for (EReaderType type : types) {
            if (type.getEReaderType() == mode)
                return type;
        }
        return null;
    }

    public static void prnTime(String msg) {
        //return;
//        endDate = new Date(System.currentTimeMillis());
//        long diff = endDate.getTime() - startDate.getTime();
//        Log.e(TAG, msg + diff);
//        startDate = new Date(System.currentTimeMillis());

    }

    // thread initial read card
    class SearchCardThread extends Thread {

        @Override
        public void run() {
            try {
                SystemClock.sleep(500);  //waiting for load screen
                //startDate = new Date(System.currentTimeMillis());
                //DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());

                //special for visa certification, the time of un-contactless processing is not more than 100ms
                //ICardReaderHelper cardReaderHelper = TradeApplication.getDal().getCardReaderHelper();
                if (readerType == null) {
                    return;
                }
                //android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                //special for visa certification, the time of un-contactless processing is not more than 100ms
                //pollingResult = cardReaderHelper.polling(readerType, 60 * 1000);
                //Log.i(TAG, "readerType = " + readerType);
                //pollingResult = myCardReaderHelper.getInstance().polling(EReaderType.PICC, 60 * 1000);
                PollingResult pollingResult = myCardReaderHelper.getInstance().polling(readerType, 60 * 1000);
                //pollingResult = cardReaderHelper.polling(EReaderType.PICC, 60*1000);
                prnTime("myCardReaderHelper.polling diff = ");

                if (pollingResult.getOperationType() == EOperationType.CANCEL
                        || pollingResult.getOperationType() == EOperationType.TIMEOUT) {
                    //cardReaderHelper.stopPolling();
                    myCardReaderHelper.getInstance().stopPolling();  //only for cancel read card
                    Log.i("TAG", "CANCEL | TIMEOUT");
                    handler.sendEmptyMessage(READ_CARD_CANCEL);
                } else {
                    //handler.sendEmptyMessage(READ_CARD_OK);
                    if (pollingResult.getReaderType() == EReaderType.MAG) { //BANDA
                        setReadType(EReaderType.MAG);
                        Log.i(TAG, " EReaderType.MAG");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                starMagTrans();
                            }
                        }).start();
                    } else if (pollingResult.getReaderType() == EReaderType.ICC) { // CHIP
                        setReadType(EReaderType.ICC);
                        Log.i(TAG, " EReaderType.ICC");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                startEmvTrans();
                            }
                        }).start();
                    } else if (pollingResult.getReaderType() == EReaderType.PICC) { //CONTACTLESS
                        setReadType(EReaderType.PICC);
                        Log.i(TAG, " EReaderType.PICC");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                prnTime("thread call in  time = ");
                                starPiccTrans();
                            }
                        }).start();
                    }
                }
            } catch (PiccDevException | IccDevException | MagDevException e) {
                Log.e(TAG, e.getMessage());
                handler.sendEmptyMessage(READ_CARD_ERR);
            }
        }
    }

    @Override
    protected void onStop() {
        handler.removeCallbacksAndMessages(null);
        stopService(iDetectCard);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        //System.exit(0);
        //finish();
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handler.sendEmptyMessage(READ_CARD_CANCEL);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_back:
                handler.sendEmptyMessage(READ_CARD_CANCEL);
                finish();
                break;
            case R.id.ok_btn:
                //test
                Intent intent = new Intent(SwingCardActivity.this, ConsumeActivity.class);
                intent.putExtra(REQUEST_TENANT, TENANT);
                intent.putExtra("amount", amount);
                intent.putExtra("pan", pan);
                intent.putExtra("pin", "10000");
                intent.putExtra("initialize", initializeResponse);
                intent.putExtra("purchase", purchaseNumber);
                startActivity(intent);
                break;
            default:
                break;

        }
    }

}
