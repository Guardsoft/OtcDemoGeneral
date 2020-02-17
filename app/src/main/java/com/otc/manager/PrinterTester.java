package com.otc.manager;

import android.graphics.Bitmap;

import com.pax.app.TradeApplication;
import com.pax.dal.IPrinter;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.pax.dal.exceptions.PrinterDevException;

public class PrinterTester {
    public static final int STATUS_OK = 0;
    public static final int STATUS_BUSY = 1;
    public static final int STATUS_OUT_OF_PAPER = 2;
    public static final int STATUS_DATA_PACKET_ERROR = 3;
    public static final int STATUS_MALFUNCTIONS = 4;
    public static final int STATUS_OVER_HEATS = 8;
    public static final int STATUS_VOLTAGE_TOO_LOW = 9;
    public static final int STATUS_UNFINISHED = 240;
    public static final int STATUS_FONT_LIB_NOT_INIT = 252;
    public static final int STATUS_DATA_PACKET_TOO_LONG = 254;

    private static PrinterTester printerTester;
    private IPrinter printer;

    public PrinterTester() {
        printer = TradeApplication.getInstance().getDal().getPrinter();
    }

    public  static PrinterTester getInstance() {
        if (printerTester == null) {
            printerTester = new PrinterTester();
        }
        return printerTester;
    }

    public void init() {
        try {
            printer.init();
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
    }

    public int getStatus() {
        try {
            int status = printer.getStatus();
            return status;
            //return statusCode2Str(status);
        } catch (PrinterDevException e) {
            e.printStackTrace();
            return STATUS_MALFUNCTIONS;
        }

    }

    public void fontSet(EFontTypeAscii asciiFontType, EFontTypeExtCode cFontType) {
        try {
            printer.fontSet(asciiFontType, cFontType);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }

    }

    public void spaceSet(byte wordSpace, byte lineSpace) {
        try {
            printer.spaceSet(wordSpace, lineSpace);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
    }

    public void printStr(String str, String charset) {
        try {
            printer.printStr(str, charset);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }

    }

    public void step(int b) {
        try {
            printer.step(b);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
    }

    public void printBitmap(Bitmap bitmap) {
        try {
            printer.printBitmap(bitmap);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
    }

    public int start() {
        try {
            int res = printer.start();
            return res;//statusCode2Str(res);
        } catch (PrinterDevException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public void leftIndents(short indent) {
        try {
            printer.leftIndent(indent);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
    }

    public int getDotLine() {
        try {
            int dotLine = printer.getDotLine();
            return dotLine;
        } catch (PrinterDevException e) {
            e.printStackTrace();
            return -2;
        }
    }

    public void setGray(int level) {
        try {
            printer.setGray(level);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }

    }

    public void setDoubleWidth(boolean isAscDouble, boolean isLocalDouble) {
        try {
            printer.doubleWidth(isAscDouble, isLocalDouble);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
    }

    public void setDoubleHeight(boolean isAscDouble, boolean isLocalDouble) {
        try {
            printer.doubleHeight(isAscDouble, isLocalDouble);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }

    }

    public void setInvert(boolean isInvert) {
        try {
            printer.invert(isInvert);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }

    }

    public String statusCode2Str(int status) {
        String res = "";
        switch (status) {
            case 0:
                res = "Success ";
                break;
            case 1:
                res = "Printer is busy ";
                break;
            case 2:
                res = "Out of paper ";
                break;
            case 3:
                res = "The format of print data packet error ";
                break;
            case 4:
                res = "Printer malfunctions ";
                break;
            case 8:
                res = "Printer over heats ";
                break;
            case 9:
                res = "Printer voltage is too low";
                break;
            case 240:
                res = "Printing is unfinished ";
                break;
            case 252:
                res = " The printer has not installed font library ";
                break;
            case 254:
                res = "Data package is too long ";
                break;
            default:
                break;
        }
        return res;
    }
}
