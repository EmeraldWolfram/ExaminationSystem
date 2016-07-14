package com.info.ghiny.examsystem;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.tools.ChiefLink;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.TCPClient;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class MainLoginActivity extends AppCompatActivity {
    private static final String TAG = MainLoginActivity.class.getSimpleName();

    private static final int PASSWORD_REQ_CODE = 888;
    private Intent pwIntent;
    private static ErrorManager errorManager;
    private ChiefLink connect;

    private CompoundBarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                checkEligibilityOfTheIdentity(result.getText());
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

        TextView idView = (TextView)findViewById(R.id.identityText);
        assert idView != null;
        idView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/DroidSerif-Regular.ttf"));

        errorManager   = new ErrorManager(this);

        ChiefLink.setErrorManager(errorManager);

        TCPClient mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                try {
                    //this method calls the onProgressUpdate
                    connect.publishMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ExternalDbLoader.setTcpClient(mTcpClient);

        connect = new ChiefLink();
        connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        barcodeView = (CompoundBarcodeView) findViewById(R.id.loginScanner);
        assert barcodeView != null;
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("Searching for Authorized Invigilator's StaffIdentity");
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

    public void checkEligibilityOfTheIdentity(String scanStr){
        barcodeView.setStatusText(scanStr);
        barcodeView.pause();

        StaffIdentity staff = new StaffIdentity();
        staff.setIdNo(scanStr);
        LoginHelper.setStaff(staff);

        pwIntent = new Intent(this, PopUpLogin.class);
        startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(reqCode == PASSWORD_REQ_CODE && resCode == RESULT_OK){
            String password = data.getStringExtra("Password");
            onIdentityVerification(password);
        }
    }

    public void onIdentityVerification(final String password){
        try{
            LoginHelper.getStaff().setPassword(password);
            LoginHelper.matchStaffPw(password);
            /*while(LoginHelper.getStaff().getName() == null){
                if(ChiefLink.isTimesOutFlag()){
                    ProcessException err = new ProcessException(
                            "Identity verification has times out.\nDo you want to resend?",
                            ProcessException.RESEND_CANCEL, IconManager.WARNING);
                    err.setListener(ProcessException.resendButton,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onIdentityVerification(password);
                        }
                    });
                    err.setListener(ProcessException.cancelButton,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            barcodeView.resume();
                            // Do Nothing
                        }
                    });
                    throw err;
                }

            }*/

            //Successful login, start AssignActivity
            Intent assignIntent = new Intent(this, AssignInfoActivity.class);
            startActivity(assignIntent);
        } catch(ProcessException err){
            //Error were caught during checkInputPassword
            //Ready to start the PopUp Login prompt window
            pwIntent = new Intent(this, PopUpLogin.class);
            errorManager.displayError(err);
            startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
        }

    }


}
