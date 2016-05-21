package com.info.ghiny.examsystem;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by GhinY on 07/05/2016.
 */
public class ObtainInfoActivity extends AppCompatActivity {
    public ExamSystemAdapter systemAdapter;
    public ExamDatabaseHelper databaseHelper;

    private static final String TAG = ObtainInfoActivity.class.getSimpleName();
    private CompoundBarcodeView barcodeView;
    private Identity student;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                student = databaseHelper.getIdentity(result.getText());
                barcodeView.setStatusText(student.getName());
                displayResult();
                //get The info of the student here
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_info);

        databaseHelper = new ExamDatabaseHelper(this);
        systemAdapter = new ExamSystemAdapter(this, null);
        ListView paperList = (ListView)findViewById(R.id.paperInfoList);
        paperList.setAdapter(systemAdapter);

        barcodeView = (CompoundBarcodeView) findViewById(R.id.obtainScanner);
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("Scan candidate ID to get his/her exam details");
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

    private void displayResult(){
        //pass in the IC to getExamTable
        TextView studentDetail = (TextView)findViewById(R.id.studentInfoText);
        studentDetail.setText(student.getRegNum());
        systemAdapter.changeCursor(databaseHelper.getExamTable());
    }

    public void onBack(View view){
        finish();
    }
}
