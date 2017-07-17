package com.oltranz.pf.n_payfuel.utilities.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

public class ChoosePumpAdapter extends RecyclerView.Adapter<ChoosePumpAdapter.MyViewHolder> {
    private OnChoosePumpInteraction mListener;
    private Context context;
    private List<PumpModel> mPumps;

    public ChoosePumpAdapter(OnChoosePumpInteraction mListener, Context context, List<PumpModel> mPumps) {
        this.mListener = mListener;
        this.context = context;
        this.mPumps = mPumps;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pump_style, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final PumpModel pumpModel = mPumps.get(position);
        holder.pumpName.setText(pumpModel.getPumpName());
        holder.pumpImg.setClickable(true);

        if(pumpModel.getStatus().equals("8")){
            holder.pumpImg.setClickable(false);
            holder.pumpImg.setEnabled(false);
            holder.pumpImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pump_taken));
        }else if (pumpModel.getStatus().equals("7")){
            holder.pumpImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pump_default));
            for(NozzleModel nozzleModel : pumpModel.getNozzleList()){
                if(nozzleModel.getStatus().equals("8"))
                    holder.pumpImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pump_taken));
                else if (nozzleModel.getStatus().equals("9"))
                    holder.pumpImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pump_selected));
            }
        }else{
            holder.pumpImg.setClickable(false);
            holder.pumpImg.setEnabled(false);
            holder.pumpImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pump_default));
        }

        holder.pumpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onChoosePump(false, pumpModel);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildMenu(view, pumpModel);
            }
        });
    }

    public int getViewPosition (PumpModel pumpModel){
        //myRecyclerView.findViewHolderForAdapterPosition(pos);
        return mPumps.indexOf(pumpModel);
    }

    public void changePumpIcon(int position){

    }

    @Override
    public int getItemCount() {
        return mPumps.size();
    }

    private void buildMenu(View view, PumpModel pumpModel){
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.pump_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(pumpModel));
        popup.show();
    }

    public interface OnChoosePumpInteraction{
        void onChoosePump(boolean isDetails, PumpModel pumpModel);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pumpName)
        MyLabel pumpName;
        @BindView(R.id.pumpImg)
        ImageView pumpImg;
        @BindView(R.id.overflow)
        ImageView overflow;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        PumpModel pumpModel;

        MyMenuItemClickListener(PumpModel pumpModel) {
            this.pumpModel=pumpModel;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.pumpDetails:
                    mListener.onChoosePump(true, pumpModel);
                    return true;
                default:
            }
            return false;
        }
    }
}
