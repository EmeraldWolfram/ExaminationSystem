package com.info.ghiny.examsystem;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class MainLoginActivity extends AppCompatActivity {
    private static final String TAG = MainLoginActivity.class.getSimpleName();
    private static final int PASSWORD_REQ_CODE = 888;
    private Intent pwIntent;
    private CompoundBarcodeView barcodeView;
    private ExamDatabaseHelper databaseHelper;
    private Identity examiner;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                examiner = databaseHelper.getIdentity(result.getText());
                checkEligibilityOfTheIdendity();
                //Create the object here by a getIdentity method from Database
            }
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

        barcodeView = (CompoundBarcodeView) findViewById(R.id.loginScanner);
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("Searching for Authorized Invigilator's Identity");
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

    private void checkEligibilityOfTheIdendity(){
        if(examiner == null){
            Toast.makeText(this, "Invalid QR code!", Toast.LENGTH_SHORT).show();
        }
        else{
            barcodeView.setStatusText(examiner.getName() + "\n" + examiner.getRegNum());

            if(examiner.getEligible()) {
                barcodeView.pause();

                pwIntent = new Intent(this, PopUpLogin.class);
                pwIntent.putExtra("Name", examiner.getName());
                pwIntent.putExtra("RegNum", examiner.getRegNum());
                startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
            } else{
                Toast.makeText(this, "Unauthorized examiner detected!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(reqCode == PASSWORD_REQ_CODE && resCode == RESULT_OK){
            String password = data.getStringExtra("Password");
            pwIntent = new Intent(this, PopUpLogin.class);

            if(password.isEmpty()){
                Toast.makeText(this, "Please enter password to proceed", Toast.LENGTH_SHORT).show();

                pwIntent.putExtra("Name", examiner.getName());
                pwIntent.putExtra("RegNum", examiner.getRegNum());
                startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
            } else if (examiner.matchPassword(password)) {
                    Intent homeIntent = new Intent(this, HomeOptionActivity.class);
                    homeIntent.putExtra("Name", examiner.getName());
                    homeIntent.putExtra("RegNum", examiner.getRegNum());
                    startActivity(homeIntent);
            } else{
                    Toast.makeText(this, "The input password was wrong!", Toast.LENGTH_SHORT).show();

                    pwIntent.putExtra("Name", examiner.getName());
                    pwIntent.putExtra("RegNum", examiner.getRegNum());
                    startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
            }
        }
    }
}
