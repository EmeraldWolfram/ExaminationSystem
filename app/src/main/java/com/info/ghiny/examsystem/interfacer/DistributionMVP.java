package com.info.ghiny.examsystem.interfacer;

import android.graphics.Bitmap;

import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by FOONG on 6/12/2016.
 */

public interface DistributionMVP {
    interface MvpView extends GeneralView {
        void setImageQr(Bitmap bitmap);
    }
    interface MvpVPresenter{
        void onCreate();
    }
    interface MvpMPresenter{}
    interface MvpModel{
        Bitmap encodeQr() throws ProcessException;
    }
}
