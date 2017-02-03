package com.info.ghiny.examsystem.model;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.manager.ErrorManager;

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

public class ConnectionTask extends AsyncTask<String, String, JavaHost> {

    private static boolean completeFlag = false;
    private ErrorManager errorManager;
    private ProcessException err;

    //= Setter & Getter ============================================================================
    public static boolean isComplete() {
        return completeFlag;
    }
    public static void setCompleteFlag(boolean completeFlag) {
        ConnectionTask.completeFlag = completeFlag;
    }

    //= Public Methods =============================================================================
    public void publishError(ErrorManager errManager, ProcessException err){
        this.errorManager   = errManager;
        this.err            = err;
        publishProgress("Error");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected JavaHost doInBackground(String... params) {

        JavaHost javaHost = new JavaHost(new JavaHost.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {}
        });
        ExternalDbLoader.setJavaHost(javaHost);
        javaHost.run();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values){
        super.onProgressUpdate(values);
        errorManager.displayError(err);
    }
}
