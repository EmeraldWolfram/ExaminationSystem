package com.info.ghiny.examsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.info.ghiny.examsystem.interfacer.DistributionMVP;
import com.info.ghiny.examsystem.manager.DistributionPresenter;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.DistributionModel;
import com.info.ghiny.examsystem.model.ProcessException;

public class DistributionActivity extends AppCompatActivity implements DistributionMVP.MvpView {
    public static final String TAG = DistributionActivity.class.getSimpleName();

    private DistributionMVP.MvpVPresenter taskPresenter;
    private ErrorManager errorManager;

    private ImageView qrView;
    private boolean inForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribution);

        initView();
        initMVP();

        taskPresenter.onCreate(this);
    }

    @Override
    protected void onStart() {
        inForeground    = true;
        super.onStart();
    }

    @Override
    protected void onRestart() {
        taskPresenter.onRestart();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        inForeground    = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        taskPresenter.onDestroy();
        super.onDestroy();
    }

    private void initView(){
        this.qrView = (ImageView) findViewById(R.id.distributeQR);
    }

    private void initMVP(){
        this.errorManager = new ErrorManager(this);
        DistributionPresenter presenter = new DistributionPresenter(this);
        DistributionModel model = new DistributionModel(presenter);
        presenter.setTaskModel(model);

        this.taskPresenter  = presenter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

    @Override
    public boolean stillInForeground() {
        return inForeground;
    }

    @Override
    public void setImageQr(Bitmap bitmap) {
        if(bitmap != null){
            this.qrView.setImageBitmap(bitmap);
        } else {
            this.qrView.setImageResource(R.drawable.ic_menu_share);
        }
    }

    @Override
    public void runItSeparate(Runnable runner) {
        runOnUiThread(runner);
    }

    @Override
    public void displayError(ProcessException err) {
        this.errorManager.displayError(err);
    }

    @Override
    public void finishActivity() {
        this.finish();
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }
}
