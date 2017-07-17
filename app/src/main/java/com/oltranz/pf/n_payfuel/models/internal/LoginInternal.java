package com.oltranz.pf.n_payfuel.models.internal;

import android.support.v4.app.Fragment;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/9/2017.
 */

public class LoginInternal {
    private boolean goTo;
    private Fragment fragment;

    public LoginInternal() {
    }

    public LoginInternal(boolean goTo, Fragment fragment) {
        this.setGoTo(goTo);
        this.setFragment(fragment);
    }

    public boolean isGoTo() {
        return goTo;
    }

    public void setGoTo(boolean goTo) {
        this.goTo = goTo;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
