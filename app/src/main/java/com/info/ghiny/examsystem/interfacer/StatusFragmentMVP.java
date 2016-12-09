package com.info.ghiny.examsystem.interfacer;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.info.ghiny.examsystem.view_holder.CandidateDisplayHolder;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by user09 on 11/25/2016.
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
    }
}
