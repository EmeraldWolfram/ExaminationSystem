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
import android.view.MenuItem;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.R;
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
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;
import com.info.ghiny.examsystem.model.TakeAttdModel;

/**
 * Created by user09 on 11/17/2016.
 */

public class SubmissionPresenter implements SubmissionMVP.MvpVPresenter, SubmissionMVP.MvpMPresenter{

    private SubmissionMVP.MvpView taskView;
    private SubmissionMVP.MvpModel taskModel;
    private Handler handler;
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
        if(ExternalDbLoader.getTcpClient() != null){
            ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
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
    public boolean onNavigationItemSelected(Toolbar toolbar, MenuItem item, ErrorManager errManager,
                                            FragmentManager manager, DrawerLayout drawer) {
        RootFragment fragment;

        switch (item.getItemId()){
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
