package com.info.ghiny.examsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class MainLoginActivity extends AppCompatActivity {
    private static final String TAG = MainLoginActivity.class.getSimpleName();
    private CompoundBarcodeView barcodeView;
    private Identity examinator = new Identity();   //Don't create the object

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
                //checkEligibilityOfTheIdendity
                //Create the object here by a getIdentity method from Database
                examinator.setIdentity(result.getText());
            }
            //DO SOMETHING WITH RESULT
            //ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            //imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        barcodeView = (CompoundBarcodeView) findViewById(R.id.loginScanner);
        barcodeView.decodeContinuous(callback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }


    public void onLogin(View view){
        AlertDialog.Builder dialogMsg = new AlertDialog.Builder(this);
        EditText password = (EditText)findViewById(R.id.inputPassword);
        examinator.setPassword(password.getText().toString());

        if(examinator.getIdentity() == null)
            dialogMsg.setMessage("Please scan your identity card!");
        else if(examinator.getPassword().isEmpty())
            dialogMsg.setMessage("Please enter password to proceed");
        else {
            //TO DO,
            // 1. change the compare equal password to matchPassword(examinator)
            // 2. Add check for id also
            if (examinator.getPassword().equals("63686689")) {
                Intent homeIntent = new Intent(this, HomeOptionActivity.class);
                startActivity(homeIntent);
                dialogMsg.setMessage("Thank you!");
            }
            else
                dialogMsg.setMessage("The input password was wrong!");
        }

        dialogMsg.setCancelable(true);
        dialogMsg.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {dialog.cancel();
                    }
                });
        AlertDialog alert = dialogMsg.create();
        alert.show();
    }
}
