package com.info.ghiny.examsystem.manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.info.ghiny.examsystem.fragments.AbsentFragment;
import com.info.ghiny.examsystem.fragments.BarredFragment;
import com.info.ghiny.examsystem.fragments.ExemptedFragment;
import com.info.ghiny.examsystem.fragments.PresentFragment;
import com.info.ghiny.examsystem.fragments.QuarantinedFragment;

/**
 * Created by GhinY on 12/06/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

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
        }

        return title;
    }
}
