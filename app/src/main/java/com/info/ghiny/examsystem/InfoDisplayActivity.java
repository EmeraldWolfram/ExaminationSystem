package com.info.ghiny.examsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.info.ghiny.examsystem.interfacer.InfoDisplayMVP;
import com.info.ghiny.examsystem.manager.ConfigManager;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.manager.InfoDisplayPresenter;
import com.info.ghiny.examsystem.model.InfoDisplayModel;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class InfoDisplayActivity extends AppCompatActivity implements InfoDisplayMVP.ViewFace {
    private InfoDisplayMVP.Presenter taskPresenter;
    private DisplayListAdapter listAdapter;
    private ErrorManager errorManager;

    private RelativeLayout help;
    private boolean helpDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        initMVP();
        initView();

        taskPresenter.onCreate(getIntent());
    }

    @Override
    protected void onRestart() {
        taskPresenter.onRestart();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if(helpDisplay){
            helpDisplay = false;
            help.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private void initMVP(){
        errorManager        = new ErrorManager(this);

        InfoDisplayPresenter presenter  = new InfoDisplayPresenter(this, new ConfigManager(this));
        InfoDisplayModel model  = new InfoDisplayModel();
        presenter.setTaskModel(model);
        this.taskPresenter  = presenter;
    }

    private void initView(){
        listAdapter = new DisplayListAdapter();
        ListView paperList = (ListView)findViewById(R.id.paperInfoList);
        assert paperList != null;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        paperList.setAdapter(listAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        taskPresenter.onPasswordReceived(requestCode, resultCode, data);
    }

    @Override
    public void notifyDataSetChanged() {
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void runItSeparate(Runnable runner) {
        runOnUiThread(runner);
    }

    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }

    @Override
    public void navigateActivity(Class<?> cls) {
        Intent next = new Intent(this, cls);
        startActivity(next);
    }

    @Override
    public void securityPrompt(boolean cancellable) {
        Intent secure   = new Intent(this, PopUpLogin.class);
        secure.putExtra("Cancellable", cancellable);
        startActivityForResult(secure, PopUpLogin.PASSWORD_REQ_CODE);
    }

    //==============================================================================================
    private class DisplayListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return taskPresenter.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return taskPresenter.getView(position, convertView, parent);
        }

        @Override
        public long getItemId(int position) {
            return taskPresenter.getItemId(position);
        }

        @Override
        public Object getItem(int position) {
            return taskPresenter.getItem(position);
        }
    }
}
