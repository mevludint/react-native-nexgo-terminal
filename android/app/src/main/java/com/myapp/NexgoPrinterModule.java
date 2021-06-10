package com.myapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.nexgo.oaf.apiv3.APIProxy;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.DeviceInfo;
import com.nexgo.oaf.apiv3.SdkResult;
import com.nexgo.oaf.apiv3.device.printer.AlignEnum;
import com.nexgo.oaf.apiv3.device.printer.DotMatrixFontEnum;
import com.nexgo.oaf.apiv3.device.printer.FontEntity;
import com.nexgo.oaf.apiv3.device.printer.GrayLevelEnum;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;
import com.nexgo.oaf.apiv3.device.printer.Printer;


public class NexgoPrinterModule extends ReactContextBaseJavaModule {
    private final FontEntity fontSmall = new FontEntity(DotMatrixFontEnum.CH_SONG_20X20, DotMatrixFontEnum.ASC_SONG_8X16);
    private final FontEntity fontNormal = new FontEntity(DotMatrixFontEnum.CH_SONG_24X24, DotMatrixFontEnum.ASC_SONG_12X24);
    private final FontEntity fontBold = new FontEntity(DotMatrixFontEnum.CH_SONG_24X24, DotMatrixFontEnum.ASC_SONG_BOLD_16X24);
    private final FontEntity fontBig = new FontEntity(DotMatrixFontEnum.CH_SONG_24X24, DotMatrixFontEnum.ASC_SONG_12X24, false, true);

    private DeviceEngine deviceEngine;
    private Printer printer;
    private DeviceInfo deviceInfo;
    private AlignEnum align;

    NexgoPrinterModule(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "NexgoPrinterModule";
    }

    public Enum<AlignEnum> getAlignEnum(String alignItemStr) {
        switch (alignItemStr) {
            case "RIGHT":
                return AlignEnum.RIGHT;
            case "CENTER":
                return AlignEnum.CENTER;
            default:
                return AlignEnum.LEFT;
        }
    }

    public Enum<GrayLevelEnum> getGreyLevelEnum(int level) {
        switch (level) {
            case 1:
                return GrayLevelEnum.LEVEL_1;
            case 2:
                return GrayLevelEnum.LEVEL_2;
            case 3:
                return GrayLevelEnum.LEVEL_3;
            case 4:
                return GrayLevelEnum.LEVEL_4;
            default:
                return GrayLevelEnum.LEVEL_0;
        }
    }


    public FontEntity getFont(String font) {
        switch (font) {
            case "SMALL":
                return fontSmall;
            case "BOLD":
                return fontBold;
            case "BIG":
                return fontBig;
            default:
                return fontNormal;
        }
    }


    // INIT PRINTER
    @ReactMethod
    public void init(Promise promise) {
        try {
            DeviceEngine deviceEngine = APIProxy.getDeviceEngine(this.getReactApplicationContext());
            deviceInfo = deviceEngine.getDeviceInfo();
            printer = deviceEngine.getPrinter();
            promise.resolve(null);
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("FAILED TO INITIALIZE", e.getMessage());
        }
    }


    //PRINTER METHODS
    @ReactMethod
    public void appendPrnStr(String text, String alignItem, String font, Promise promise) {
        try {
            align = (AlignEnum) getAlignEnum(alignItem);
            FontEntity fontEnt = getFont(font);
            printer.appendPrnStr(text, fontEnt, align);
            promise.resolve("Success appended string");
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void appendImg(String base64Str, String alignItem, int imgWidth, int imgHeight, Promise promise) {
        try {

            align = (AlignEnum) getAlignEnum(alignItem);
            byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap scaled = Bitmap.createScaledBitmap(decodedByte, imgWidth, imgHeight, true);
            printer.appendImage(scaled, align);
            promise.resolve("Success appended img");
        } catch (Exception e) {
            promise.reject("ERROR: ", e.getMessage());

        }

    }

    @ReactMethod
    public void setLetterSpacing(int spacing) {
        printer.setLetterSpacing(spacing);
    }

    @ReactMethod
    public void startPrint(Boolean cutPaper, Promise promise) {
        try {
            printer.startPrint(true, new OnPrintListener() {
                @Override
                public void onPrintResult(final int retCode) {
                    Log.d("PRINT", "PRINT FINISHED WITH CODE: " + retCode);
                    if (cutPaper) printer.cutPaper();
                    switch (retCode) {
                        case SdkResult.Success:
                            promise.resolve("Success");
                            break;
                        default:
                            promise.reject("Something went wrong. ERROR CODE: " + retCode);
                    }
                }
            });
        } catch (Exception e) {
            promise.reject("PRINT ERRROR", e.getMessage());
        }
    }

    @ReactMethod
    public void getPrinterStatus(Promise promise) {
        try {
            int printerStatus = printer.getStatus();
            promise.resolve(printerStatus);
        } catch (Exception e) {
            promise.reject("ERROR: " + e.getMessage());
        }
    }


    @ReactMethod
    public void cutPaper(Promise promise) {
        try {
            printer.cutPaper();
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject("ERROR: " + e.getMessage());
        }
    }

    @ReactMethod
    public void setGrayLevel(int level, Promise promise) {
        try {
            GrayLevelEnum grayLevel = (GrayLevelEnum) getGreyLevelEnum(level);
            printer.setGray(grayLevel);
            promise.resolve("Grey level settled");
        } catch (Exception e) {
            promise.reject("ERROR: " + e.getMessage());
        }
    }


    // DEVICE METHODS
    @ReactMethod
    public void getSerialNumber(Promise promise) {
        try {
            String serialNumber = deviceInfo.getSn();
            promise.resolve(serialNumber);
        } catch (Exception e) {
            promise.reject("ERROR: " + e.getMessage());
        }
    }

}
