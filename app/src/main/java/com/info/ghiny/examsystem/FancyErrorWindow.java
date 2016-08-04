package com.info.ghiny.examsystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by GhinY on 22/07/2016.
 */
public class FancyErrorWindow extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_error);

        Intent intent   = getIntent();

        TextView errorView = (TextView)findViewById(R.id.errorText);
        ImageView errIcon  = (ImageView)findViewById(R.id.errorIcon);
        errorView.setText(intent.getStringExtra("ErrorTxt"));
        errorView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/Oswald-Bold.ttf"));
        errIcon.setImageResource(intent.getIntExtra("ErrorIcon", R.drawable.warn_icon));

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
