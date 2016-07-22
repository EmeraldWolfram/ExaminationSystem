package com.info.ghiny.examsystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

/**
 * Created by GhinY on 22/07/2016.
 */
public class FancyErrorWindow extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_error);

        TextView errorView = (TextView)findViewById(R.id.errorText);
        errorView.setText(getIntent().getStringExtra("Error"));
        errorView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/Oswald-Bold.ttf"));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width * 0.9), (int)(height * 0.3));
    }

    public void onOkay(View view){
        finish();
    }

}
