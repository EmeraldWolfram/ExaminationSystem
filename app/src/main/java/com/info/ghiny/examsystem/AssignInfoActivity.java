package com.info.ghiny.examsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by GhinY on 13/05/2016.
 */
public class AssignInfoActivity extends AppCompatActivity {
    private Intent grabIntent;
    private AlertDialog.Builder dialogMsg;
    private AlertDialog alert;
    private ExamDatabaseHelper databaseHelper;
    private String scanString;
    private int flag = 0;
    private Identity candidate;

    private static final String TAG = AssignInfoActivity.class.getSimpleName();
    private CompoundBarcodeView barcodeView;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
                scanString = result.getText();
                lockScanValue();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_info);

        databaseHelper = new ExamDatabaseHelper(this);

        dialogMsg = new AlertDialog.Builder(this);
        dialogMsg.setCancelable(true);
        dialogMsg.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {dialog.cancel();
            }
        });

        barcodeView = (CompoundBarcodeView) findViewById(R.id.assignScanner);
        barcodeView.decodeContinuous(callback);
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

    public void lockScanValue(){
        TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
        TextView cddView    = (TextView)findViewById(R.id.canddAssignText);


        if((flag & 3) == 3){
            dialogMsg.setMessage("Please click 'ADD TO LIST' button to proceed");
            alert = dialogMsg.create();
            alert.show();
        }
        else if(scanString.length() < 4){
            if((flag & 3) == 0 || (flag & 3) == 2){
                tableView.setText(scanString);
                flag = flag | 1;
            }else{
                dialogMsg.setMessage("Please scan candidate ID");
                alert = dialogMsg.create();
                alert.show();
            }
        }else{
            if((flag & 3) == 0 || (flag & 3) == 1){
                candidate = databaseHelper.getIdentity(scanString);
                cddView.setText(candidate.getName());
                flag = flag | 2;
            }
            else{
                dialogMsg.setMessage("Please scan table QR");
                alert = dialogMsg.create();
                alert.show();
            }
        }
    }

    public void onDiscard(View view){

    }

    public void onAddToList(View view){
        grabIntent = getIntent();
        TextView tableView  = (TextView)findViewById(R.id.tableNumberText);
        TextView cddView    = (TextView)findViewById(R.id.canddAssignText);

        if((flag & 3) == 3){
            grabIntent.putExtra("Table", Integer.valueOf(tableView.getText().toString()));
            grabIntent.putExtra("Candidate", cddView.getText().toString());
            flag = 4;
        }
        else{
            if((flag & 3) != 1)
                dialogMsg.setMessage("Please scan the table QR to proceed.");
            else
                dialogMsg.setMessage("Please scan candidate identity card.");
            alert = dialogMsg.create();
            alert.show();
        }
    }

    public void onFinish(View view){
        //Return the assigned candidates to CheckListActivity
        if((flag & 4) != 0)
            this.setResult(RESULT_OK, grabIntent);
        else
            this.setResult(RESULT_CANCELED, grabIntent);
        finish();
    }
}
