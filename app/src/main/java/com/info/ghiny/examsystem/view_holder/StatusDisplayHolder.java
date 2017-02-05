package com.info.ghiny.examsystem.view_holder;

import android.view.View;
import android.widget.TextView;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.Status;

/**
 * Created by FOONG on 5/2/2017.
 */

public class StatusDisplayHolder {

    private TextView statusView;
    private TextView quantityView;
    private Status status;
    private Integer quantity;

    public StatusDisplayHolder(Status status, Integer quantity){
        this.status     = status;
        this.quantity   = quantity;
    }

    public void setView(View parentView){
        this.statusView     = (TextView) parentView.findViewById(R.id.statusView);
        this.quantityView   = (TextView) parentView.findViewById(R.id.statusCount);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.quantityView.setText(quantity.toString());
    }

    public void setStatus(Status status) {
        this.status = status;
        this.statusView.setText(status.toString());
    }

    public int getQuantity() {
        return quantity;
    }

    public Status getStatus() {
        return status;
    }
}
