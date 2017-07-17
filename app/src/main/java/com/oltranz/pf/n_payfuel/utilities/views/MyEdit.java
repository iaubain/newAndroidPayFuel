package com.oltranz.pf.n_payfuel.utilities.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.oltranz.pf.n_payfuel.utilities.AppFont;

/**
 * Created by Hp on 6/8/2017.
 */

public class MyEdit extends EditText {
    public MyEdit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyEdit(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(AppFont.provide(getContext()));
        }
    }
}