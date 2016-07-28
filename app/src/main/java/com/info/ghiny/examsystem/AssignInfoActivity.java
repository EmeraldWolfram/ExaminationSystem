package com.info.ghiny.examsystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.tools.AssignHelper;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.CustomToast;
import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.OnSwipeListener;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by GhinY on 13/05/2016.
 */
public class AssignInfoActivity extends AppCompatActivity {
    private static final String TAG = AssignInfoActivity.class.getSimpleName();

    //Required Tools
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

        errManager  = new ErrorManager(this);
        AssignHelper.setAssignAct(this);

        try{
            LocalDbLoader jdbcLoader = new LocalDbLoader(LocalDbLoader.DRIVER, LocalDbLoader.ADDRESS);
            //CheckListLoader clLoader = new CheckListLoader(this);
            AssignHelper.initLoader(jdbcLoader);
            //AssignHelper.initLoader(clLoader);
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    //==============================================================================================
    public void onScanTableOrCandidate(String scanString){
        try{
            AssignHelper.tryAssignScanValue(scanString);
        } catch(ProcessException err){
            barcodeView.pause();
            errManager.displayError(err);
            barcodeView.resume();
        }
    }

    //UI Setting method
    public static void clearViews(AssignInfoActivity act){
        TextView cddView    = (TextView)act.findViewById(R.id.canddAssignText);
        TextView regNumView = (TextView)act.findViewById(R.id.regNumAssignText);
        TextView paperView  = (TextView)act.findViewById(R.id.paperAssignText);

        assert cddView     != null; assert regNumView   != null;    assert paperView   != null;

        setTableView(act, "");
        cddView.setText("");
        regNumView.setText("");
        paperView.setText("");
    }

    public static void setTableView(AssignInfoActivity act, String tableNumber){
        TextView tableView  = (TextView)act.findViewById(R.id.tableNumberText);
        assert tableView != null;
        tableView.setTypeface(Typeface.createFromAsset(act.getAssets(), "fonts/Chunkfive.otf"));

        tableView.setText(tableNumber);
    }

    public static void setCandidateView(AssignInfoActivity act, Candidate cdd){
        try{
            TextView cddView    = (TextView)act.findViewById(R.id.canddAssignText);
            TextView regNumView = (TextView)act.findViewById(R.id.regNumAssignText);
            TextView paperView  = (TextView)act.findViewById(R.id.paperAssignText);

            assert cddView     != null; assert regNumView   != null;    assert paperView   != null;

            cddView.setTypeface(Typeface.createFromAsset(act.getAssets(), "fonts/Oswald-Bold.ttf"));
            regNumView.setTypeface(Typeface.createFromAsset(act.getAssets(), "fonts/DroidSerif-Regular.ttf"));
            paperView.setTypeface(Typeface.createFromAsset(act.getAssets(), "fonts/DroidSerif-Regular.ttf"));

            cddView.setText(cdd.getExamIndex());
            regNumView.setText(cdd.getRegNum());
            paperView.setText(cdd.getPaper().toString());
        } catch (ProcessException err) {
            act.barcodeView.pause();
            act.errManager.displayError(err);
            act.barcodeView.resume();
        }

    }

}
/*
Removed Code in onScanValue

        TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
        TextView cddView    = (TextView)findViewById(R.id.canddAssignText);
        TextView regNumView = (TextView)findViewById(R.id.regNumAssignText);
        TextView paperView  = (TextView)findViewById(R.id.paperAssignText);

        assert tableView    != null;    assert cddView     != null;
        assert regNumView   != null;    assert paperView   != null;

        tableView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/Chunkfive.otf"));
        cddView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/Oswald-Bold.ttf"));
        regNumView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/DroidSerif-Regular.ttf"));
        paperView.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/DroidSerif-Regular.ttf"));


        ============================================================================================

            int scanPossibly    =   AssignHelper.checkScan(scanString);

            if(scanPossibly == AssignHelper.MAYBE_TABLE){
                AssignHelper.checkTable(Integer.parseInt(scanString));
                tableView.setText(scanString);
            }

            if(scanPossibly == AssignHelper.MAYBE_CANDIDATE){
                cdd = AssignHelper.checkCandidate(scanString);
                //Candidate is legal, display all the candidate value
                cddView.setText(cdd.getExamIndex());
                regNumView.setText(cdd.getRegNum());
                paperView.setText(cdd.getPaper().toString());
            }

            if(AssignHelper.tryAssignCandidate()){
                //Candidate successfully assigned, clear display and acknowledge with message
                clearViews(this);

                message.showCustomMessage(cdd.getExamIndex()+ " Assigned to "
                        + cdd.getTableNumber().toString(),
                        new IconManager().getIcon(IconManager.ASSIGNED));
            }


    REMOVED CODE IN onCreate() -> OnSwipeRight

    TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
                TextView cddView    = (TextView)findViewById(R.id.canddAssignText);
                TextView regNumView = (TextView)findViewById(R.id.regNumAssignText);
                TextView paperView  = (TextView)findViewById(R.id.paperAssignText);

                assert tableView    != null;    assert cddView     != null;
                assert regNumView   != null;    assert paperView   != null;

                AssignHelper.resetCandidate(Integer.parseInt(tableView.getText().toString()));
                tableView.setText("");      cddView.setText("");
                regNumView.setText("");     paperView.setText("");
 */