package com.info.ghiny.examsystem.model;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.ThreadManager;
import com.info.ghiny.examsystem.interfacer.DistributionMVP;
import com.info.ghiny.examsystem.manager.IconManager;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class DistributionModel implements DistributionMVP.MvpModel{

    private DistributionMVP.MvpMPresenter taskPresenter;
    private StaffIdentity user;

    public DistributionModel(DistributionMVP.MvpMPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.user           = LoginModel.getStaff();
    }

    @Override
    public Bitmap encodeQr(int localPort) throws ProcessException {

        String ip           = ThreadManager.getThisIpv4();
        Connector connector = new Connector(ip, localPort, JavaHost.getConnector().getDuelMessage());
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(connector.toString(), BarcodeFormat.QR_CODE,
                    400, 400, null);
        } catch (Exception err) {
            throw new ProcessException("QR Encode Failed\nPlease consult developer!",
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

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!user.matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }



}
