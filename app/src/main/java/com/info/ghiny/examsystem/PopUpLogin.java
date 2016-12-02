package com.info.ghiny.examsystem;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.info.ghiny.examsystem.manager.ConfigManager;
import com.info.ghiny.examsystem.model.CustomToast;
import com.info.ghiny.examsystem.model.LoginModel;

/**
 * Created by GhinY on 21/05/2016.
 */
public class PopUpLogin extends Activity {

    public static final int PASSWORD_REQ_CODE = 888;
    private boolean cancellable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_password_prompt);

        TextView nameView = (TextView)findViewById(R.id.popUpExaminerName);
        TextView regNView = (TextView)findViewById(R.id.popUpExaminerRegNum);
        TextView entView  = (TextView)findViewById(R.id.enterPasswordText);

        if(LoginModel.getStaff() != null){
            nameView.setText(LoginModel.getStaff().getName());
            nameView.setTypeface(Typeface.createFromAsset(this.getAssets(), ConfigManager.THICK_FONT));

            regNView.setText(LoginModel.getStaff().getIdNo());
            regNView.setTypeface(Typeface.createFromAsset(this.getAssets(), ConfigManager.THICK_FONT));
        }

        entView.setTypeface(Typeface.createFromAsset(this.getAssets(), ConfigManager.DEFAULT_FONT));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width * 0.9), (int)(height * 0.3));

        cancellable = getIntent().getBooleanExtra("Cancellable", false);
    }

    public void onLogin(View view){
        EditText inputPW    = (EditText)findViewById(R.id.popUpExaminerPassword);
        getIntent().putExtra("Password", inputPW.getText().toString());

        this.setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public void onBackPressed() {
        if(cancellable){
            this.setResult(RESULT_CANCELED, getIntent());
            finish();
        } else {
            CustomToast toast   = new CustomToast(this);
            toast.showCustomMessage("Please enter a password to proceed", R.drawable.other_warn_icon);
        }
    }
}
