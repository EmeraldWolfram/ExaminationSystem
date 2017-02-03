package com.info.ghiny.examsystem.interfacer;

import android.content.Context;
import android.graphics.Bitmap;

import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.model.AndroidClient;
import com.info.ghiny.examsystem.model.ProcessException;

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

public interface DistributionMVP {
    interface MvpView extends GeneralView {
        void setImageQr(Bitmap bitmap);
    }

    interface MvpVPresenter extends TaskSecurePresenter{
        void onCreate(Context context);
        void onDestroy();
    }

    interface MvpMPresenter{}

    interface MvpModel extends TaskSecureModel{
        Bitmap encodeQr(int localPort) throws ProcessException;
    }
}
