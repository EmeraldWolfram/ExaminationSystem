package com.info.ghiny.examsystem.manager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.fragments.AbsentFragment;
import com.info.ghiny.examsystem.fragments.BarredFragment;
import com.info.ghiny.examsystem.fragments.ExemptedFragment;
import com.info.ghiny.examsystem.fragments.PresentFragment;
import com.info.ghiny.examsystem.fragments.QuarantinedFragment;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;

/**
 * Created by user09 on 11/17/2016.
 */

public class SubmissionPresenter implements SubmissionMVP.MvpVPresenter, SubmissionMVP.MvpMPresenter{

    public SubmissionMVP.MvpView taskView;
    public SubmissionMVP.MvpModel taskModel;
    public Handler handler;

    public SubmissionPresenter(SubmissionMVP.MvpView taskView){
        this.taskView   = taskView;
    }

    public void setTaskModel(SubmissionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item, FragmentManager manager, DrawerLayout drawer) {
        Fragment fragment;

        switch (item.getItemId()){
            case R.id.nav_present:
                fragment    = new PresentFragment();
                break;
            case R.id.nav_absent:
                fragment    = new AbsentFragment();
                break;
            case R.id.nav_barred:
                fragment    = new BarredFragment();
                break;
            case R.id.nav_exempted:
                fragment    = new ExemptedFragment();
                break;
            case R.id.nav_quarantined:
                fragment    = new QuarantinedFragment();
                break;
            default:
                fragment    = new PresentFragment();
        }

        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.submitContainer, fragment);
        ft.commit();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onUpload() {

    }
}
