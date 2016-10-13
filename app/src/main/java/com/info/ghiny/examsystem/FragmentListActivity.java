package com.info.ghiny.examsystem;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;


import com.info.ghiny.examsystem.fragments.AbsentFragment;
import com.info.ghiny.examsystem.fragments.BarredFragment;
import com.info.ghiny.examsystem.fragments.ExemptedFragment;
import com.info.ghiny.examsystem.fragments.PresentFragment;
import com.info.ghiny.examsystem.fragments.QuarantinedFragment;
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.interfacer.TaskConnView;
import com.info.ghiny.examsystem.manager.FragListManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 12/06/2016.
 */
public class FragmentListActivity extends AppCompatActivity implements GeneralView, TaskConnView {

    private ErrorManager errorManager;
    private FragListManager fragListManager;
    private ProgressDialog progDialog;

    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_list);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assert viewPager != null;
        assert tabLayout != null;

        errorManager    = new ErrorManager(this);
        fragListManager = new FragListManager(this, this);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
    }

    @Override
    protected void onResume() {
        fragListManager.onResume(errorManager);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        fragListManager.onRestart();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        fragListManager.onDestroy();
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
        fragListManager.toggleUnassign(view);
    }

    //==============================================================================================
    public void onUpload(View view){
        fragListManager.signToUpload();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragListManager.onPasswordReceived(requestCode, resultCode, data);
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
    public void openProgressWindow() {
        progDialog  = ProgressDialog.show(this, "Sending:", "Uploading Attendance List...");
    }

    @Override
    public void closeProgressWindow() {
        if(progDialog != null)
            progDialog.dismiss();
    }

    /**
     * Created by GhinY on 12/06/2016.
     */
    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            Fragment fragment = null;

            switch (index) {
                case 0:
                    fragment = new PresentFragment();
                    break;
                case 1:
                    fragment = new AbsentFragment();
                    break;
                case 2:
                    fragment = new BarredFragment();
                    break;
                case 3:
                    fragment = new ExemptedFragment();
                    break;
                case 4:
                    fragment = new QuarantinedFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position){
                case 0:
                    title="PRESENT";
                    break;
                case 1:
                    title="ABSENT";
                    break;
                case 2:
                    title="BARRED";
                    break;
                case 3:
                    title="EXEMPTED";
                    break;
                case 4:
                    title="QUARANTINED";
                    break;
            }

            return title;
        }
    }
}


