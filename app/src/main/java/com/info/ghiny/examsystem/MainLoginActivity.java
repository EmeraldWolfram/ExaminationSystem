package com.info.ghiny.examsystem;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.ExamDatabaseLoader;
import com.info.ghiny.examsystem.database.Identity;
import com.info.ghiny.examsystem.tools.CustomToast;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class MainLoginActivity extends AppCompatActivity {
    private static final String TAG = MainLoginActivity.class.getSimpleName();

    private ExamDatabaseLoader databaseHelper;
    private static final int PASSWORD_REQ_CODE = 888;
    private Identity examiner;
    private Intent pwIntent;
    private CustomToast message;

    private CompoundBarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                examiner = databaseHelper.getIdentity(result.getText());
                checkEligibilityOfTheIdendity();
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

        databaseHelper = new ExamDatabaseLoader(this);
        message = new CustomToast(this);

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
            message.showCustomMessageWithCondition(CustomToast.notId, R.drawable.warn_icon,
                    message.checkEqualToast(CustomToast.notId));
        } else {
            barcodeView.setStatusText(examiner.getName() + "\n" + examiner.getRegNum());

            if(examiner.getEligible()) {
                barcodeView.pause();

                pwIntent = new Intent(this, PopUpLogin.class);
                pwIntent.putExtra("Name", examiner.getName());
                pwIntent.putExtra("RegNum", examiner.getRegNum());
                startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
            } else
                message.showCustomMessageWithCondition(CustomToast.unathr, R.drawable.warn_icon,
                        message.checkEqualToast(CustomToast.unathr));
        }
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(reqCode == PASSWORD_REQ_CODE && resCode == RESULT_OK){
            String password = data.getStringExtra("Password");
            pwIntent = new Intent(this, PopUpLogin.class);

            if(password.isEmpty()) {
                //If the user didn't enter a password
                message.showCustomMessageWithCondition(CustomToast.emptyPW, R.drawable.msg_icon,
                        message.checkEqualToast(CustomToast.emptyPW));

                pwIntent.putExtra("Name", examiner.getName());
                pwIntent.putExtra("RegNum", examiner.getRegNum());

                startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
            }
            else if (examiner.matchPassword(password)) {
                //If the user entered CORRECT password
                Intent assignIntent = new Intent(this, AssignInfoActivity.class);
                startActivity(assignIntent);
            }
            else {
                //If the user entered INCORRECT password
                message.showCustomMessageWithCondition(CustomToast.wrongPW, R.drawable.warn_icon,
                        message.checkEqualToast(CustomToast.wrongPW));

                pwIntent.putExtra("Name", examiner.getName());
                pwIntent.putExtra("RegNum", examiner.getRegNum());

                startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
            }
        }
    }
}
