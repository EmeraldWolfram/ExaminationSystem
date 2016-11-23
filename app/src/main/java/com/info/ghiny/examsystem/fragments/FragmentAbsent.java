package com.info.ghiny.examsystem.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CandidateDisplayHolder;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.model.ProcessException;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by user09 on 11/23/2016.
 */

public class FragmentAbsent extends RootFragment implements View.OnClickListener {
    private SubmissionMVP.MvpModel taskModel;
    private ErrorManager errorManager;
    private RecyclerView recyclerView;
    private AbsentListAdapter adapter;

    private Candidate tempCandidate;
    private int tempPosition;

    private Bitmap retakeIcon;
    private Paint p;
    private ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            try{
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT){
                    tempCandidate   = adapter.removeCandidate(position);
                    tempPosition    = taskModel.assignCandidate(tempCandidate);
                    String msg  = String.format(Locale.ENGLISH, "%s is now PRESENT",
                            tempCandidate.getRegNum());

                    Snackbar.make(getActivity().findViewById(R.id.uploadButton),
                            msg, Snackbar.LENGTH_INDEFINITE)
                            .setAction("UNDO", FragmentAbsent.this).show();
                }
            } catch (ProcessException err) {
                errorManager.displayError(err);
            }
        }

        @Override
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;

                if(dX < 0){
                    p.setColor(Color.parseColor("#00b500"));
                    RectF background = new RectF(
                            (float) itemView.getRight() + dX,
                            (float) itemView.getTop(),
                            (float) itemView.getRight(),
                            (float) itemView.getBottom());
                    RectF icon_dest = new RectF(
                            (float) itemView.getRight()  - 2*width,
                            (float) itemView.getTop()    + width,
                            (float) itemView.getRight()  - width,
                            (float) itemView.getBottom() - width);
                    canvas.drawRect(background,p);
                    canvas.drawBitmap(retakeIcon, null, icon_dest, p);
                }
            }
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public FragmentAbsent(){}

    @Override
    public void setTaskModel(SubmissionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager   = errorManager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p   = new Paint();
        retakeIcon   = BitmapFactory.decodeResource(getResources(), R.drawable.scroll_with_tick_icon);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view       =  inflater.inflate(R.layout.fragment_status_absent, null);
        recyclerView    = (RecyclerView) view.findViewById(R.id.recyclerAbsentList);
        adapter         = new AbsentListAdapter(taskModel.getCandidatesWith(Status.ABSENT));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }


    public class AbsentListAdapter extends RecyclerView.Adapter<CandidateDisplayHolder> {

        private ArrayList<Candidate> absentList;


        AbsentListAdapter(ArrayList<Candidate> absentList) {
            this.absentList    = absentList;
        }

        @Override
        public CandidateDisplayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View v = LayoutInflater.from(context).inflate(R.layout.attendance_body, parent, false);
            return new CandidateDisplayHolder(context, v);
        }

        @Override
        public void onBindViewHolder(CandidateDisplayHolder holder, int position) {
            Candidate cdd   = absentList.get(position);

            holder.setCddName(cdd.getExamIndex());
            holder.setCddRegNum(cdd.getRegNum());
            holder.setCddPaperCode(cdd.getPaperCode());
            holder.setCddProgramme(cdd.getProgramme());
            holder.setCddTable(cdd.getTableNumber());
        }


        @Override
        public int getItemCount() {
            return absentList.size();
        }

        void insertCandidate(int position, Candidate cdd){
            absentList.add(position, cdd);
            notifyItemInserted(position);
        }

        Candidate removeCandidate(int position){
            Candidate cdd = absentList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, absentList.size());

            return cdd;
        }
    }

    @Override
    public void onClick(View v) {
        try{
            taskModel.unassignCandidate(tempPosition, tempCandidate);
            adapter.insertCandidate(tempPosition, tempCandidate);
        } catch (ProcessException err) {
            errorManager.displayError(err);
        }
    }
}
