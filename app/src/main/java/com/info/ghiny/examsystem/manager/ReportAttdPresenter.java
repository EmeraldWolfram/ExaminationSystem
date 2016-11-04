package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

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
import com.info.ghiny.examsystem.interfacer.ReportAttdMVP;
import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;
import com.info.ghiny.examsystem.model.TakeAttdModel;

import java.util.HashMap;

/**
 * Created by GhinY on 08/08/2016.
 */
public class ReportAttdPresenter implements ReportAttdMVP.VPresenter, ReportAttdMVP.MPresenter {
    private ReportAttdMVP.View taskView;
    private ReportAttdMVP.Model taskModel;
    private boolean sent;
    private HashMap<String, Fragment> fragmentHashMap;

    private Handler handler;
    private boolean uploadFlag = false;

    public ReportAttdPresenter(ReportAttdMVP.View taskView){
        this.taskView       = taskView;
        /*fragmentHashMap.put("PRESENT",      new PresentFragment());
        fragmentHashMap.put("ABSENT",       new AbsentFragment());
        fragmentHashMap.put("BARRED",       new BarredFragment());
        fragmentHashMap.put("EXEMPTED",     new ExemptedFragment());
        fragmentHashMap.put("QUARANTINED",  new QuarantinedFragment());*/
    }

    @Override
    public boolean isSent() {
        return sent;
    }

    @Override
    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public void setTaskModel(ReportAttdMVP.Model taskModel) {
        this.taskModel = taskModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean isUploadFlag(){
        return uploadFlag;
    }

    @Override
    public void onResume(final ErrorManager errorManager){
        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                onChiefRespond(errorManager, message);
            }
        });
    }

    @Override
    public void onRestart() {
        uploadFlag = false;
        taskView.securityPrompt(false);
    }

    @Override
    public void onDestroy(){
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
    public void onChiefRespond(ErrorManager errManager, String messageRx) {
        try{
            taskView.closeProgressWindow();
            this.setSent(true);
            boolean uploaded = JsonHelper.parseBoolean(messageRx);
        } catch (ProcessException err){
            ExternalDbLoader.getConnectionTask().publishError(errManager, err);
        }
    }

    @Override
    public void onPasswordReceived(int requestCode, int resultCode, Intent data){
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

    @Override
    public Fragment getItem(int index) {
        Fragment fragment = null;

        switch (index) {
            case 0:
                PresentFragment preFragment = new PresentFragment();
                preFragment.setTaskModel(taskModel);
                fragment = preFragment;
                break;
            case 1:
                AbsentFragment absfragment  = new AbsentFragment();
                absfragment.setTaskModel(taskModel);
                fragment = absfragment;
                break;
            case 2:
                BarredFragment barfragment  = new BarredFragment();
                barfragment.setTaskModel(taskModel);
                fragment = barfragment;
                break;
            case 3:
                ExemptedFragment exfragment = new ExemptedFragment();
                exfragment.setTaskModel(taskModel);
                fragment = exfragment;
                break;
            case 4:
                QuarantinedFragment qfragment = new QuarantinedFragment();
                qfragment.setTaskModel(taskModel);
                fragment = qfragment;
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public void signToUpload(){
        uploadFlag = true;
        taskView.securityPrompt(true);
    }

    @Override
    public void toggleUnassign(View view){
        float clear     = 1.0f;
        float chalky    = 0.1f;

        ViewGroup parent = (ViewGroup) view.getParent();

        TextView table  = (TextView) parent.findViewById(R.id.assignedTableText);
        TextView cdd    = (TextView) parent.findViewById(R.id.assignedCddText);
        TextView prg    = (TextView) parent.findViewById(R.id.assignedPrgText);
        CheckBox bt     = (CheckBox) parent.findViewById(R.id.uncheckPresent);
        TextView status = (TextView) parent.findViewById(R.id.checkboxStatus);
        try{
            if(bt.isChecked()){
                taskModel.assignCandidate(cdd.getText().toString());
                table.setAlpha(clear);
                cdd.setAlpha(clear);
                prg.setAlpha(clear);
                status.setText(R.string.checked);
                //remove from delete list by MODEL
            } else {
                taskModel.unassignCandidate(table.getText().toString(), cdd.getText().toString());
                table.setAlpha(chalky);
                cdd.setAlpha(chalky);
                prg.setAlpha(chalky);
                status.setText(R.string.unchecked);
                //add to delete list by MODEL
            }
        } catch (ProcessException err) {
            taskView.displayError(err);
        }
    }

    @Override
    public void onTimesOut(ProcessException err) {
        if(taskView != null){
            taskView.closeProgressWindow();
            taskView.displayError(err);
        }
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

    @Override
    public void onCancel(DialogInterface dialog) {
        dialog.cancel();
    }
}
