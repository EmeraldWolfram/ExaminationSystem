package com.info.ghiny.examsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;
import com.info.ghiny.examsystem.manager.CollectionPresenter;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.CollectionModel;
import com.info.ghiny.examsystem.view_holder.OnSwipeAnimator;
import com.info.ghiny.examsystem.model.ProcessException;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.List;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class CollectionActivity extends AppCompatActivity implements CollectionMVP.View {
    private static final String TAG = CollectionActivity.class.getSimpleName();

    private ErrorManager errorManager;
    private BeepManager beepManager;
    private CollectionMVP.MvpVPresenter taskPresenter;

    private int mode;
    private ImageView crossHairView;
    private FloatingActionButton scanInitiater;
    private TextView collectorId;
    private TextView bundlePaper;
    private TextView bundleProgramme;
    private TextView bundleVenue;

    private RelativeLayout help;
    private boolean helpDisplay;

    private LinearLayout infoContainer;
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
        public void possibleResultPoints(List<ResultPoint> resultPoints) {}
    };

    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        initMVP();
        initView();

        taskPresenter.loadSetting();
        barcodeView.decodeContinuous(callback);
    }

    private void initView(){
        errorManager    = new ErrorManager(this);
        beepManager     = new BeepManager(this);
        barcodeView     = (BarcodeView) findViewById(R.id.bundleScanner);
        scanInitiater   = (FloatingActionButton) findViewById(R.id.collectScanButton);
        crossHairView   = (ImageView) findViewById(R.id.collectCrossHair);
        help            = (RelativeLayout) findViewById(R.id.collectionHelpContext);

        collectorId     = (TextView) findViewById(R.id.collectorId);
        bundlePaper     = (TextView) findViewById(R.id.bundlePaperCode);
        bundleProgramme = (TextView) findViewById(R.id.bundleProgramme);
        bundleVenue     = (TextView) findViewById(R.id.bundleVenue);

        infoContainer   = (LinearLayout) findViewById(R.id.collectionInfo);
        infoContainer.setOnTouchListener(new OnSwipeAnimator(this, infoContainer, taskPresenter));

        helpDisplay = false;
        help.setVisibility(View.INVISIBLE);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDisplay = false;
                help.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initMVP(){
        SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        CollectionPresenter presenter   = new CollectionPresenter(this, preferences);
        CollectionModel model           = new CollectionModel(presenter);
        presenter.setHandler(new Handler());
        presenter.setTaskModel(model);
        taskPresenter   = presenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                helpDisplay = true;
                help.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_setting:
                return taskPresenter.onSetting();
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void onBackPressed() {
        if(helpDisplay){
            helpDisplay = false;
            help.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

    //==============================================================================================
    @Override
    public void setBundle(String venue, String paper, String programme) {
        bundleVenue.setText(venue);
        bundlePaper.setText(paper);
        bundleProgramme.setText(programme);
    }

    @Override
    public void setCollector(String id) {
        collectorId.setText(id);
    }

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
    public void beep() {
        if(beepManager.isBeepEnabled()){
            beepManager.playBeepSoundAndVibrate();
        }
    }

    @Override
    public void pauseScanning() {
        barcodeView.pause();
    }

    @Override
    public void resumeScanning() {
        if(helpDisplay){
            helpDisplay = false;
            help.setVisibility(View.INVISIBLE);
        }
        switch (mode){
            case 2:
                barcodeView.postDelayed(this, 1000);
                break;
            case 3:
                barcodeView.postDelayed(this, 2000);
                break;
            case 4:
                barcodeView.postDelayed(this, 3000);
                break;
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void runItSeparate(Runnable runner) {
        runOnUiThread(runner);
    }

    @Override
    public void openProgressWindow(String title, String message) {
        progDialog  = new ProgressDialog(this, R.style.ProgressDialogTheme);
        progDialog.setMessage(message);
        progDialog.setTitle(title);
        progDialog.show();
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }

    @Override
    public void changeScannerSetting(boolean crossHair, boolean beep, boolean vibrate, int mode) {
        if(crossHair){
            this.crossHairView.setVisibility(View.VISIBLE);
        } else {
            this.crossHairView.setVisibility(View.INVISIBLE);
        }
        beepManager.setBeepEnabled(beep);
        beepManager.setVibrateEnabled(vibrate);
        this.mode   = mode;
        if(mode == 1){
            scanInitiater.setVisibility(View.VISIBLE);
        } else {
            scanInitiater.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onInitiateScan(View view) {
        barcodeView.resume();
    }

    @Override
    public void run() {
        barcodeView.resume();
    }
}
