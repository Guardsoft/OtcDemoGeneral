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

import com.otc.model.response.InitializeResponse;
import com.pax.app.IConvert;
import com.pax.app.TradeApplication;
import com.pax.tradepaypw.device.Device;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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


    public void dialogResult(Context context, String msg){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("MENSAJE")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
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

        return serialNumber;
    }

    public static void writeKeysDataPin(String data, String pin) {

        String decryptInit = Device.decrypt3DesCBC(data, 1);
        String llaveReencriptada = Device.encrypt3DesEBC(decryptInit, 1);

        int slotTMK = 2;
        int slotDataTAESK10 = 10;

        byte[] bytesTDKData10 = TradeApplication
                .getConvert()
                .strToBcd(llaveReencriptada, IConvert.EPaddingPosition.PADDING_LEFT);

//        Device.writeTAESK2(slotTMK,slotDataTAESK10, bytesTDKData10);
        Device.writeTAESK3(slotDataTAESK10, bytesTDKData10);
        //*****************************************************************************************

        String decryptInitPin = Device.decrypt3DesCBC(pin, 1);

        String llaveTpkClaro = decryptInit.substring(0,32);

        String llaveReencriptadaPin = Device.encrypt3DesEBC(decryptInitPin, 1);

        int slotDataTAESK11 = 11;

        byte[] bytesTDKPin11 = TradeApplication
                .getConvert()
                .strToBcd(llaveReencriptadaPin, IConvert.EPaddingPosition.PADDING_LEFT);

        Device.writeTAESK2(slotTMK,slotDataTAESK11, bytesTDKPin11);

        //******************************************************************************************

//      llave pin en claro =  B4BBC1FB914C1D69D907A4B6F069B375
//        Key:			703D6EAE86355C9B17E50BE4E20B7121
//        Key length:		32

        int slotTPK = 2;

        String llaveTPK = Device.encrypt3DesEBC(llaveTpkClaro, 1);

        byte[] bytesTPK = TradeApplication
                .getConvert()
                .strToBcd(llaveTPK, IConvert.EPaddingPosition.PADDING_LEFT);

        // usar para capturar el pin
        Device.writeTPK2(slotTMK,slotTPK, bytesTPK);

        Device.getKCV_TPK((byte)slotTPK);


        //**************************************************************************


        int slotTDK = 5;
        Device.writeTDK(slotTMK,slotTDK, bytesTPK);
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
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(amount);
    }

    public static void injectKeys() {

        byte slot1 = (byte) 1;
        String TLK = "F4F710AE16B5C1EF1985512616FE6432867FFD23E99408AD";

        String TMK_OTC = "811164B0BB4126C1EE75377DE9FE5F0B"; // ENCRYPT TLK
        String TDK = "60AB5A1E944ED2936F1954B7C34C9C5B"; // ENCRYPT TMK SLOT 2
//        String TPK = "B759ACBF8BF0F8FB8DAA489616E62F1D61149C44294CA155FD4FF005F8D68E60";

        byte[] bytesTLK2 = TradeApplication
                .getConvert()
                .strToBcd(TLK, IConvert.EPaddingPosition.PADDING_LEFT);


        byte[] bytesTMK2 = TradeApplication
                .getConvert()
                .strToBcd(TMK_OTC, IConvert.EPaddingPosition.PADDING_LEFT);

        byte[] bytesTDK = TradeApplication
                .getConvert()
                .strToBcd(TDK, IConvert.EPaddingPosition.PADDING_LEFT);

//        byte[] bytesTPK = TradeApplication
//                .getConvert()
//                .strToBcd(TPK, IConvert.EPaddingPosition.PADDING_LEFT);


        try {


            Device.writeTLK(bytesTLK2);

            // KEY 1
            Device.getKCV_TLK();

            //encrypt con ECB
            //SLOT2 tmk_otc
            int slotTMK = 2;
            int slotTDK = 1;
//            int slotTPK = 1;
            Device.writeTMK2(slotTMK, bytesTMK2);

            // KEY 2
            Device.getKCV_TMK((byte) slotTMK);

            Device.writeTDK2(slotTMK,slotTDK, bytesTDK);

            // KEY 3
            Device.getKCV_TDK2((byte) slotTDK);
//

        } catch (Exception e) {
            Log.e(TAG, "uploadKeys: ", e);
        }

    }

    public static boolean validateKeys() {
        try {
            // KEY 1
            Device.getKCV_TLK();
            int slotTMK = 2;
            Device.getKCV_TMK((byte) slotTMK);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "uploadKeys: ", e);
            return false;
        }
    }


}
