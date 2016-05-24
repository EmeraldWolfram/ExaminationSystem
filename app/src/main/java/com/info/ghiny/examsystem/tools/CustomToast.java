package com.info.ghiny.examsystem.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by GhinY on 23/05/2016.
 */
public class CustomToast {
    Toast message;
    Context context;

    public CustomToast(Context context){
        this.context = context;
    }

    public void showMessage(String textMessage){
        if(message != null){
            //If there is a Toast message displaying
            message.cancel();
        }
        message = Toast.makeText(context, textMessage, Toast.LENGTH_LONG);
        message.show();
    }

}
