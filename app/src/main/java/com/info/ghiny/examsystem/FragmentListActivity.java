package com.info.ghiny.examsystem;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.manager.FragListManager;
import com.info.ghiny.examsystem.manager.ViewPagerAdapter;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 12/06/2016.
 */
public class FragmentListActivity extends AppCompatActivity implements GeneralView {

    private ErrorManager errorManager;
    private FragListManager fragListManager;

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
        fragListManager = new FragListManager(this);

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

    public void securityPrompt(){

    }
}


