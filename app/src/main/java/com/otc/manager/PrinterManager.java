package com.otc.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.otc.model.response.authorize.AuthorizeResponse;
import com.otc.ui.util.BitmapUtils;
import com.otc.ui.util.UtilOtc;
import com.pax.app.TradeApplication;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.pax.jemv.demo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class PrinterManager {
    private static final int PRINT_COMPLETED = 9;

    public PrinterManager() {
        super();
        if (!TradeApplication.getInstance().isDalEnabled()) {
            return;
        }
        PrinterTester.getInstance().init();
    }

    public void print(Context context) {


        final Bitmap  logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_restaurant);

        String invoiceRow0 = "Producto            Cant  Precio";
        String invoiceRowd = "--------------------------------";
        String invoiceRow1 = "Pantalon Negr        2    $32.00";
        String invoiceRow2 = "Short Azul           2    $48.00";
        String invoiceRow3 = "Polos RipCur        10    $15.00";
        String invoiceRow6 = "DESCUENTO                 $ 0,00";
        String invoiceRow7 = "IVA                       $ 0,00";
        String invoiceRow8 = "Total a pagar             $85,00";

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer
                .append("\n\n")
                .append(invoiceRow0).append("\n")
                .append(invoiceRowd).append("\n")
                .append(invoiceRow1).append("\n")
                .append(invoiceRow2).append("\n")
                .append(invoiceRow3).append("\n")
                .append(invoiceRowd).append("\n")
                .append(invoiceRow6).append("\n")
                .append(invoiceRow7).append("\n")
                .append(invoiceRowd).append("\n")
                .append(invoiceRow8).append("\n");

        final String products = stringBuffer.toString();

        new Thread(() -> {
            PrinterTester print = PrinterTester.getInstance();
            print.init();
            print.printBitmap(BitmapUtils.getPrinableBitmap(logo));
            print.setGray(1);
            print.setGray(1);
            print.fontSet(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_16_16);
            print.printStr(products, "UTF-8");
            //if (transaction.getTiendaList().size() > 0) {
            //print.fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_16);
            //}
            print.printStr(products, "UTF-8");
            print.step(150);
            int status = PrinterTester.getInstance().start();
        }).start();
    }

    public void printDemo(Context context, AuthorizeResponse authorizeResponse, String TENANT, String track2) {

        final Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_culqi_black);

        final Bitmap  logo1 = BitmapUtils.getWhiteBgBitmapScaleTo(logo, 500);

        Date timestamp = new Date(authorizeResponse.getHeader().getTransactionDate());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String contable = authorizeResponse.getOrder().isCountable() == true ? "TRUE": " FALSE";
        String card = authorizeResponse.getCustomFields().getCard() != null ? authorizeResponse.getCustomFields().getCard() : "";
        String moneda = authorizeResponse.getOrder().getCurrency()!= null ? authorizeResponse.getOrder().getCurrency() : "";
        String terminal = authorizeResponse.getCustomFields().getTerminal()!= null ? authorizeResponse.getCustomFields().getTerminal() : "";
        String tipoCaptura = authorizeResponse.getDevice().getCaptureType()!= null ? authorizeResponse.getDevice().getCaptureType() : "";
        String purchaseNumbre = authorizeResponse.getOrder().getPurchaseNumber()!= null ? authorizeResponse.getOrder().getPurchaseNumber() : "";
        String description = authorizeResponse.getOrder().getActionDescription()!= null ? authorizeResponse.getOrder().getActionDescription() : "";
        String estado = authorizeResponse.getOrder().getStatus()!= null ? authorizeResponse.getOrder().getStatus() : "";
        double importe = authorizeResponse.getOrder().getAmount();


        if (moneda.equals("PEN")) {
            moneda = "SOLES";
        }

        String invoiceRow0 = "Transacción exitosa";
        String invoiceRowd = "--------------------------------";
        String invoiceRow1 = "Fecha y Hora {data1}".replace("{data1}", df.format(timestamp));
        String invoiceRow3 = "Tarjeta No. {data3}".replace("{data3}", UtilOtc.getCardNumber(track2));
//        String invoiceRow4 = "Nombre {data4}".replace("{data4}", data4);
        String invoiceRow5 = "--------------------------------";
        String invoiceRow6 = "Moneda {data5}".replace("{data5}", moneda);
        String invoiceRow61 = "Importe {data}".replace("{data}", UtilOtc.formatAmount(importe));
        String invoiceRow7 = "N° Serial {data6}".replace("{data6}", UtilOtc.getSerialNumber());
        String invoiceRow8 = "Tipo Captura {data7}".replace("{data7}", tipoCaptura);
        String invoiceRow9 = "Numero de pedido {data8}".replace("{data8}", purchaseNumbre);
        String invoiceRow10 = "Descripción {data9}".replace("{data9}", description);
        String invoiceRow11 = "Estado {data10}".replace("{data10}", estado);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer
                .append("\n\n")
                .append(invoiceRow0).append("\n")
                .append(invoiceRowd).append("\n")
                .append(invoiceRow1).append("\n")
                .append(invoiceRow3).append("\n")
                .append(invoiceRow5).append("\n")
                .append(invoiceRow6).append("\n")
                .append(invoiceRow61).append("\n")
                .append(invoiceRow7).append("\n")
                .append(invoiceRow8).append("\n")
                .append(invoiceRow9).append("\n")
                .append(invoiceRow10).append("\n")
                .append(invoiceRow11).append("\n")
                .append(invoiceRowd).append("\n\n");

        final String products = stringBuffer.toString();

        new Thread(() -> {
            PrinterTester print = PrinterTester.getInstance();
            print.init();
            print.printBitmap(logo1);
            print.setGray(10);
//            print.setInvert(true);
            print.fontSet(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_16_16);
            print.printStr(products, "UTF-8");
            print.step(150);
            int status = PrinterTester.getInstance().start();
        }).start();
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PRINT_COMPLETED:
//                    EventBus.getDefault().post(msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}

