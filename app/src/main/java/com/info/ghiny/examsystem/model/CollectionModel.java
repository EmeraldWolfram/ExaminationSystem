package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.PaperBundle;
import com.info.ghiny.examsystem.interfacer.CollectionMVP;

/**
 * Created by GhinY on 05/10/2016.
 */

public class CollectionModel implements CollectionMVP.Model {

    private CollectionMVP.PresenterForModel taskPresenter;
    private String staffIdentity;
    private PaperBundle bundle;

    public CollectionModel(CollectionMVP.PresenterForModel taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.staffIdentity  = null;
        this.bundle         = null;
    }

    PaperBundle getBundle() {
        return bundle;
    }

    void setBundle(PaperBundle bundle) {
        this.bundle = bundle;
    }

    void setStaffIdentity(String staffIdentity) {
        this.staffIdentity = staffIdentity;
    }

    String getStaffIdentity() {
        return staffIdentity;
    }

    //==============================================================================================

    @Override
    public void bundleCollection(String scanValue) throws ProcessException {
        if(!verifyCollector(scanValue) && !verifyBundle(scanValue)){
            throw new ProcessException("The decrypted QR code is neither Staff ID or Bundle",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
        }

        if(staffIdentity != null && bundle != null){
            taskPresenter.setAcknowledgementComplete(false);
            ExternalDbLoader.acknowledgeCollection(staffIdentity, bundle);

            staffIdentity   = null;
            bundle          = null;
            taskPresenter.notifyClearance();
        }
    }

    @Override
    public void run() {
        try{
            if(!taskPresenter.isAcknowledgementComplete()){
                ProcessException err = new ProcessException("PaperBundle collection times out.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, taskPresenter);
                err.setBackPressListener(taskPresenter);
                throw err;
            }
        } catch (ProcessException err) {
            taskPresenter.onTimesOut(err);
        }
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!LoginModel.getStaff().matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    @Override
    public void resetCollection() {
        this.bundle = null;
        this.staffIdentity = null;
        this.taskPresenter.notifyClearance();
    }

    boolean verifyCollector(String scanStr){
        if(scanStr.length() == 6){
            this.staffIdentity  = scanStr;
            this.taskPresenter.notifyCollectorScanned(scanStr);
            return true;
        }
        return false;
    }

    boolean verifyBundle(String scanStr){
        PaperBundle bundle  = new PaperBundle();

        if (bundle.parseBundle(scanStr)){
            this.bundle = bundle;
            this.taskPresenter.notifyBundleScanned(bundle);
            return true;
        }

        return false;
    }


}
