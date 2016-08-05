package com.info.ghiny.examsystem;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.info.ghiny.examsystem.tools.CustomToast;
import com.info.ghiny.examsystem.tools.LoginHelper;

/**
 * Created by GhinY on 21/05/2016.
 */
public class PopUpLogin extends Activity {

    public static final int PASSWORD_REQ_CODE = 888;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_window);

        TextView nameView = (TextView)findViewById(R.id.popUpExaminerName);
        TextView regNView = (TextView)findViewById(R.id.popUpExaminerRegNum);
        TextView entView  = (TextView)findViewById(R.id.enterPasswordText);
        nameView.setText(LoginHelper.getStaff().getName());
        nameView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/Oswald-Bold.ttf"));
        regNView.setText(LoginHelper.getStaff().getIdNo());
        regNView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/Oswald-Bold.ttf"));
        entView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/DroidSerif-Regular.ttf"));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width * 0.9), (int)(height * 0.3));
    }

    public void onLogin(View view){
        EditText inputPW    = (EditText)findViewById(R.id.popUpExaminerPassword);
        getIntent().putExtra("Password", inputPW.getText().toString());

        this.setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public void onBackPressed() {
        CustomToast toast   = new CustomToast(this);
        toast.showCustomMessage("Please enter a password to proceed", R.drawable.warn_icon);
    }
}
