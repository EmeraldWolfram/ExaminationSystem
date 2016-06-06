package com.info.ghiny.examsystem.tools;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.test.ActivityTestCase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.info.ghiny.examsystem.R;

/**
 * Created by GhinY on 23/05/2016.
 */
public class CustomToast {
    Toast message;
    Activity activity;

    /*public CustomToast(Activity activity){
        this.activity = activity;
    }*/

    public CustomToast(Activity activity){
        this.activity = activity;

    }

    public void showMessage(String textMessage){
        if(message != null){
            //If there is a Toast message displaying
            message.cancel();
        }
        message = new Toast(activity);
        message.setDuration(Toast.LENGTH_LONG);
        LayoutInflater toastInflater = activity.getLayoutInflater();
        View toastView      = toastInflater.inflate(R.layout.toast_message_layout,
                                (ViewGroup) activity.findViewById(R.id.toastLayout));
        TextView msgView    = (TextView) toastView.findViewById(R.id.toastTxt);
        msgView.setText(textMessage);
        message.setView(toastView);
        message.show();
    }

    public void showMessageWithCondition(String textMessage, boolean condition){
        if(condition){

        }
    }

    public void showMessageWithImage(String textMessage, int resImageId){
        if(message != null){
            //If there is a Toast message displaying
            message.cancel();
        }
        message = new Toast(activity);
        message.setDuration(Toast.LENGTH_LONG);
        LayoutInflater toastInflater = activity.getLayoutInflater();
        View toastView      = toastInflater.inflate(R.layout.toast_message_layout,
                (ViewGroup) activity.findViewById(R.id.toastLayout));
        TextView msgView    = (TextView) toastView.findViewById(R.id.toastTxt);
        ImageView imgView   = (ImageView) toastView.findViewById(R.id.toastImg);
        imgView.setImageResource(resImageId);
        msgView.setText(textMessage);
        message.setView(toastView);
        message.show();
    }

}
