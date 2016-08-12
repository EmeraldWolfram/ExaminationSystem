package com.info.ghiny.examsystem.manager;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 08/08/2016.
 */
public class ConnectionManager {
    private GeneralView generalView;
    private LoginHelper loginModel;

    public ConnectionManager(GeneralView generalView){
        this.generalView = generalView;
        this.loginModel = new LoginHelper();
    }

    public void setLoginModel(LoginHelper loginModel) {
        this.loginModel = loginModel;
    }

    public void onScanForChief(String scanStr){
        try{
            loginModel.verifyChief(scanStr);
            generalView.navigateActivity(MainLoginActivity.class);
        } catch (ProcessException err) {
            generalView.displayError(err);
        }
    }

    public void setupConnection(){
        //Setup ChiefLink and TCP Client
    }

    public void onDestroy(){
        //Close ChiefLink and TCP Client
    }
}
