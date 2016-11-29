package com.info.ghiny.examsystem.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.CandidateDisplayHolder;
import com.info.ghiny.examsystem.interfacer.StatusFragmentMVP;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.manager.ErrorManager;
import com.info.ghiny.examsystem.manager.StatusPresentPresenter;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by user09 on 11/22/2016.
 */

public class FragmentPresent extends RootFragment implements StatusFragmentMVP.PresentMvpView {

    private SubmissionMVP.MvpModel taskModel;
    private RecyclerView recyclerView;
    private PresentListAdapter adapter;
    private ErrorManager errorManager;

    private StatusFragmentMVP.PresentMvpPresenter taskPresenter;
    private Bitmap trashIcon;
    private View uploadButton;

    public FragmentPresent(){}

    @Override
    public void setTaskModel(SubmissionMVP.MvpModel taskModel) {
        this.taskModel = taskModel;
    }

    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager   = errorManager;
    }

    //==============================================================================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trashIcon   = BitmapFactory.decodeResource(getResources(), R.drawable.trash_icon);
        initMVP();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater         inflater,
                             @Nullable ViewGroup    container,
                             @Nullable Bundle       savedInstanceState) {
        View view       =  inflater.inflate(R.layout.fragment_status_present, null);
        recyclerView    = (RecyclerView) view.findViewById(R.id.recyclerPresentList);
        uploadButton    = getActivity().findViewById(R.id.uploadButton);
        adapter         = new PresentListAdapter();
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            @Override
            public boolean onMove(RecyclerView              recyclerView,
                                  RecyclerView.ViewHolder   viewHolder,
                                  RecyclerView.ViewHolder   target) {
                return taskPresenter.onMove(recyclerView, viewHolder, target);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                taskPresenter.onSwiped(uploadButton, viewHolder, direction);
            }

            @Override
            public void onChildDraw(Canvas canvas, RecyclerView rccView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isActive) {
                taskPresenter.onChildDraw(canvas, rccView, viewHolder, dX, dY, actionState, isActive);
                super.onChildDraw(canvas, rccView, viewHolder, dX, dY, actionState, isActive);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onResume() {
        taskPresenter.onResume();
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onPause() {
        taskPresenter.onPause();
        super.onPause();
    }

    private void initMVP(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        StatusPresentPresenter presenter = new StatusPresentPresenter(trashIcon, preferences, this);
        presenter.setTaskModel(taskModel);
        this.taskPresenter  = presenter;
    }

    //= Adapter Class ==============================================================================

    public class PresentListAdapter extends RecyclerView.Adapter<CandidateDisplayHolder> {
        @Override
        public CandidateDisplayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return taskPresenter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(CandidateDisplayHolder holder, int position) {
            taskPresenter.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return taskPresenter.getItemCount();
        }
    }

    //= MVP View Interface Implementation ==========================================================

    @Override
    public void insertCandidate(int position) {
        adapter.notifyItemInserted(position);
    }

    @Override
    public void removeCandidate(int position, int newSize) {
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, newSize);
    }

    @Override
    public void displayError(ProcessException err) {
        errorManager.displayError(err);
    }
}
