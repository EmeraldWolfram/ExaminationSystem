package com.info.ghiny.examsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;
import com.info.ghiny.examsystem.manager.CollectionPresenter;
import com.info.ghiny.examsystem.manager.ConfigManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.CollectionModel;
import com.info.ghiny.examsystem.model.OnSwipeListener;
import com.info.ghiny.examsystem.model.ProcessException;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.List;

public class CollectionActivity extends AppCompatActivity implements CollectionMVP.View {
    private static final String TAG = CollectionActivity.class.getSimpleName();

    private ErrorManager errorManager;
    private CollectionMVP.PresenterForView taskPresenter;
    private ProgressDialog progDialog;
    private BarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                taskPresenter.onScan(result.getText());
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

        initMVP();

        RelativeLayout thisLayout = (RelativeLayout) findViewById(R.id.collectionLayout);
        assert thisLayout != null;
        thisLayout.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeBottom() {
                taskPresenter.onSwipeBottom();
            }
        });

        barcodeView.decodeContinuous(callback);
    }

    private void initMVP(){
        TextView head2 = (TextView)findViewById(R.id.bundleText);
        head2.setTypeface(Typeface.createFromAsset(this.getAssets(), ConfigManager.DEFAULT_FONT));

        errorManager    = new ErrorManager(this);
        barcodeView     = (BarcodeView) findViewById(R.id.bundleScanner);

        CollectionPresenter presenter   = new CollectionPresenter(this);
        CollectionModel model           = new CollectionModel(presenter);
        presenter.setHandler(new Handler());
        presenter.setTaskModel(model);
        taskPresenter   = presenter;
    }

    @Override
    protected void onResume() {
        taskPresenter.onResume(errorManager);
        super.onResume();
    }

    @Override
    protected void onPause() {
        taskPresenter.onPause();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        taskPresenter.onRestart();
        super.onRestart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        taskPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

    //==============================================================================================
    @Override
    public void navigateActivity(Class<?> cls) {
        Intent intent   = new Intent(this, cls);
        startActivity(intent);
    }

    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void beep() {}

    @Override
    public void pauseScanning() {
        barcodeView.pause();
    }

    @Override
    public void resumeScanning() {
        barcodeView.resume();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void openProgressWindow() {
        progDialog  = ProgressDialog.show(this, "Notify Collection:", "Waiting for Acknowledgement...");
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }
}
