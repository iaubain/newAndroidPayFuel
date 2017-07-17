package com.oltranz.pf.n_payfuel.utilities;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Hp on 6/7/2017.
 */

public final class AppFont {
    public static Typeface provide(Context context){
        return Typeface.createFromAsset(context.getAssets(), "font/Ubuntu.ttf");
    }
}
