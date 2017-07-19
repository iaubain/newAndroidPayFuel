package com.oltranz.pf.n_payfuel.utilities.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/19/2017.
 */

public class SalesNozzleAdapter extends Adapter<SalesNozzleAdapter.MyViewHolder> {
    private OnChooseSellingNozzle mListener;
    private Context context;
    private List<MNozzle> mNozzles;

    public SalesNozzleAdapter(OnChooseSellingNozzle mListener, Context context, List<MNozzle> mNozzles) {
        this.mListener = mListener;
        this.context = context;
        this.mNozzles = mNozzles;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sales_nozzle_style, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MNozzle mNozzle = mNozzles.get(position);
        holder.nozzleName.setText(mNozzle.getNozzleName()+" - "+mNozzle.getProductName());
        holder.nozzleIndex.setText(mNozzle.getIndexCount());

        holder.nozzleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onChooseNozzle(true, mNozzle);
            }
        });
    }

    public void refreshAdapter(List<MNozzle> mNozzles){
        this.mNozzles = mNozzles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mNozzles.size();
    }


    public interface OnChooseSellingNozzle {
        void onChooseNozzle(boolean isSelected, MNozzle mNozzle);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nozzleName)
        MyLabel nozzleName;
        @BindView(R.id.indexes)
        MyLabel nozzleIndex;
        @BindView(R.id.nozzleIc)
        ImageView nozzleImg;
        @BindView(R.id.overflow)
        ImageView overFlow;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
