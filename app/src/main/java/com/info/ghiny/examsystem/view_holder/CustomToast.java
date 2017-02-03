package com.info.ghiny.examsystem.view_holder;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.manager.ConfigManager;

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

public class CustomToast {
    private Toast message;
    private Activity activity;
    //private Context context;
    private View toastView;
    private TextView msgView;
    private ImageView imgView;

    public CustomToast(Activity activity){
        this.activity = activity;
        //this.context    = context;
        LayoutInflater toastInflater = activity.getLayoutInflater();
        toastView   = toastInflater.inflate(R.layout.toast_message_layout,
                (ViewGroup) activity.findViewById(R.id.toastLayout));
        msgView     = (TextView) toastView.findViewById(R.id.toastTxt);
        msgView.setTypeface(Typeface.createFromAsset(activity.getAssets(), ConfigManager.DEFAULT_FONT));
        imgView     = (ImageView) toastView.findViewById(R.id.toastImg);
        msgView.setText("");
    }

    public void showMessage(String textMessage){
        if(message != null){
            //If there is a Toast message displaying
            message.cancel();
        }
        msgView.setText(textMessage);
        message = Toast.makeText(activity.getApplicationContext(), textMessage, Toast.LENGTH_LONG);
        message.show();
    }

    public void showMessageWithCondition(String textMessage, boolean sameMsg){
        if(message != null){
            if(!sameMsg) {
                message.cancel();
                msgView.setText(textMessage);
                message = Toast.makeText(activity.getApplicationContext(),
                            textMessage, Toast.LENGTH_LONG);
                message.show();
            }
        } else {
            msgView.setText(textMessage);
            message = Toast.makeText(activity.getApplicationContext(),
                        textMessage, Toast.LENGTH_LONG);
            message.show();
        }
    }

    public void showCustomMessage(String textMessage, int resImageId){
        if(message != null){
            //If there is a Toast message displaying
            message.cancel();
        }
        customMakeText(textMessage, resImageId);
        message.show();
    }

    public void showCustomMessageWithCondition(String textMessage, int resImageId, boolean sameMsg){
        if(message != null){
            if(!sameMsg || msgView.getText().toString().isEmpty()){
                message.cancel();
                customMakeText(textMessage, resImageId);
                message.show();
            }
        } else {
            customMakeText(textMessage, resImageId);
            message.show();
        }
    }

    public boolean checkEqualToast(String textMessage){
        boolean isTrue = false;
        if(textMessage.equals(msgView.getText().toString())){
            isTrue = true;
        }
        return isTrue;
    }

    //=============================================================================================
    private void customMakeText(String textMessage, int resImageId){
        message = new Toast(activity);
        message.setDuration(Toast.LENGTH_LONG);
        imgView.setImageResource(resImageId);
        msgView.setText(textMessage);
        message.setView(toastView);
    }
}
