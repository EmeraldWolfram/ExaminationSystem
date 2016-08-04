package com.info.ghiny.examsystem;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.tools.AssignHelper;
import com.info.ghiny.examsystem.tools.ChiefLink;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.JsonHelper;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.TCPClient;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.HashMap;
import java.util.List;

public class MainLoginActivity extends AppCompatActivity {
    private static final String TAG = MainLoginActivity.class.getSimpleName();

    private static final int PASSWORD_REQ_CODE = 888;
    private ErrorManager errorManager;
    private ChiefLink connect;
    private LoginHelper helper;

    private DialogInterface.OnClickListener timesOutListener =
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    barcodeView.resume();
                    dialog.cancel();
                }
            };

    private static BarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                onScanIdentity(result.getText());
            }
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        TextView idView = (TextView)findViewById(R.id.identityText);
        assert idView  != null;
        idView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/DroidSerif-Regular.ttf"));

        helper  = new LoginHelper();
        errorManager    = new ErrorManager(this);
        //ChiefLink.setErrorManager(errorManager);
        connect = new ChiefLink();
        connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        barcodeView = (BarcodeView) findViewById(R.id.loginScanner);
        assert barcodeView != null;
        barcodeView.decodeContinuous(callback);
        //barcodeView.setStatusText("Searching for Authorized Invigilator's StaffIdentity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        while(ExternalDbLoader.getTcpClient() == null){}

        ExternalDbLoader.getTcpClient().setmMessageListener(new TCPClient.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                try{
                    ChiefLink.setCompleteFlag(true);

                    StaffIdentity id    = JsonHelper.parseStaffIdentity(message);
                    LoginHelper.setStaff(id);

                    AttendanceList attdList = JsonHelper.parseAttdList(message);
                    AssignHelper.setAttdList(attdList);

                    HashMap<String, ExamSubject> papers = JsonHelper.parsePaperMap(message);
                    Candidate.setPaperList(papers);

                    Intent assignIntent = new Intent(MainLoginActivity.this, AssignInfoActivity.class);
                    startActivity(assignIntent);
                } catch (ProcessException err) {
                    Intent errIn = new Intent(MainLoginActivity.this, FancyErrorWindow.class);
                    errIn.putExtra("Error", err.getErrorMsg());
                    startActivity(errIn);
                }
            }
        });
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    protected void onDestroy() {
        try {
            ExternalDbLoader.getTcpClient().stopClient();
            connect.cancel(true);
            connect = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    //==============================================================================================
    private void onScanIdentity(String scanStr){
        try{
            barcodeView.pause();
            helper.checkQrId(scanStr);

            Intent pwIntent = new Intent(this, PopUpLogin.class);
            startActivityForResult(pwIntent, PASSWORD_REQ_CODE);
        } catch (ProcessException err) {
            errorManager.displayError(err);
            barcodeView.resume();
        }

    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(reqCode == PASSWORD_REQ_CODE && resCode == RESULT_OK){
            String password = data.getStringExtra("Password");
            try{
                barcodeView.pause();
                helper.matchStaffPw(password);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!ChiefLink.isComplete()){
                            ProcessException err = new ProcessException(
                                    "Identity verification times out.",
                                    ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                            err.setListener(ProcessException.okayButton, timesOutListener);
                            barcodeView.pause();
                            errorManager.displayError(err);
                            barcodeView.resume();
                        }
                    }
                }, 10000);
            } catch(ProcessException err){
                errorManager.displayError(err);
                barcodeView.resume();
            }

        }
    }
}
