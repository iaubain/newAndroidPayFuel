package com.oltranz.pf.n_payfuel.utilities.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.models.pump.NozzleModel;
import com.oltranz.pf.n_payfuel.models.pump.PumpModel;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/15/2017.
 */

public class ChooseNozzleAdapter extends Adapter<ChooseNozzleAdapter.MyViewHolder> {
    private OnChooseNozzleInteraction mListener;
    private Context context;
    private List<NozzleModel> mNozzles;
    private PumpModel pumpModel;

    public ChooseNozzleAdapter(OnChooseNozzleInteraction mListener, Context context, List<NozzleModel> mNozzles, PumpModel pumpModel) {
        this.mListener = mListener;
        this.context = context;
        this.mNozzles = mNozzles;
        this.pumpModel = pumpModel;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nozzle_style, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final NozzleModel nozzleModel = mNozzles.get(position);
        holder.nozzleName.setText(nozzleModel.getNozzleName());
        holder.nozzleIndex.setText(nozzleModel.getIndexCount());

        if(nozzleModel.getStatus().equals("8")){
            holder.nozzleImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nozzle_taken));
            holder.checkBox.setEnabled(false);
            holder.nozzleName.setText(nozzleModel.getNozzleName() +" taken by, "+ (nozzleModel.getUserName() != null ? nozzleModel.getUserName() : "NO_NAME"));
        }else if (nozzleModel.getStatus().equals("7")){
            holder.nozzleImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nozzle_default));
        }else{
            holder.nozzleImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nozzle_selected));
            holder.checkBox.setChecked(true);
        }

        holder.nozzleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nozzleModel.getStatus().equals("8")){
                if(!holder.checkBox.isChecked()){
                    mListener.onChooseNozzle(true, pumpModel, nozzleModel);
                    holder.checkBox.setChecked(true);
                    holder.nozzleImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nozzle_selected));
                    nozzleModel.setStatus("9");
                }else{
                    mListener.onChooseNozzle(false, pumpModel, nozzleModel);
                    holder.nozzleImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nozzle_default));
                    nozzleModel.setStatus("7");
                    holder.checkBox.setChecked(false);
                }
                }else{
                    Toast.makeText(context, "Taken by: "+(nozzleModel.getUserName() != null ? nozzleModel.getUserName() : "NO_NAME"), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.nozzleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nozzleModel.getStatus().equals("8")){
                if(!holder.checkBox.isChecked()){
                    mListener.onChooseNozzle(true, pumpModel, nozzleModel);
                    holder.checkBox.setChecked(true);
                    holder.nozzleImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nozzle_selected));
                    nozzleModel.setStatus("9");
                }else{
                    mListener.onChooseNozzle(false, pumpModel, nozzleModel);
                    holder.nozzleImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nozzle_default));
                    nozzleModel.setStatus("7");
                    holder.checkBox.setChecked(false);
                }
                }else{
                    Toast.makeText(context, "Taken by: "+(nozzleModel.getUserName() != null ? nozzleModel.getUserName() : "NO_NAME"), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.checkBox.isChecked()){
                    mListener.onChooseNozzle(false, pumpModel, nozzleModel);
                    holder.nozzleImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nozzle_default));
                    nozzleModel.setStatus("7");
                }else{
                    mListener.onChooseNozzle(true, pumpModel, nozzleModel);
                    holder.checkBox.setChecked(true);
                    holder.nozzleImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nozzle_selected));
                    nozzleModel.setStatus("9");
                }
            }
        });

    }

    public void refreshAdapter(){
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mNozzles.size();
    }


    public interface OnChooseNozzleInteraction {
        void onChooseNozzle(boolean isAdd, PumpModel pumpModel, NozzleModel nozzleModel);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nozzleName)
        MyLabel nozzleName;
        @BindView(R.id.nozzleIndex)
        MyLabel nozzleIndex;
        @BindView(R.id.pumpImg)
        ImageView nozzleImg;
        @BindView(R.id.checkBox)
        CheckBox checkBox;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
