package com.info.ghiny.examsystem;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;

public class SettingActivity extends AppCompatActivity implements GeneralView{

    private boolean secureFlag;
    private ErrorManager errorManager;
    private StaffIdentity user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingFragment()).commit();

        errorManager    = new ErrorManager(this);
        secureFlag      = false;
        user            = LoginModel.getStaff();
    }

    @Override
    protected void onRestart() {
        if(!secureFlag && user != null){
            secureFlag = true;
            securityPrompt(false);
        }
        super.onRestart();
    }

    public static class SettingFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_screen);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            secureFlag  = false;
            String password = data.getStringExtra("Password");
            try{
                matchPassword(password);
            } catch(ProcessException err){
                errorManager.displayError(err);
                securityPrompt(false);
                secureFlag = true;
            }
        }
    }

    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void navigateActivity(Class<?> cls) {

    }

    @Override
    public void runItSeparate(Runnable runner) {
        runOnUiThread(runner);
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    private void matchPassword(String password) throws ProcessException {
        if(!user.matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }
}
