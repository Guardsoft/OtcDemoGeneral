package com.culqi.signature;

import com.otc.ui.util.UtilOtc;
import com.pax.app.IConvert;
import com.pax.app.TradeApplication;
import com.pax.tradepaypw.device.Device;


import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class MacRetailSignature extends Signature {


    public MacRetailSignature(RequestToSign request) {
        super(request);
    }

    @Override
    public String prepareStringToSign(String canonicalURL, String xAmzDate) {
        String stringToSign = "";
        stringToSign = "MAC-RETAIL" + "\n";
        stringToSign += (xAmzDate != null ? xAmzDate : SignatureUtil.getTimeStamp()) + "\n";
        stringToSign += (xAmzDate != null && xAmzDate.length() >= 8) ? xAmzDate.substring(0, 8) : SignatureUtil.getCurrentDate() + "/" + request.getService() + "/" + "aws4_request" + "\n";
        stringToSign += SignatureUtil.toHex(SignatureUtil.hash(canonicalURL));
        return stringToSign;
    }

    @Override
    public String buildAuthorizationString(String strSignature) {
        return String.format("MAC-RETAIL Credential=%s/%s/%s/aws4_request, SignedHeaders=%s, Signature=%s",
                accessKey,
                SignatureUtil.getCurrentDate(),
                request.getService(),
                stringSignedHeader,
                strSignature);
    }

    //TODO: Reemplazar por Firma del Dispositivo
//    public byte[] macRetail(byte[] macKey, String data) throws Exception {
//        final BlockCipher cipher = new DESEngine();
//        final KeyParameter key = createKey(macKey);
//        final byte[] dataToMac = data.getBytes("UTF-8");
//        return generateIso9797Alg3Mac(key, cipher, dataToMac);
//    }

//    private byte[] generateIso9797Alg3Mac(KeyParameter key, BlockCipher cipher, byte[] data) {
//        final Mac mac = new ISO9797Alg3Mac(cipher);
//        mac.init(key);
//        mac.update(data, 0, data.length);
//        final byte[] out = new byte[8];
//        mac.doFinal(out, 0);
//        return out;
//    }

//    private static KeyParameter createKey(byte[] key) {
//        if (key.length != 16) {
//            throw new RuntimeException("Unsupported key len " + key.length + " B for ISO9797Alg3Mac");
//        }
//        return new KeyParameter(key);
//    }

    public String macRetail(String stringToSign) {

        int indexKeyTak = 10;

        String temp = "";
        try {

            temp = toHex(stringToSign.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] signature = TradeApplication.getConvert().strToBcd(temp, IConvert.EPaddingPosition.PADDING_LEFT);




        return TradeApplication.getConvert().bcdToStr( Device.getMacRetail(indexKeyTak, signature) );
    }

    @Override
    public String toString() {
        String xAmzDate = SignatureUtil.getTimeStamp();
        String canonicalURL = "";
        try {
            /* Task 1 - Create a Canonical Request */
            canonicalURL = prepareCanonicalRequest(xAmzDate);
        }catch(UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }

        /* Task 2 - Create a String to Sign */
        return prepareStringToSign(canonicalURL, xAmzDate);
    }

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }


}
