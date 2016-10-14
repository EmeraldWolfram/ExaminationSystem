package com.info.ghiny.examsystem;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;


import com.info.ghiny.examsystem.interfacer.ReportAttdMVP;
import com.info.ghiny.examsystem.manager.ReportAttdPresenter;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.ReportAttdModel;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 12/06/2016.
 */
public class ReportAttdActivity extends AppCompatActivity implements ReportAttdMVP.View {

    private ErrorManager errorManager;
    private ProgressDialog progDialog;
    private ReportAttdMVP.VPresenter taskPresenter;

    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_list);
        initMVP();
        initView();
    }

    private void initView(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
    }

    private void initMVP(){
        errorManager    = new ErrorManager(this);

        ReportAttdPresenter presenter   = new ReportAttdPresenter(this);
        ReportAttdModel model        = new ReportAttdModel(presenter);
        presenter.setTaskModel(model);
        presenter.setHandler(new Handler());
        taskPresenter   = presenter;

    }

    @Override
    protected void onResume() {
        taskPresenter.onResume(errorManager);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        taskPresenter.onRestart();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        taskPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onUncheck(View view) {
        taskPresenter.toggleUnassign(view);
    }

    //==============================================================================================
    public void onUpload(View view){
        taskPresenter.signToUpload();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent secure   = new Intent(this, cls);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void securityPrompt(boolean cancellable){
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    public void openProgressWindow(String title, String message) {
        progDialog  = ProgressDialog.show(this, "Sending:", "Uploading Attendance List...");
        progDialog  = ProgressDialog.show(this, title, message);
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }

    /**
     * Created by GhinY on 12/06/2016.
     */
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            return taskPresenter.getItem(index);
        }

        @Override
        public int getCount() {
            return taskPresenter.getCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return taskPresenter.getPageTitle(position);
        }
    }
}


