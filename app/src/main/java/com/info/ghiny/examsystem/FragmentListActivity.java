package com.info.ghiny.examsystem;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.info.ghiny.examsystem.adapter.ViewPagerAdapter;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.tools.AssignHelper;
import com.info.ghiny.examsystem.tools.CustomToast;

/**
 * Created by GhinY on 12/06/2016.
 */
public class FragmentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_list);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        assert viewPager != null; assert tabLayout != null;
        // Initilization

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
    }

    public void onUpload(View view){
        //if(ExternalDbLoader.updateAttdList(AssignHelper.getAttdList()))
        // clearDatabase;
        // else
        // throw retry
    }

}
