package com.oltranz.pf.n_payfuel.utilities.loaders;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.entities.MUser;
import com.oltranz.pf.n_payfuel.models.login.LoginResponse;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.views.MyButton;
import com.oltranz.pf.n_payfuel.utilities.views.MyEdit;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/30/2017.
 */

public class ResumeLoader {
    private OnResumeLoader mListener;
    private Context context;
    private LoginResponse loginResponse;

    public ResumeLoader(OnResumeLoader mListener, Context context, LoginResponse loginResponse) {
        this.mListener = mListener;
        this.context = context;
        this.loginResponse = loginResponse;
    }

    public void lockDevice(){
        try {
            final Dialog resumeDialog = new Dialog(context);
            resumeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            resumeDialog.setContentView(R.layout.resume_shift);
            resumeDialog.setCancelable(false);
            resumeDialog.setCanceledOnTouchOutside(false);

            final MyButton ok = (MyButton) resumeDialog.findViewById(R.id.ok);
            final MyEdit pin = (MyEdit) resumeDialog.findViewById(R.id.pin);
            final MyLabel info = (MyLabel) resumeDialog.findViewById(R.id.info);

            info.setText("Device locked by: "+loginResponse.getmUser().getName());

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(TextUtils.isEmpty(pin.getText().toString()))
                        pin.setError("Invalid PIN");
                    else{
                        try {
                            MUser mUser = DbBulk.getLastLogin();
                            if(mUser.getUserPin().equals(pin.getText().toString())) {
                                resumeDialog.dismiss();
                                mListener.onResume(true, "Unlocked");
                            }else
                                Toast.makeText(context, "Access denied", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error with local database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            resumeDialog.show();

        } catch (Exception e) {
            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            mListener.onResume(false, "Can't lock device now");
        }
    }

    public interface OnResumeLoader{
        void onResume(boolean isDone, Object object);
    }
}
