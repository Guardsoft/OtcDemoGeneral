package com.otc.ui.util;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Scroller;
import android.widget.TextView;

import com.culqi.signature.MacRetailUtil;
import com.culqi.signature.RequestToSign;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.otc.model.request.authorize.AuthorizeRequest;
import com.otc.model.response.InitializeResponse;
import com.pax.app.IConvert;
import com.pax.app.TradeApplication;
import com.pax.dal.entity.ECryptOperate;
import com.pax.dal.entity.ECryptOpt;
import com.pax.dal.entity.EPedType;
import com.pax.dal.exceptions.PedDevException;
import com.pax.tradepaypw.device.Device;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class UtilOtc extends Application {

    private static final String TAG = "UtilOtc";

    private static InitializeResponse initializeResponse;

    public static InitializeResponse getInitializeResponse() {
        return initializeResponse;
    }

    public static void setInitializeResponse(InitializeResponse initializeResponse) {
        UtilOtc.initializeResponse = initializeResponse;
    }

    private static UtilOtc otcUtil;

    public UtilOtc() {
    }

    public  static UtilOtc getInstance() {
        if (otcUtil == null) {
            otcUtil = new UtilOtc();
        }
        return otcUtil;
    }

    public static Map<String, String> getSignatureRequest(AuthorizeRequest authorizeRequest) throws Exception {

        String accessKey = String.format("%s-%s",
                authorizeRequest.getMerchant().getMerchantId(),
                authorizeRequest.getDevice().getTerminalId());

        String host = "culqimpos.quiputech.com";
        String path = "/api.authorization/v3/culqi/authorize";

        String payload = toJsonPretty(authorizeRequest);

        RequestToSign request = RequestToSign.builder()
                .withAccessKey(accessKey)
                .withHost(host)
                .withMethod("POST")
                .withPath(path)
                .withRegion("global")
                .withService("authentication")
                .withPayload(payload)
                .withQueryParams(null)
                .build();


        return MacRetailUtil.sign("", request);
    }

    public void dialogResult(Context context, String msg){

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("MENSAJE")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, (dialog1, which) -> dialog1.dismiss())
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setScroller(new Scroller(context));
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        //return serialNumber;
        return TradeApplication.TERMINAL_CODE;
    }

    public static void writeKeysDataPin(String data, String pin) {

        Log.i(TAG, "writeKeysDataPin: ENCRIPTANDO DATA PIN");

        String decryptInit = Device.decrypt3DesCBC(data, TradeApplication.INDEX_TDK);
        String llaveReencriptada = Device.encrypt3DesEBC(decryptInit, TradeApplication.INDEX_TDK);

        int slotDataTAESK10 = 10;

        byte[] bytesTDKData10 = TradeApplication
                .getConvert()
                .strToBcd(llaveReencriptada, IConvert.EPaddingPosition.PADDING_LEFT);

        Device.writeTAESK2(TradeApplication.INDEX_TMK, slotDataTAESK10, bytesTDKData10);
        //Device.writeTAESK3(slotDataTAESK10, bytesTDKData10);
        //*****************************************************************************************
        String decryptInitPin = Device.decrypt3DesCBC(pin, TradeApplication.INDEX_TDK);
        String llaveTpkClaro = decryptInit.substring(0,32);

        String llaveReencriptadaPin = Device.encrypt3DesEBC(decryptInitPin, TradeApplication.INDEX_TDK);

        int slotDataTAESK11 = 11;

        byte[] bytesTDKPin11 = TradeApplication
                .getConvert()
                .strToBcd(llaveReencriptadaPin, IConvert.EPaddingPosition.PADDING_LEFT);

        Device.writeTAESK2(TradeApplication.INDEX_TMK, slotDataTAESK11, bytesTDKPin11);

        //******************************************************************************************

//      llave pin en claro =  B4BBC1FB914C1D69D907A4B6F069B375
//      Key:			703D6EAE86355C9B17E50BE4E20B7121
//      Key length:		32

       // int slotTPK = 2;

        String llaveTPK = Device.encrypt3DesEBC(llaveTpkClaro, TradeApplication.INDEX_TDK);

        byte[] bytesTPK = TradeApplication
                .getConvert()
                .strToBcd(llaveTPK, IConvert.EPaddingPosition.PADDING_LEFT);

        // usar para capturar el pin
        Device.writeTPK2(TradeApplication.INDEX_TMK, TradeApplication.INDEX_TPK_PIN, bytesTPK);

        Device.getKCV_TPK((byte)TradeApplication.INDEX_TPK_PIN);


        //++++++++++++++++++++++++++
        //para desencriptar
        int slotTDK = 5;
        Device.writeTDK(TradeApplication.INDEX_TMK,slotTDK, bytesTPK);
        Device.getKCV_TDK((byte)slotTDK);

    }

    public static void writeWorkKeys(String data, String pin, String signature) {

        int TdkSlot = 10;
        int TpkSlot = 10;
        int TakSlot = 10;

        byte[] bytesTdkData = TradeApplication
                .getConvert()
                .strToBcd(data, IConvert.EPaddingPosition.PADDING_LEFT);

        Device.writeTDK2(TradeApplication.INDEX_TMK, TdkSlot, bytesTdkData);

        //*****************************************************************************************

        byte[] bytesTpkPin = TradeApplication
                .getConvert()
                .strToBcd(pin, IConvert.EPaddingPosition.PADDING_LEFT);

        Device.writeTPK2(TradeApplication.INDEX_TMK, TpkSlot, bytesTpkPin);

        //******************************************************************************************

        byte[] bytesTakSignature = TradeApplication
                .getConvert()
                .strToBcd(signature, IConvert.EPaddingPosition.PADDING_LEFT);

        Device.writeTAK(TradeApplication.INDEX_TMK, TakSlot, bytesTakSignature);

        //******************************************************************************************

        // usar para capturar el pin
        Device.writeTPK2(TradeApplication.INDEX_TMK, TradeApplication.INDEX_TPK_PIN, bytesTpkPin);
        Device.getKCV_TPK((byte)TradeApplication.INDEX_TPK_PIN);


        //++++++++++++++++++++++++++
        //para desencriptar
        int slotTDK = 5;
        Device.writeTDK(TradeApplication.INDEX_TMK,slotTDK, bytesTpkPin);
        Device.getKCV_TDK((byte)slotTDK);

    }

    public static String getTrack2(String track) {

        Log.i(TAG, "getTrack2: " + track);

        // debe tener un formato de 38 caracteres
        track = track.split("F")[0];
//        track = track.split("D")[0];
//        track = track.split("=")[0];
        if (track.length() > 38) {
            track = track.substring(0,37);
        }

        track = String.format("%-38s", track ).replace(' ', '0');
        return track;
    }

    public static String getCardNumber(String track) {

        // debe tener un formato de 38 caracteres
        track = track.split("D")[0];
        track = track.split("F")[0];
        track = track.split("=")[0];

        track = track.substring(0,6) + "******" + track.substring(12);

        return track;
    }

    public String pinBlockToAes(byte[] pindata) {
        String pinBlockClaro = Device.decrypt3DesECB(TradeApplication.getConvert().bcdToStr(pindata), 5);
        Log.i(TAG, "pinBlockClaro: " + pinBlockClaro);
        pinBlockClaro = pinBlockClaro +"0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D";
        Log.i(TAG, "pinBlockToAes: " + pinBlockClaro);
        return Device.encryptAES_CBC(pinBlockClaro, 11);
    }

    public static String formatAmount(double amount){
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.UK);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(true);
        return nf.format(amount);
    }

    public static void injectKeys() {

        // CLAVE A283C38D7D7366C6DEFD9B6FFBF45783
        String TLK = "F4F710AE16B5C1EF1985512616FE6432867FFD23E99408AD";
        String TMK = "811164B0BB4126C1EE75377DE9FE5F0B"; // ENCRYPT TLK  // 3DES - ECB
        String TDK = "60AB5A1E944ED2936F1954B7C34C9C5B"; // ENCRYPT TMK SLOT 2   // LLAVE EN CLARO + LA LLAVE A ENCRIPTAR // 3DES - ECB
        //String TAK =   VALUE // llave en claro + llave a encriptar // 3DES - ECB

//        String TPK = "B759ACBF8BF0F8FB8DAA489616E62F1D61149C44294CA155FD4FF005F8D68E60";

        byte[] bytesTLK = TradeApplication
                .getConvert()
                .strToBcd(TLK, IConvert.EPaddingPosition.PADDING_LEFT);

        byte[] bytesTMK = TradeApplication
                .getConvert()
                .strToBcd(TMK, IConvert.EPaddingPosition.PADDING_LEFT);

        byte[] bytesTDK = TradeApplication
                .getConvert()
                .strToBcd(TDK, IConvert.EPaddingPosition.PADDING_LEFT);

//        byte[] bytesTPK = TradeApplication
//                .getConvert()
//                .strToBcd(TPK, IConvert.EPaddingPosition.PADDING_LEFT);

        try {
            Device.writeTLK(bytesTLK);

            // KEY 1
            Device.getKCV_TLK();

            Device.writeTMK2(TradeApplication.INDEX_TMK, bytesTMK);

            // KEY 2
            Device.getKCV_TMK((byte) TradeApplication.INDEX_TMK);

            Device.writeTDK2(TradeApplication.INDEX_TMK, TradeApplication.INDEX_TDK, bytesTDK);

            // KEY 3
            Device.getKCV_TDK((byte) TradeApplication.INDEX_TDK);

        } catch (Exception e) {
            Log.e(TAG, "uploadKeys: ", e);
        }

    }

    public static boolean cleanKeys() {
        try {
            Device.cleanKeys();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "cleanKeys: ", e);
            return false;
        }
    }


    public static String keyValidateTlk(){
        String result;
        try{
            result =  TradeApplication.getConvert().bcdToStr( Device.getKCV_TLK());
        }
        catch (Exception ex){
            Log.e(TAG, "keyValidateTlk: ", ex);
            result = "vacío";
        }
        return result;
    }


    public static boolean keyValidateTlkBoolean(){
        boolean result = true;
        try{
            TradeApplication.getConvert().bcdToStr( Device.getKCV_TLK());
            result = true;
        }
        catch (Exception ex){
            Log.e(TAG, "keyValidateTlk: ", ex);
            result = false;
        }
        return result;
    }

    public static String keyValidateTmk(int indexTmk){
        String result;
        try{
            result =  TradeApplication.getConvert().bcdToStr( Device.getKCV_TMK((byte) indexTmk));
        }
        catch (Exception ex){
            Log.e(TAG, "keyValidateTmk: ", ex);
            result = "vacío";
        }
        return result;
    }

    public static String keyValidateTdk(int indexTdk){
        String result;
        try{
            result =  TradeApplication.getConvert().bcdToStr( Device.getKCV_TDK((byte) indexTdk));
        }
        catch (Exception ex){
            Log.e(TAG, "keyValidateTmk: ", ex);
            result = "vacío";
        }
        return result;
    }

    public static String keyValidateAes(int indexAES) {

        String result;

        String testValue = "12345678912345678912345678912345";

        byte [] initVector = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; //init vector of CBC

        try {
            byte [] valueByte = TradeApplication.getConvert()
                    .strToBcd(testValue, IConvert.EPaddingPosition.PADDING_LEFT);

            byte [] resultAes =  TradeApplication.getDal()
                    .getPed(EPedType.INTERNAL)
                    .calcAes(
                            (byte)indexAES,
                            initVector,
                            valueByte,
                            ECryptOperate.ENCRYPT,
                            ECryptOpt.CBC);

            result = TradeApplication.getConvert().bcdToStr(resultAes);
            return  result;
        } catch (PedDevException e) {
            result = "error en la encriptación";
            Log.w("writeTMK", e);
            return result;
        }

    }

    public static String toJson(Object value){
        Gson gson = new Gson();
        return gson.toJson(value);
    }

    public static String toJsonPretty(Object value){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(value);
    }

}
