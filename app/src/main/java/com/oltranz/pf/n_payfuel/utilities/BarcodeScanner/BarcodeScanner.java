package com.oltranz.pf.n_payfuel.utilities.BarcodeScanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.oltranz.pf.n_payfuel.utilities.sound.AudioTrackManager;

import java.io.IOException;

import justtide.BarcodeReader;
import justtide.BarcodeReader.BarcodeResult;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 7/6/2017.
 */

public class BarcodeScanner {
    protected static final int REFRESH = 0;
    protected static final int REFRESHBUTTON = 1;
    protected static final int READER_FAILS = -1;
    protected static final String TAG = "BarcCode";
    boolean first=true;
    private OnBarcodeScan mListener;
    private Context context;
    private Handler mHandler = null;
    private Thread mThread=null;
    private String decodeString = "";
    private BarcodeResult barcodeResult;
    private BarcodeReader barcodeReader = BarcodeReader.getInstance();
    private AudioTrackManager audio= new AudioTrackManager();

    public BarcodeScanner(OnBarcodeScan mListener, Context context) {
        this.mListener = mListener;
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    public void startReading () {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == REFRESH) {
                    if(barcodeResult.getResult()<0) {
                        String test = barcodeResult.getText();//"Get Timeout");
                        Log.d("BarCode","Reading results: "+test);
                        mListener.onBarcode(true, test);
                    } else {
                        decodeString = barcodeResult.getText();
                        Log.d("BarCode","Reading results: "+decodeString);
                        audio.start(3000);
                        audio.play();
                        audio.stop();
                        mListener.onBarcode(true, decodeString);
                    }
                    //decodeString = "";
                } else if (msg.what == REFRESHBUTTON){
                    //showStateText.setText("");
                } else if(msg.what == READER_FAILS){
                    Log.d("BarCode","Reading results: "+decodeString);
                    audio.start(3000);
                    audio.play();
                    audio.stop();
                    mListener.onBarcode(false, "Failed to read the code");
                }
                super.handleMessage(msg);
            }
        };
        try {
            barcodeReader.open();
            switch (barcodeReader.getState()){
                case 0:
                    mListener.onBarcode(false, "No barcode scanner found");
                    return;
                case 1:
                    barcodeReader.open();
            }
        } catch (IOException e) {
            e.printStackTrace();
            mListener.onBarcode(false, "Barcode reader failed");
            return;
        }
        codescan();
    }

    private void codescan() {
        if (mThread == null)
        {
            mThread = new MyThread();
            mThread.start();

        }

    }

    public interface OnBarcodeScan{
        void onBarcode(boolean isDone, String barcodeValue);
    }

    public class MyThread extends Thread {
        Message msg = new Message();
        @Override
        public void run() {

            Log.i(TAG , "scan begin");
            try {
                barcodeReader.delay(50); //delay time Nx100mm MAX：100
                barcodeReader.setAimingPattern(2);
                barcodeReader.setIllumination(1);
                barcodeResult = barcodeReader.scan(); //scan
            } catch (IOException e) {
                e.printStackTrace();
                msg.what = READER_FAILS;
                mHandler.sendMessage(msg);
                mThread = null;
                return;
            }
            Log.i(TAG , "scan end");
            msg.what = REFRESH;
            mHandler.sendMessage(msg);
            mThread = null;
            //barcodeReader.stop();

            try {
                barcodeReader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
