package com.info.ghiny.examsystem;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.Role;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.LoginMVP;
import com.info.ghiny.examsystem.manager.LoginPresenter;
import com.info.ghiny.examsystem.manager.ConfigManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TakeAttdModel;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.Calendar;
import java.util.HashMap;
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

public class MainLoginActivity extends AppCompatActivity implements LoginMVP.MvpView {
    public static final String TAG = MainLoginActivity.class.getSimpleName();

    //private LoginPresenter loginManager;
    private LoginMVP.MvpVPresenter taskPresenter;
    private LocalDbLoader dbLoader;
    private ErrorManager errorManager;
    private BeepManager beepManager;
    private ProgressDialog progDialog;

    private RelativeLayout help;
    private boolean helpDisplay;
    private int mode;
    private ImageView crossHairView;
    private FloatingActionButton scanInitiater;

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
        setContentView(R.layout.activity_main_login);

        initView();
        initMVP();

        TextView idView = (TextView)findViewById(R.id.identityText);    assert idView  != null;
        idView.setTypeface(Typeface.createFromAsset(this.getAssets(), ConfigManager.DEFAULT_FONT));

        taskPresenter.loadSetting();
        barcodeView.decodeContinuous(callback);
    }

    private void initView(){
        barcodeView     = (BarcodeView) findViewById(R.id.loginScanner);
        scanInitiater   = (FloatingActionButton) findViewById(R.id.loginScanButton);
        crossHairView   = (ImageView) findViewById(R.id.loginCrossHair);
        help            = (RelativeLayout) findViewById(R.id.loginHelpContext);
        helpDisplay     = false;

        errorManager    = new ErrorManager(this);
        beepManager     = new BeepManager(this);
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
        dbLoader        = new LocalDbLoader(this);
        SharedPreferences preferences   = PreferenceManager.getDefaultSharedPreferences(this);
        LoginPresenter presenter        = new LoginPresenter(this, preferences);
        LoginModel model                = new LoginModel(presenter, dbLoader);
        presenter.setHandler(new Handler());
        presenter.setTaskModel(model);
        taskPresenter           = presenter;
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
    protected void onDestroy() {
        taskPresenter.onDestroy();
        beepManager.close();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater   = this.getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        StaffIdentity staff;
        staff = new StaffIdentity("123456", true, "Staff 1", "M4");
        staff.setPassword("1");
        try{
            staff.setHashPass(staff.hmacSha("1", JavaHost.getConnector().getDuelMessage()));
        } catch (ProcessException err) {
            Log.d("HMAC Error", err.getErrorMsg());
        }

        LoginModel.setStaff(staff);
        AttendanceList attdList = new AttendanceList();
        attdList.addCandidate(new Candidate(12, "RMB3", "Candidate A", "15WAR00001", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(15, "RMB3", "Candidate B", "15WAR00002", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(14, "RMB3", "Candidate C", "15WAR00003", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(13, "RMB3", "Candidate D", "15WAR00004", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(0, "RMB3", "Candidate E", "15WAR00005", "BAME 0001", Status.ABSENT));
        attdList.addCandidate(new Candidate(0, "RMB3", "Candidate F", "15WAR00006", "BAME 0001", Status.ABSENT));
        attdList.addCandidate(new Candidate(0, "RMB3", "Candidate G", "15WAR00007", "BAME 0001", Status.BARRED));
        attdList.addCandidate(new Candidate(0, "RMB3", "Candidate H", "15WAR00008", "BAME 0001", Status.EXEMPTED));
        attdList.addCandidate(new Candidate(0, "RMB3", "Candidate I", "15WAR00009", "BAME 0001", Status.QUARANTINED));

        attdList.addCandidate(new Candidate(21, "RMC3", "Candidate J", "15WAR00010", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(22, "RMC3", "Candidate K", "15WAR00011", "BAME 0001", Status.PRESENT));

        attdList.addCandidate(new Candidate(26, "RMA3", "Candidate D", "15WAR00012", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(0, "RMA3", "Candidate L", "15WAR00013", "BAME 0001", Status.ABSENT));
        attdList.addCandidate(new Candidate(0, "RMA3", "Candidate M", "15WAR00014", "BAME 0001", Status.ABSENT));
        TakeAttdModel.setAttdList(attdList);

        ExamSubject examSubject = new ExamSubject("BAME 0001", "SUBJECT 1",
                10, Calendar.getInstance(), 20, "M4", Session.AM);
        HashMap<String, ExamSubject> papers = new HashMap<>();
        papers.put("BAME 0001", examSubject);
        Candidate.setPaperList(papers);

        switch (item.getItemId()){
            case R.id.action_help:
                helpDisplay = true;
                help.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_setting:
                Intent setting  = new Intent(this, SettingActivity.class);
                startActivity(setting);
                return true;
            case R.id.action_test:
                staff.setRole(Role.INVIGILATOR);
                navToHome(true, true, true, false);
                return true;
            case R.id.action_test_2:
                staff.setRole(Role.IN_CHARGE);
                navToHome(true, true, true, true);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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
    public void onInitiateScan(View view) {
        barcodeView.resume();
    }

    //==============================================================================================
    public void onActivityResult(int reqCode, int resCode, Intent data){
        taskPresenter.onPasswordReceived(reqCode, resCode, data);
    }

    //Interface of MvpView ============================================================================
    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent nextAct  = new Intent(this, cls);
        startActivity(nextAct);
    }

    @Override
    public void navToHome(Boolean attendance, Boolean bundle, Boolean info, Boolean distribution) {
        Intent tasks    = new Intent(this, HomeOptionActivity.class);
        tasks.putExtra(HomeOptionActivity.FEATURE_INFO_GRAB,  info);
        tasks.putExtra(HomeOptionActivity.FEATURE_ATTENDANCE, attendance);
        tasks.putExtra(HomeOptionActivity.FEATURE_COLLECTION, bundle);
        tasks.putExtra(HomeOptionActivity.FEATURE_CONNECTION, distribution);
        startActivity(tasks);
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
    public void securityPrompt(boolean cancellable) {
        Intent pwPrompt = new Intent(this, PopUpLogin.class);
        pwPrompt.putExtra("Cancellable", cancellable);
        startActivityForResult(pwPrompt, PopUpLogin.PASSWORD_REQ_CODE);
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
        this.beepManager.setBeepEnabled(beep);
        this.beepManager.setVibrateEnabled(vibrate);
        this.mode   = mode;
        if(mode == 1){
            scanInitiater.setVisibility(View.VISIBLE);
        } else {
            scanInitiater.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void run() {
        barcodeView.resume();
    }
}