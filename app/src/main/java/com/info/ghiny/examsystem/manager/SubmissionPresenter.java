package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.SettingActivity;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.fragments.FragmentAbsent;
import com.info.ghiny.examsystem.fragments.FragmentBarred;
import com.info.ghiny.examsystem.fragments.FragmentExempted;
import com.info.ghiny.examsystem.fragments.FragmentPresent;
import com.info.ghiny.examsystem.fragments.FragmentQuarantined;
import com.info.ghiny.examsystem.fragments.RootFragment;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TakeAttdModel;

/**
 * Created by user09 on 11/17/2016.
 */

public class SubmissionPresenter implements SubmissionMVP.MvpVPresenter, SubmissionMVP.MvpMPresenter{

    private SubmissionMVP.MvpView taskView;
    private SubmissionMVP.MvpModel taskModel;
    private Handler handler;
    private Handler attdSyn;
    //private boolean sent;
    private boolean uploadFlag;
    private boolean secureFlag;
    private boolean navFlag;


    public SubmissionPresenter(SubmissionMVP.MvpView taskView){
        this.taskView   = taskView;
        this.uploadFlag = false;
        this.secureFlag = false;
        this.navFlag    = false;
        this.attdSyn    = new Handler();
    }

    public void setTaskModel(SubmissionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onResume(final ErrorManager errManager) {
        if(ExternalDbLoader.getJavaHost() != null){
            ExternalDbLoader.getJavaHost().setMessageListener(new JavaHost.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    onChiefRespond(errManager, message);
                }
            });
        }
    }

    @Override
    public void onRestart() {
        uploadFlag = false;
        if(!secureFlag && !navFlag){
            secureFlag = true;
            taskView.securityPrompt(false);
        }
        navFlag = false;
    }

    @Override
    public void onDestroy() {
        taskView.closeProgressWindow();
        handler.removeCallbacks(taskModel);
    }

    @Override
    public void onUpload() {
        StaffIdentity staff = LoginModel.getStaff();
        AttendanceList list = TakeAttdModel.getAttdList();

        Integer presentSize = list.getNumberOfCandidates(Status.PRESENT);
        Integer absentSize  = list.getNumberOfCandidates(Status.ABSENT);
        Integer barredSize  = list.getNumberOfCandidates(Status.BARRED);
        Integer exemptedSize= list.getNumberOfCandidates(Status.EXEMPTED);
        Integer total       = presentSize + absentSize + barredSize + exemptedSize;

        String[] statusSize = new String[]{presentSize.toString(), absentSize.toString(),
                barredSize.toString(), exemptedSize.toString()};
        String totalSize    = total.toString();

        taskView.displayReportWindow(staff.getName(), staff.getExamVenue(), statusSize, totalSize);
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data) {
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            secureFlag  = false;
            String password = data.getStringExtra("Password");
            try{
                taskModel.matchPassword(password);

                if(uploadFlag){
                    taskModel.uploadAttdList();
                    taskView.openProgressWindow("Sending:", "Uploading Attendance List...");
                    handler.postDelayed(taskModel, 5000);
                }
            } catch(ProcessException err){
                taskView.displayError(err);
                secureFlag = true;
                taskView.securityPrompt(uploadFlag);
            }
        }
    }

    @Override
    public void onChiefRespond(ErrorManager errManager, String messageRx) {
        try{
            String type = JsonHelper.parseType(messageRx);
            if(type.equals(JsonHelper.TYPE_SUBMISSION)){
                taskView.closeProgressWindow();
                taskModel.verifyChiefResponse(messageRx);
            } else if(type.equals(JsonHelper.TYPE_ATTENDANCE_UP)){
                attdSyn.postDelayed(synTimer,1000);
            }
        } catch (ProcessException err){
            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
    }

    @Override
    public boolean onNavigationItemSelected(Toolbar toolbar, int itemId, ErrorManager errManager,
                                            FragmentManager manager, DrawerLayout drawer) {
        RootFragment fragment;

        switch (itemId){
            case R.id.nav_present:
                fragment    = new FragmentPresent();
                toolbar.setSubtitle("Present Candidates");
                break;
            case R.id.nav_absent:
                fragment    = new FragmentAbsent();
                toolbar.setSubtitle("Absent Candidates");
                break;
            case R.id.nav_barred:
                fragment    = new FragmentBarred();
                toolbar.setSubtitle("Barred Candidates");
                break;
            case R.id.nav_exempted:
                fragment    = new FragmentExempted();
                toolbar.setSubtitle("Exempted Candidates");
                break;
            case R.id.nav_quarantined:
                fragment    = new FragmentQuarantined();
                toolbar.setSubtitle("Quarantined Candidates");
                break;
            default:
                fragment    = new FragmentPresent();
                toolbar.setSubtitle("Present Candidates");
        }

        fragment.setTaskModel(taskModel);
        fragment.setErrorManager(errManager);

        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.submitContainer, fragment);
        ft.commit();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onSetting() {
        navFlag = true;
        taskView.navigateActivity(SettingActivity.class);
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_POSITIVE:
                uploadFlag = true;
                secureFlag = true;
                taskView.securityPrompt(true);
                dialog.cancel();
                break;
            default:
                dialog.cancel();
        }
    }

    //= MvpMPresenter ==============================================================================

    @Override
    public void onCancel(DialogInterface dialog) {
        dialog.cancel();
    }

    @Override
    public void onTimesOut(ProcessException err) {
        if(taskView != null){
            taskView.closeProgressWindow();
            taskView.displayError(err);
        }
    }

    Runnable synTimer   = new Runnable() {
        @Override
        public void run() {
            //Notify New Set Added or Removed;
        }
    };

    //==============================================================================================
    void setUploadFlag(boolean uploadFlag) {
        this.uploadFlag = uploadFlag;
    }
    void setSecureFlag(boolean secureFlag) {
        this.secureFlag = secureFlag;
    }
    void setNavFlag(boolean navFlag) {
        this.navFlag = navFlag;
    }
    boolean isUploadFlag() {
        return uploadFlag;
    }
    boolean isSecureFlag() {
        return secureFlag;
    }
    boolean isNavFlag() {
        return navFlag;
    }
}
