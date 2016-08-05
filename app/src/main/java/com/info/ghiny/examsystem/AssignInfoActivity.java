package com.info.ghiny.examsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.tools.AssignHelper;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.OnSwipeListener;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by GhinY on 13/05/2016.
 */
public class AssignInfoActivity extends AppCompatActivity implements ViewSetter{
    private static final String TAG = AssignInfoActivity.class.getSimpleName();

    //Required Tools
    private AssignHelper assignHelper;
    private ErrorManager errManager;
    private CompoundBarcodeView barcodeView;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
                onScanTableOrCandidate(result.getText());
            }
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    //==============================================================================================
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_info);

        errManager      = new ErrorManager(this);
        assignHelper    = new AssignHelper();
        assignHelper.setAssignAct(this);

        try{
            //LocalDbLoader jdbcLoader = new LocalDbLoader(LocalDbLoader.DRIVER, LocalDbLoader.ADDRESS);
            CheckListLoader clLoader = new CheckListLoader(this);
            //assignHelper.initLoader(jdbcLoader);
            assignHelper.initLoader(clLoader);
        } catch (ProcessException err){
            errManager.displayError(err);
        }

        /*LinearLayout assignResult = (LinearLayout)findViewById(R.id.assignInfoLinearLayout);
        assert assignResult != null;
        assignResult.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeRight() {
                TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
                assert tableView != null;
                AssignHelper.resetCandidate(Integer.parseInt(tableView.getText().toString()));
                AssignInfoActivity.clearViews(AssignInfoActivity.this);
            }
        });*/

        //Set swiping gesture
        RelativeLayout thisLayout = (RelativeLayout)findViewById(R.id.assignInfoBarcodeLayout);
        assert thisLayout != null;
        thisLayout.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeBottom(){
                Intent obtainIntent = new Intent(AssignInfoActivity.this, ObtainInfoActivity.class);
                startActivity(obtainIntent);
            }
            @Override
            public void onSwipeLeft() {
                Intent listIntent = new Intent(AssignInfoActivity.this, FragmentListActivity.class);
                startActivity(listIntent);
            }
        });

        //Barcode Viewer
        barcodeView = (CompoundBarcodeView) findViewById(R.id.assignScanner);
        assert barcodeView != null;
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("Ready to take candidates attendance");
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
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent   = new Intent(this, PopUpLogin.class);
        startActivityForResult(intent, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == RESULT_OK){
            String password = data.getStringExtra("Password");
            try{
                barcodeView.pause();
                if(!LoginHelper.getStaff().matchPassword(password))
                    throw new ProcessException("Access denied. Incorrect Password",
                            ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);

                barcodeView.resume();
            } catch(ProcessException err){
                errManager.displayError(err);
                Intent intent   = new Intent(this, PopUpLogin.class);
                startActivityForResult(intent, PopUpLogin.PASSWORD_REQ_CODE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        ProcessException err    = new ProcessException("Confirm logout and exit?",
                ProcessException.YES_NO_MESSAGE, IconManager.MESSAGE);
        err.setListener(ProcessException.yesButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                AssignInfoActivity.this.finish();
            }
        });
        errManager.displayError(err);
    }

    //==============================================================================================
    public void onScanTableOrCandidate(String scanString){
        try{
            assignHelper.tryAssignScanValue(scanString);
        } catch(ProcessException err){
            barcodeView.pause();
            errManager.displayError(err);
            barcodeView.resume();
        }
    }

    //= Interface Method ==========================================================================
    @Override
    public void clearView() {
        TextView cddView    = (TextView)findViewById(R.id.canddAssignText);
        TextView regNumView = (TextView)findViewById(R.id.regNumAssignText);
        TextView paperView  = (TextView)findViewById(R.id.paperAssignText);

        assert cddView     != null; assert regNumView   != null;    assert paperView   != null;

        setTableView(0);
        cddView.setText("");
        regNumView.setText("");
        paperView.setText("");
    }

    @Override
    public void setTableView(Integer tableNum) {
        TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
        assert tableView != null;
        tableView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Chunkfive.otf"));

        if(tableNum != 0)
            tableView.setText(tableNum.toString());
        else
            tableView.setText("");
    }

    @Override
    public void setCandidateView(Candidate cdd) {
        try{
            TextView cddView    = (TextView)findViewById(R.id.canddAssignText);
            TextView regNumView = (TextView)findViewById(R.id.regNumAssignText);
            TextView paperView  = (TextView)findViewById(R.id.paperAssignText);

            assert cddView     != null; assert regNumView   != null;    assert paperView   != null;

            cddView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Oswald-Bold.ttf"));
            regNumView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DroidSerif-Regular.ttf"));
            paperView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DroidSerif-Regular.ttf"));

            cddView.setText(cdd.getExamIndex());
            regNumView.setText(cdd.getRegNum());
            paperView.setText(cdd.getPaper().toString());
        } catch (ProcessException err) {
            barcodeView.pause();
            errManager.displayError(err);
            barcodeView.resume();
        }
    }
}