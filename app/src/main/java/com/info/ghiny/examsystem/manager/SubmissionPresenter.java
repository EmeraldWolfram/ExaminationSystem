package com.info.ghiny.examsystem.manager;

import android.app.Activity;
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

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.fragments.AbsentFragment;
import com.info.ghiny.examsystem.fragments.BarredFragment;
import com.info.ghiny.examsystem.fragments.ExemptedFragment;
import com.info.ghiny.examsystem.fragments.PresentFragment;
import com.info.ghiny.examsystem.fragments.QuarantinedFragment;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;
import com.info.ghiny.examsystem.model.TakeAttdModel;

/**
 * Created by user09 on 11/17/2016.
 */

public class SubmissionPresenter implements SubmissionMVP.MvpVPresenter, SubmissionMVP.MvpMPresenter{

    public SubmissionMVP.MvpView taskView;
    public SubmissionMVP.MvpModel taskModel;
    public Handler handler;
    //private boolean sent;
    private boolean uploadFlag;

    public SubmissionPresenter(SubmissionMVP.MvpView taskView){
        this.taskView   = taskView;
        this.uploadFlag = false;
    }

    public void setTaskModel(SubmissionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    boolean isUploadFlag() {
        return uploadFlag;
    }

    @Override
    public void onResume(final ErrorManager errManager) {
        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                onChiefRespond(errManager, message);
            }
        });
    }

    @Override
    public void onRestart() {
        uploadFlag = false;
        taskView.securityPrompt(false);
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
                taskView.securityPrompt(uploadFlag);
            }
        }
    }

    @Override
    public void onChiefRespond(ErrorManager errManager, String messageRx) {
        try{
            taskView.closeProgressWindow();
            taskModel.verifyChiefResponse(messageRx);
            /*this.setSent(true);
            boolean uploaded = JsonHelper.parseBoolean(messageRx);
            throw new ProcessException("Submission successful",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);*/
        } catch (ProcessException err){
            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item, FragmentManager manager, DrawerLayout drawer) {
        Fragment fragment;

        switch (item.getItemId()){
            case R.id.nav_present:
                PresentFragment presentF    = new PresentFragment();
                //presentF.setTaskModel(taskModel);
                fragment    = presentF;
                break;
            case R.id.nav_absent:
                AbsentFragment absentF    = new AbsentFragment();
                //absentF.setTaskModel(taskModel);
                fragment    = absentF;
                break;
            case R.id.nav_barred:
                BarredFragment barredF    = new BarredFragment();
                //barredF.setTaskModel(taskModel);
                fragment    = barredF;
                break;
            case R.id.nav_exempted:
                ExemptedFragment exemptF    = new ExemptedFragment();
                //exemptF.setTaskModel(taskModel);
                fragment    = exemptF;
                break;
            case R.id.nav_quarantined:
                QuarantinedFragment quaranF = new QuarantinedFragment();
                //quaranF.setTaskModel(taskModel);
                fragment    = quaranF;
                break;
            default:
                PresentFragment presentFragment = new PresentFragment();
                //presentFragment.setTaskModel(taskModel);
                fragment    = presentFragment;
        }

        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.submitContainer, fragment);
        ft.commit();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_POSITIVE:
                uploadFlag = true;
                taskView.securityPrompt(true);
                dialog.cancel();
                break;
            default:
                dialog.cancel();
        }
    }

    //= MPresenter =================================================================================

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
}
