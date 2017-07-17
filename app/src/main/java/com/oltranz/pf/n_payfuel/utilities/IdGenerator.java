package com.oltranz.pf.n_payfuel.utilities;

import java.util.Date;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/19/2017.
 */

public class IdGenerator {
    public static long GEN(String userId){
        long currentDate = new Date().getTime();
        String combined = userId+currentDate;
        return Long.parseLong(combined);
    }
}
