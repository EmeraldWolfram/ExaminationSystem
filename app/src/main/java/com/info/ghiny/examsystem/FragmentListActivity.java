package com.info.ghiny.examsystem;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.info.ghiny.examsystem.adapter.ViewPagerAdapter;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.tools.ChiefLink;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.FragmentHelper;
import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.JsonHelper;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.TCPClient;

/**
 * Created by GhinY on 12/06/2016.
 */
public class FragmentListActivity extends AppCompatActivity {

    private ErrorManager errorManager;
    private FragmentHelper helper;
    private DialogInterface.OnClickListener timesOutListener =
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };

    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_list);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        assert viewPager != null;
        assert tabLayout != null;

        errorManager    = new ErrorManager(this);
        helper          = new FragmentHelper();

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                try{
                    ChiefLink.setCompleteFlag(true);
                    boolean uploaded = JsonHelper.parseBoolean(message);
                    CheckListLoader dbLoader = new CheckListLoader(FragmentListActivity.this);
                    //LocalDbLoader dbLoader  =
                    //        new LocalDbLoader(LocalDbLoader.DRIVER, LocalDbLoader.ADDRESS);
                    dbLoader.clearDatabase();
                } catch (ProcessException err){
                    //Intent errIn = new Intent(FragmentListActivity.this, FancyErrorWindow.class);
                    //errIn.putExtra("ErrorTxt", err.getErrorMsg());
                    //errIn.putExtra("ErrorIcon", err.getErrorIcon());
                    //startActivity(errIn);
                    ExternalDbLoader.getChiefLink().publishError(errorManager, err);

                }
            }
        });
    }

    //==============================================================================================
    public void onUpload(View view){
        Intent authorSign = new Intent(this, PopUpLogin.class);
        startActivityForResult(authorSign, PopUpLogin.PASSWORD_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == RESULT_OK){
            String password = data.getStringExtra("Password");
            try{
                if(!LoginHelper.getStaff().matchPassword(password))
                    throw new ProcessException("Submission denied. Incorrect Password",
                            ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);

                helper.uploadAttdList();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!ChiefLink.isComplete()){
                            ProcessException err = new ProcessException(
                                    "Server busy. Upload times out.\nPlease try again later.",
                                    ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                            err.setListener(ProcessException.okayButton, timesOutListener);
                            errorManager.displayError(err);
                        }
                    }
                }, 5000);
            } catch(ProcessException err){
                errorManager.displayError(err);
            }
        }
    }
}


