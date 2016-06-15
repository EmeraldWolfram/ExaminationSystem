package com.info.ghiny.examsystem;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.ExamDatabaseLoader;
import com.info.ghiny.examsystem.database.Identity;
import com.info.ghiny.examsystem.tools.CustomException;
import com.info.ghiny.examsystem.tools.CustomToast;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class MainLoginActivity extends AppCompatActivity {
    private static final String TAG = MainLoginActivity.class.getSimpleName();

    private ExamDatabaseLoader databaseHelper;
    private static final int PASSWORD_REQ_CODE = 888;
    private Identity invglt;
    private Intent pwIntent;
    private CustomToast message;

    private CompoundBarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                invglt = databaseHelper.getIdentity(result.getText());
                checkEligibilityOfTheIdentity();
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

    private void checkEligibilityOfTheIdentity(){
        try{
            LoginHelper.checkInvigilator(invglt);

            barcodeView.setStatusText(invglt.getName() + "\n" + invglt.getRegNum());
            barcodeView.pause();

            pwIntent = new Intent(this, PopUpLogin.class);
            pwIntent.putExtra("Name", invglt.getName());
            pwIntent.putExtra("RegNum", invglt.getRegNum());

            startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
        } catch (CustomException err){
            switch (err.getErrorCode()) {
                case CustomException.ERR_NULL_IDENTITY:
                    message.showCustomMessageWithCondition(CustomToast.notId, R.drawable.warn_icon,
                            message.checkEqualToast(CustomToast.notId));
                    break;
                case CustomException.ERR_ILLEGAL_IDENTITY:
                    message.showCustomMessageWithCondition(CustomToast.unathr, R.drawable.warn_icon,
                            message.checkEqualToast(CustomToast.unathr));
                    break;
            }
        }
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(reqCode == PASSWORD_REQ_CODE && resCode == RESULT_OK){
            String password = data.getStringExtra("Password");
            try{
                LoginHelper.checkInputPassword(invglt, password);
                Intent assignIntent = new Intent(this, AssignInfoActivity.class);
                startActivity(assignIntent);
            } catch(CustomException err){

                pwIntent = new Intent(this, PopUpLogin.class);
                pwIntent.putExtra("Name", invglt.getName());
                pwIntent.putExtra("RegNum", invglt.getRegNum());

                switch (err.getErrorCode()){
                    case CustomException.ERR_EMPTY_PASSWORD:
                        message.showCustomMessageWithCondition(CustomToast.emptyPW,
                                R.drawable.msg_icon, message.checkEqualToast(CustomToast.emptyPW));
                        startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
                        break;
                    case CustomException.ERR_WRONG_PASSWORD:
                        message.showCustomMessageWithCondition(CustomToast.wrongPW, R.drawable.warn_icon,
                                message.checkEqualToast(CustomToast.wrongPW));
                        startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
                        break;
                    case CustomException.ERR_NULL_IDENTITY:
                        throw new NullPointerException("Identity should not be null");

                }
            }
        }
    }
}
