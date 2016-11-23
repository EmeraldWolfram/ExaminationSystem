package com.info.ghiny.examsystem.database;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.manager.ConfigManager;

import org.w3c.dom.Text;

/**
 * Created by user09 on 11/22/2016.
 */

public class CandidateDisplayHolder extends RecyclerView.ViewHolder {

    private OnLongPressed pressListener;

    private TextView cddName;
    private TextView cddRegNum;
    private TextView cddProgramme;
    private TextView cddPaperCode;
    private TextView cddTable;
    private ImageView cddLateTag;

    public CandidateDisplayHolder(Context context, View view, final OnLongPressed pressListener) {
        super(view);
        this.pressListener   = pressListener;
        this.cddName         = (TextView) view.findViewById(R.id.assignedCddText);
        this.cddRegNum       = (TextView) view.findViewById(R.id.assignedRegNumText);
        this.cddProgramme    = (TextView) view.findViewById(R.id.assignedPrgText);
        this.cddPaperCode    = (TextView) view.findViewById(R.id.assignedPaperText);
        this.cddTable        = (TextView) view.findViewById(R.id.assignedTableText);
        this.cddLateTag      = (ImageView)view.findViewById(R.id.assignedLateTag);

        cddTable.setTypeface(Typeface.createFromAsset(context.getAssets(), ConfigManager.THICK_FONT));
        cddName.setTypeface(Typeface.createFromAsset(context.getAssets(), ConfigManager.BOLD_FONT));
        cddProgramme.setTypeface(Typeface.createFromAsset(context.getAssets(), ConfigManager.DEFAULT_FONT));
        cddPaperCode.setTypeface(Typeface.createFromAsset(context.getAssets(), ConfigManager.DEFAULT_FONT));

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(cddLateTag.getVisibility() == View.VISIBLE){
                    cddLateTag.setVisibility(View.INVISIBLE);
                    pressListener.onLongPressed(getAdapterPosition(), v, false);
                } else {
                    cddLateTag.setVisibility(View.VISIBLE);
                    pressListener.onLongPressed(getAdapterPosition(), v, true);
                }
                return true;
            }
        });
    }

    public CandidateDisplayHolder(Context context, View view) {
        super(view);
        this.cddName         = (TextView) view.findViewById(R.id.assignedCddText);
        this.cddRegNum       = (TextView) view.findViewById(R.id.assignedRegNumText);
        this.cddProgramme    = (TextView) view.findViewById(R.id.assignedPrgText);
        this.cddPaperCode    = (TextView) view.findViewById(R.id.assignedPaperText);
        this.cddTable        = (TextView) view.findViewById(R.id.assignedTableText);
        this.cddLateTag      = (ImageView)view.findViewById(R.id.assignedLateTag);

        cddTable.setTypeface(Typeface.createFromAsset(context.getAssets(), ConfigManager.THICK_FONT));
        cddName.setTypeface(Typeface.createFromAsset(context.getAssets(), ConfigManager.BOLD_FONT));
        cddProgramme.setTypeface(Typeface.createFromAsset(context.getAssets(), ConfigManager.DEFAULT_FONT));
        cddPaperCode.setTypeface(Typeface.createFromAsset(context.getAssets(), ConfigManager.DEFAULT_FONT));
    }

    public void setCddName(String cddName) {
        this.cddName.setText(cddName);
    }

    public void setCddRegNum(String cddRegNum) {
        this.cddRegNum.setText(cddRegNum);
    }

    public void setCddPaperCode(String cddPaperCode) {
        this.cddPaperCode.setText(cddPaperCode);
    }

    public void setCddProgramme(String cddProgramme) {
        this.cddProgramme.setText(cddProgramme);
    }

    public void setCddTable(Integer cddTable) {
        if(cddTable != 0){
            this.cddTable.setBackgroundResource(R.drawable.rounded_table);
            this.cddTable.setText(cddTable.toString());
        } else {
            this.cddTable.setText("");
        }
    }

    public void setCddLateTag(boolean showTag) {
        if(showTag){
            this.cddLateTag.setVisibility(View.VISIBLE);
        } else {
            this.cddLateTag.setVisibility(View.INVISIBLE);
        }
    }

    public interface OnLongPressed {
        void onLongPressed(int position, View view, boolean toggleToTrue);
    }
}
