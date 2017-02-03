package com.info.ghiny.examsystem.interfacer;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.info.ghiny.examsystem.view_holder.CandidateDisplayHolder;
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

public interface StatusFragmentMVP {

    interface AbsentMvpView {
        void insertCandidate(int position);
        void removeCandidate(int position, int newSize);
        void displayError(ProcessException err);
    }

    interface AbsentMvpVPresenter extends View.OnClickListener {
        CandidateDisplayHolder onCreateViewHolder(ViewGroup parent, int viewType);
        void onBindViewHolder(CandidateDisplayHolder holder, int position);
        int getItemCount();
        void onResume();
        void onPause();
        boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                       RecyclerView.ViewHolder target);
        void onSwiped(View refView, RecyclerView.ViewHolder viewHolder, int direction);
        void onChildDraw(Canvas canvas, RecyclerView recyclerView,
                         RecyclerView.ViewHolder viewHolder, float dX, float dY,
                         int actionState, boolean isCurrentlyActive);
        void onRefresh();
    }

    interface PresentMvpView {
        void insertCandidate(int position);
        void removeCandidate(int position, int newSize);
        void displayError(ProcessException err);
    }

    interface PresentMvpPresenter extends View.OnClickListener, CandidateDisplayHolder.OnLongPressed {
        CandidateDisplayHolder onCreateViewHolder(ViewGroup parent, int viewType);
        void onBindViewHolder(CandidateDisplayHolder holder, int position);
        int getItemCount();
        void onResume();
        void onPause();
        boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                       RecyclerView.ViewHolder target);
        void onSwiped(View refView, RecyclerView.ViewHolder viewHolder, int direction);
        void onChildDraw(Canvas canvas, RecyclerView recyclerView,
                         RecyclerView.ViewHolder viewHolder, float dX, float dY,
                         int actionState, boolean isCurrentlyActive);
        void onRefresh();
    }
}
