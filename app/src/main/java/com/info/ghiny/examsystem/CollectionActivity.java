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
import com.info.ghiny.examsystem.tools.InfoCollectHelper;
import com.info.ghiny.examsystem.tools.JsonHelper;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.TCPClient;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.HashMap;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private static final String TAG = CollectionActivity.class.getSimpleName();

    private InfoCollectHelper helper;
    private ErrorManager errorManager;

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
                onScanBundle(result.getText());
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
        setContentView(R.layout.activity_collection);

        TextView bundleView = (TextView)findViewById(R.id.bundleText);
        assert bundleView  != null;
        bundleView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/DroidSerif-Regular.ttf"));

        helper          = new InfoCollectHelper();
        errorManager    = new ErrorManager(this);

        barcodeView = (BarcodeView) findViewById(R.id.bundleScanner);
        assert barcodeView != null;
        barcodeView.decodeContinuous(callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ExternalDbLoader.getTcpClient().setmMessageListener(new TCPClient.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                try{
                    ChiefLink.setCompleteFlag(false);
                    boolean ack = JsonHelper.parseBoolean(message);
                } catch (ProcessException err) {
                    Intent errIn = new Intent(CollectionActivity.this, FancyErrorWindow.class);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    //==============================================================================================
    private void onScanBundle(String scanStr){
        try{
            barcodeView.pause();
            helper.bundleCollection(scanStr);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!ChiefLink.isComplete()){
                        ProcessException err = new ProcessException(
                                "Bundle collection times out.",
                                ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                        err.setListener(ProcessException.okayButton, timesOutListener);
                        barcodeView.pause();
                        errorManager.displayError(err);
                        barcodeView.resume();
                    }
                }
            }, 10000);

        } catch (ProcessException err) {
            errorManager.displayError(err);
            barcodeView.resume();
        }
    }
}
