package com.oltranz.pf.n_payfuel.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.models.register.RegisterRequest;
import com.oltranz.pf.n_payfuel.utilities.DataFactory;
import com.oltranz.pf.n_payfuel.utilities.loaders.RegisterLoader;
import com.oltranz.pf.n_payfuel.utilities.views.MyButton;
import com.oltranz.pf.n_payfuel.utilities.views.MyEdit;
import com.oltranz.pf.n_payfuel.utilities.views.MyLabel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRegisterInteraction} interface
 * to handle interaction events.
 * Use the {@link Register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Register extends Fragment implements RegisterLoader.RegisterLoaderInteraction {

    private static int pwdId = 12;
    @BindView(R.id.email)
    MyEdit email;
    @BindView(R.id.devName)
    MyEdit devName;
    @BindView(R.id.submit)
    MyButton submit;
    private OnRegisterInteraction mListener;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

    public Register() {
        // Required empty public constructor
    }

    public static Register newInstance() {
        return new Register();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.register_layout, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DataFactory.isValidEmail(email.getText().toString())){
                    email.requestFocus();
                    email.setError("Invalid mail");
                    return;
                }

                if(devName.getText().toString().length() < 3){
                    devName.requestFocus();
                    devName.setError("Invalid device name");
                    return;
                }
                confirmBox(devName.getText().toString(), email.getText().toString());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterInteraction) {
            mListener = (OnRegisterInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void confirmBox(final String devNameValue, final String adminMail){
        try {
            final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.confirm_box);

            final MyLabel boxTitle = (MyLabel) dialog.findViewById(R.id.dialogTitle);
            ImageView close = (ImageView) dialog.findViewById(R.id.icClose);
            MyButton cancel = (MyButton) dialog.findViewById(R.id.cancel);
            MyButton ok = (MyButton) dialog.findViewById(R.id.ok);
            LinearLayout boxContent = (LinearLayout) dialog.findViewById(R.id.dialogContent);

            int fieldId = View.generateViewId();
            final MyEdit myEdit = new MyEdit(getContext());

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            boxTitle.setText("Confirm registration");

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //proceed with other validations
                    if(TextUtils.isEmpty(myEdit.getText().toString()) || myEdit.getText().toString().length() < 3){
                        myEdit.requestFocus();
                        myEdit.setError("Invalid password");
                    }else{
                        RegisterRequest registerRequest = new RegisterRequest(adminMail, myEdit.getText().toString(), devNameValue, Build.SERIAL);
                        mProgress("Registering...");
                        RegisterLoader registerLoader = new RegisterLoader(Register.this, getContext(), registerRequest);
                        registerLoader.startLoading();
                        dialog.dismiss();
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            //setting box content

            MyLabel lbl = new MyLabel(getContext());
            lbl.setText("Device Name: ");
            lbl.setTextSize(21);
            lbl.setId(View.generateViewId());
            lbl.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            boxContent.addView(lbl);

            MyLabel lblValue = new MyLabel(getContext());
            lblValue.setText(devNameValue+"\n");
            lblValue.setTextSize(27);
            lblValue.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            lblValue.setId(View.generateViewId());
            lblValue.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            boxContent.addView(lblValue);

            lbl = new MyLabel(getContext());
            lbl.setText("Admin mail: ");
            lbl.setTextSize(21);
            lbl.setId(View.generateViewId());
            lbl.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            boxContent.addView(lbl);

            lblValue = new MyLabel(getContext());
            lblValue.setText(adminMail+"\n");
            lblValue.setTextSize(27);
            lblValue.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            lblValue.setId(View.generateViewId());
            lblValue.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            boxContent.addView(lblValue);

            myEdit.setHint("Provide Password");
            myEdit.setId(fieldId);
            myEdit.setTextSize(27);
            myEdit.setGravity(Gravity.CENTER);
            myEdit.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            myEdit.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            myEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                    InputType.TYPE_CLASS_TEXT);

            TextInputLayout textInputLayout = new TextInputLayout(getContext());
            textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textInputLayout.setPaddingRelative(6, 0 , 6, 3);
            textInputLayout.addView(myEdit);
            boxContent.addView(textInputLayout);


            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void popBox(String message){
        try {
            builder = new AlertDialog.Builder(getContext(), R.style.SimpleDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    private void mProgress(String message){
        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(message != null ? message : "NO_MESSAGE");
        progressDialog.show();
    }

    private void dismissProgress(){
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.dismiss();
    }

    private void updateMessage(String message){
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.setMessage(message != null ? message : "NO_MESSAGE");
    }

    @Override
    public void onRegisterLoader(boolean isRegistered, Object object) {
        dismissProgress();
        if(!isRegistered){
            popBox((String) object);
            return;
        }
        //Continue with other chain activities
        devName.setText("");
        email.setText("");
        popBox("Successful register");
    }

    public interface OnRegisterInteraction {
        void onRegister(Object object);
    }
}
