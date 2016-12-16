package com.info.ghiny.examsystem.interfacer;

import android.content.Context;
import android.graphics.Bitmap;

import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by FOONG on 6/12/2016.
 */

public interface DistributionMVP {
    interface MvpView extends GeneralView {
        void setImageQr(Bitmap bitmap);
    }
    interface MvpVPresenter extends TaskSecurePresenter{
        void onCreate(Context context);
    }
    interface MvpMPresenter{
        Connector getMyConnector();

    }
    interface MvpModel extends TaskSecureModel{

        Bitmap encodeQr() throws ProcessException;
    }
}
