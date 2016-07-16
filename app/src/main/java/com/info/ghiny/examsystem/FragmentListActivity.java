package com.info.ghiny.examsystem;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.info.ghiny.examsystem.adapter.ViewPagerAdapter;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.tools.AssignHelper;
import com.info.ghiny.examsystem.tools.ChiefLink;
import com.info.ghiny.examsystem.tools.CustomToast;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.FragmentHelper;
import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.JsonHelper;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.TCPClient;

/**
 * Created by GhinY on 12/06/2016.
 */
public class FragmentListActivity extends AppCompatActivity {

    private ErrorManager errorManager;
    private TCPClient tcpClient;
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
        assert viewPager != null; assert tabLayout != null;
        // Initilization

        errorManager    = new ErrorManager(this);
        tcpClient       = new TCPClient(new TCPClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                try{
                    boolean uploaded = JsonHelper.parseBoolean(message);
                    AssignHelper.getJdbcLoader().clearDatabase();
                } catch (ProcessException err){
                    errorManager.displayError(err);
                }
            }
        });
        ExternalDbLoader.setTcpClient(tcpClient);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ExternalDbLoader.setTcpClient(tcpClient);
    }

    //==============================================================================================
    public void onUpload(View view){
        try{
            FragmentHelper.uploadAttdList();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try{
                        ProcessException err;
                        if(!ChiefLink.isComplete()){
                            err = new ProcessException(
                                    "Server busy. Upload times out.\nPlease try again later.",
                                    ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                            err.setListener(ProcessException.okayButton, timesOutListener);
                            throw err;
                        }
                    } catch (ProcessException err){
                        errorManager.displayError(err);
                    }
                }
            }, 10000);
        } catch (ProcessException err){
            errorManager.displayError(err);
        }
    }

}
