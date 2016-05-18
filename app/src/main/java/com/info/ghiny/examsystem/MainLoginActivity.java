package com.info.ghiny.examsystem;

import android.content.DialogInterface;
import android.content.Intent;
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
    private Identity examinator;
    private AlertDialog.Builder dialogMsg;
    private AlertDialog alert;

    private ExamDatabaseHelper databaseHelper;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                examinator = databaseHelper.getIdentity(result.getText());
                barcodeView.setStatusText(examinator.getName() + "\n" + examinator.getRegNum());
                checkEligibilityOfTheIdendity(examinator);
                //Create the object here by a getIdentity method from Database
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

        databaseHelper = new ExamDatabaseHelper(this);

        dialogMsg = new AlertDialog.Builder(this);
        dialogMsg.setCancelable(true);
        dialogMsg.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                barcodeView.decodeSingle(callback);
                dialog.cancel();
            }
        });

        barcodeView = (CompoundBarcodeView) findViewById(R.id.loginScanner);
        barcodeView.decodeSingle(callback);
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

    private void checkEligibilityOfTheIdendity(Identity identity){
        if(!identity.getEligible()) {
            dialogMsg.setMessage("Unauthorized examiner detected!");
            alert = dialogMsg.create();
            alert.show();
        }
    }

    public void onLogin(View view){
        EditText password = (EditText)findViewById(R.id.inputPassword);


        if(password.getText().toString().isEmpty())
            dialogMsg.setMessage("Please enter password to proceed");
        else {
            if (examinator.matchPassword(password.getText().toString())) {
                Intent homeIntent = new Intent(this, HomeOptionActivity.class);
                startActivity(homeIntent);
                dialogMsg.setMessage("Thank you! " + examinator.getName());
            }
            else
                dialogMsg.setMessage("The input password was wrong!");
        }

        alert = dialogMsg.create();
        alert.show();
    }
}
