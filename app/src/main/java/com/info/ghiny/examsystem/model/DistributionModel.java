package com.info.ghiny.examsystem.model;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.interfacer.DistributionMVP;
import com.info.ghiny.examsystem.manager.IconManager;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by FOONG on 6/12/2016.
 */

public class DistributionModel implements DistributionMVP.MvpModel{

    private DistributionMVP.MvpMPresenter taskPresenter;

    public DistributionModel(DistributionMVP.MvpMPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
    }

    @Override
    public Bitmap encodeQr() throws ProcessException {

        Connector connector = new Connector("192.168.0.1", 5457, "DUEL");
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(connector.toString(),
                    BarcodeFormat.QR_CODE, 400, 400, null);
        } catch (Exception err) {
            throw new ProcessException("QR Encrytion failed\nPlease consult developer!",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
