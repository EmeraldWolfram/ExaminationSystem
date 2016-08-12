package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.model.ChiefLink;
import com.info.ghiny.examsystem.model.FragmentHelper;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */
public class FragListManager {
    private Handler handler;
    private GeneralView generalView;
    private FragmentHelper fragmentModel;

    public FragListManager(GeneralView generalView){
        this.generalView    = generalView;
        this.fragmentModel  = new FragmentHelper();
        this.handler        = new Handler();
    }

    public void setFragmentModel(FragmentHelper fragmentModel) {
        this.fragmentModel = fragmentModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void signToUpload(){
        generalView.navigateActivity(PopUpLogin.class);
    }

    public void onReceivePassword(int requestCode, int resultCode, Intent data){
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            String password = data.getStringExtra("Password");
            try{
                if(!LoginHelper.getStaff().matchPassword(password))
                    throw new ProcessException("Submission denied. Incorrect Password",
                            ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);

                fragmentModel.uploadAttdList();

                handler.postDelayed(timer, 5000);
            } catch(ProcessException err){
                generalView.displayError(err);
            }
        }
    }

    public void onResume(final ErrorManager errorManager){
        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                try{
                    ChiefLink.setCompleteFlag(true);
                    boolean uploaded = JsonHelper.parseBoolean(message);
                } catch (ProcessException err){
                    ExternalDbLoader.getChiefLink().publishError(errorManager, err);
                }
            }
        });
    }

    public void onDestroy(){
        handler.removeCallbacks(timer);
    }

    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            if(!ChiefLink.isComplete()){
                ProcessException err = new ProcessException(
                        "Server busy. Upload times out.\nPlease try again later.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                if(generalView != null){
                    generalView.displayError(err);
                }
            }
        }
    };
}
