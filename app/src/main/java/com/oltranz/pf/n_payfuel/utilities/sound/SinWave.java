package com.oltranz.pf.n_payfuel.utilities.sound;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/30/2017.
 */
public class SinWave {
    public static final int HEIGHT = 127;
    public static final double TWOPI = 2 * 3.1415;

    /**
     *
     * @param wave
     * @param waveLen
     * @param length
     * @return
     */
    public static byte[] sin(byte[] wave, int waveLen, int length) {
        for (int i = 0; i < length; i++) {
            wave[i] = (byte) (HEIGHT * (1 - Math.sin(TWOPI * ((i % waveLen) * 1.00 / waveLen))));
        }
        return wave;
    }
}

