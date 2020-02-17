package com.otc.util;

import android.util.Log;

import com.otc.util.ConstantsOtc;
import com.pax.app.IConvert;
import com.pax.app.TradeApplication;
import com.pax.dal.IPed;
import com.pax.dal.entity.DUKPTResult;
import com.pax.dal.entity.EBeepMode;
import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.ECryptOperate;
import com.pax.dal.entity.ECryptOpt;
import com.pax.dal.entity.EDUKPTPinMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.EPinBlockMode;
import com.pax.dal.exceptions.PedDevException;
import com.pax.tradepaypw.pay.Constants;

public class DeviceOtc {
    private static final String TAG = "DeviceOtc";

    private DeviceOtc() {

    }

    /**
     * beep 成功
     */
    public static void beepOk() {
        TradeApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_3, 100);
        TradeApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_4, 100);
        TradeApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_5, 100);
    }

    /**
     * beep 失败
     */
    public static void beepErr() {
        TradeApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_6, 200);
    }

    /**
     * beep 提示音
     */

    public static void beepPromt() {
        TradeApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_6, 50);
    }

    /**
     * write TMK
     *
     * @param tmkIndex
     * @param tmkValue
     * @return
     */
    public static boolean writeTMK(byte[] tmkValue) {
        // write TMK
        try {
            TradeApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TLK, (byte) 0,
                    EPedKeyType.TMK, Constants.INDEX_TMK,
                    tmkValue, ECheckMode.KCV_NONE, null);
            return true;
        } catch (PedDevException e) {
            Log.w("writeTMK", e);
        }
        return false;
    }

    public static boolean writeTPK(byte[] tpkValue, byte[] tpkKcv) {
        try {
            //int mKeyIndex = Utils.getMainKeyIndex(FinancialApplication.getSysParam().get(SysParam.NumberParam.MK_INDEX));
            ECheckMode checkMode = ECheckMode.KCV_ENCRYPT_0;
            if (tpkKcv == null || tpkKcv.length == 0) {
                checkMode = ECheckMode.KCV_NONE;
            }
            TradeApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TMK, Constants.INDEX_TMK,
                    EPedKeyType.TPK, Constants.INDEX_TPK, tpkValue, checkMode, tpkKcv);
            return true;
        } catch (PedDevException e) {
            Log.w("writeTPK", e);
        }
        return false;
    }

    public static boolean writeTIKFuc(byte[] keyValue, byte[] ksn) {
        // write TIK
        try {
            TradeApplication.getDal().getPed(EPedType.INTERNAL).writeTIK(Constants.INDEX_TIK, (byte) 0,
                    keyValue, ksn, ECheckMode.KCV_NONE, null);
            return true;
        } catch (PedDevException e) {
            Log.w("writeTIKFuc", e);
        }
        return false;
    }


    /**
     * 计算pinblock(包括国密)
     *
     * @param panBlock
     * @return
     * @throws PedDevException
     */
    public static byte[] getPinBlockOtc(String panBlock) throws PedDevException {
        IPed ped = TradeApplication.getDal().getPed(EPedType.INTERNAL);
        return ped.getPinBlock(ConstantsOtc.INDEX_TPK, "0,4,5,6,7,8,9,10,11,12", panBlock.getBytes(),
                EPinBlockMode.ISO9564_0, 60 * 1000);
    }

    /**
     *
     * @param panBlock el valor ingresado
     * @param indexTPK la llave para desencriptar pan
     * @return
     * @throws PedDevException
     */
    public static byte[] getPinBlockOtc(String panBlock, byte indexTPK) throws PedDevException {
        IPed ped = TradeApplication.getDal().getPed(EPedType.INTERNAL);
        return ped.getPinBlock(indexTPK, "0,4,5,6,7,8,9,10,11,12", panBlock.getBytes(),
                EPinBlockMode.ISO9564_0, 60 * 1000);
    }

    public static DUKPTResult getDUKPTPin(String panBlock) throws PedDevException {
        IPed ped = TradeApplication.getDal().getPed(EPedType.INTERNAL);

        return ped.getDUKPTPin(Constants.INDEX_TIK, "0,4,5,6,7,8,9,10,11,12", panBlock.getBytes(),
                EDUKPTPinMode.ISO9564_0_INC, 60 * 1000);
    }

    public static String decrypt3DesECB(String value, int indexTDK) {
        byte [] initVector = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; //init vector of CBC


        int DECRYPT_ECB = 0;
        int ENCRYPT_ECB = 1;
        int DECRYPT_CBC = 2;
        int ENCRYPT_CBC = 3;

        try {
            byte [] valueByte = TradeApplication.getConvert()
                    .strToBcd(value, IConvert.EPaddingPosition.PADDING_LEFT);

            byte [] result =  TradeApplication.getDal().getPed(EPedType.INTERNAL)
                    .calcDes(
                            (byte)indexTDK,
                            null,
                            valueByte,
                            (byte)DECRYPT_ECB);

            return TradeApplication.getConvert().bcdToStr(result);
        } catch (PedDevException e) {
            Log.w("writeTMK", e);
        }
        return null;
    }

    public static String encryptAES_CBC(String value, int indexTAES) {
        byte [] initVector = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; //init vector of CBC

        try {
            byte [] valueByte = TradeApplication.getConvert()
                    .strToBcd(value, IConvert.EPaddingPosition.PADDING_LEFT);

            byte [] result =  TradeApplication.getDal()
                    .getPed(EPedType.INTERNAL)
                    .calcAes(
                            (byte)indexTAES,
                            initVector,
                            valueByte,
                            ECryptOperate.ENCRYPT,
                            ECryptOpt.CBC);

            return TradeApplication.getConvert().bcdToStr(result);
        } catch (PedDevException e) {
            Log.w("writeTMK", e);
        }
        return null;
    }

}
