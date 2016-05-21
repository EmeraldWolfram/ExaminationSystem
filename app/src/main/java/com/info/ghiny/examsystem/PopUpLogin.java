package com.info.ghiny.examsystem;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by GhinY on 21/05/2016.
 */
public class PopUpLogin extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_window);

        String name     = getIntent().getStringExtra("Name");
        String regNum   = getIntent().getStringExtra("RegNum");

        TextView nameView = (TextView)findViewById(R.id.popUpExaminerName);
        TextView regNView = (TextView)findViewById(R.id.popUpExaminerRegNum);
        nameView.setText(name);
        regNView.setText(regNum);

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
}
