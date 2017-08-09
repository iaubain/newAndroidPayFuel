package com.oltranz.pf.n_payfuel.utilities.nfc;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.oltranz.pf.n_payfuel.utilities.sound.AudioTrackManager;

import java.io.IOException;

import justtide.CommandApdu;
import justtide.ContactlessCard;
import justtide.PiccException;
import justtide.PiccInterface;
import justtide.PiccReader;
import justtide.ResponseApdu;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 7/6/2017.
 */

public class NfcReader implements PiccInterface {
    protected static final String TAG = "PcciReader";
    protected static final int CHECK_SUCCESS = 0;
    protected static final int RESPONSE_APDU = 1;
    protected static final int OPEN_FAIL = 2;
    protected static final int REFRESH = 3;
    protected static final int TIME_OUT = 4;
    protected static final int USER_CANCEL = 5;
    protected static final int REFRESH_EXC = 6;
    protected static final int REFRESH_M = 7;
    private static byte[] apdu = {0x00, (byte)0xA4, 0x00, 0x00, 0x02, 0x3f, 0x00,(byte)0xff};
    public Handler mHandler;
    private OnNfcDraftInteraction mListener;
    private Thread mThread;
    private String stateString = "";
    private String dataShowString = "";
    private String strResponseApdu = "";
    private String mPayLoad = "";
    private String strPiccException = "";
    private ContactlessCard contactlessCard ;
    private ResponseApdu responseApdu ;
    private CommandApdu commandApdu;
    private AudioTrackManager audio ;
    private PiccReader piccReader = PiccReader.getInstance();

    public NfcReader(OnNfcDraftInteraction mListener) {
        this.mListener = mListener;
    }

    @SuppressLint("HandlerLeak")
    public void startReading() {
        audio = new AudioTrackManager();
        responseApdu = null;
        contactlessCard = null;
        commandApdu = new CommandApdu(apdu);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if(msg.what == CHECK_SUCCESS) {
                    audio.start(3000);
                    audio.play();
                    for(int i = 0; i<10; i++){
                    }
                    audio.stop();
                    String strCardType = "";
                    byte[] serialNo = contactlessCard.getSerialNo();
                    String strSerialNo = NfcParser.byte2String(serialNo);
                    Log.i(TAG,"serialNo:"+strSerialNo);
                    int state = contactlessCard.getState();
                    Log.i(TAG,"state:"+state);
                    byte type = contactlessCard.getType();
                    if(type == ContactlessCard.TYPE_A){
                        strCardType = "A";
                    } else if (type == ContactlessCard.TYPE_B) {
                        strCardType = "B";
                    } else if (type == ContactlessCard.TYPE_MIFARE) {
                        strCardType = "M";
                    } else
                        strCardType = "Unkown";
                    Log.i(TAG,"type:"+strCardType);
                    dataShowString ="type:" + strCardType + "\n" + "serialNo:" + strSerialNo + "\n" + "state:" + state + "\n";
                    //showStateText.setText(stateString);
                    Log.d("My Tag", dataShowString);
                    mListener.onNfcDraft(true, new NfcCardData(strSerialNo, mPayLoad, null));
                }else if (msg.what == RESPONSE_APDU)	{
                    //snShowText.setText(snShowString);
                    audio.start(3000);
                    audio.play();
                    for(int i = 0; i<10; i++){
                    }
                    audio.stop();
                    byte[] responseData = responseApdu.getData();
                    int iSW1  = responseApdu.getSW1();
                    int iSW2 = responseApdu.getSW2();
                    //   Integer.toHexString()

                    strResponseApdu = "Send:" + NfcParser.byte2String(apdu);
                    strResponseApdu += "\n" +"Resp:";
                    strResponseApdu += NfcParser.byte2String(responseData) +"\n";
                    strResponseApdu += "SW:" +NfcParser.intToString(iSW1)+" "+NfcParser.intToString(iSW2);
                    mListener.onNfcDraft(false, "Not supported yet");
                }else if(msg.what == OPEN_FAIL)	{
                    mListener.onNfcDraft(false, "Fails to open");
                } else if (msg.what == REFRESH) {
                    mListener.onNfcDraft(false, "Refreshing");
                } else if (msg.what == USER_CANCEL) {
                    mListener.onNfcDraft(false, "User cancel");
                } else if (msg.what == TIME_OUT) {
                    mListener.onNfcDraft(false, "Request timeout");
                } else if (msg.what == REFRESH_EXC){
                    mListener.onNfcDraft(false, "Refreshing Exc");
                } else if(msg.what == REFRESH_M){
                    byte[] serialNo = contactlessCard.getSerialNo();
                    //mListener.onNfcDraft(true, new NfcCardData(NfcParser.byte2String(serialNo), mPayLoad, null));
                }
                super.handleMessage(msg);
            }
        };

        if (mThread == null)
        {
            try {
                piccReader.open();
            } catch (IOException e) {
                strPiccException = e.getMessage();
                Log.e("PcciReader","open IOException:"+strPiccException);

                e.printStackTrace();
            } catch (PiccException e) {
                e.printStackTrace();
                strPiccException = e.getMessage();
                Log.e("PcciReader","open Exception:"+strPiccException);
            }
            mThread = new MyThread();
            mThread.start();
        }
    }

    @Override
    public void getContactlessCard(int arg0, ContactlessCard arg1) {
        if(arg0 == 0) {
            stateString = "Successful";
            this.contactlessCard = arg1;
            Log.i(TAG,"Successful");
            Message msg = new Message();
            msg.what = CHECK_SUCCESS;
            mHandler.sendMessage(msg);
        } else if(arg0 == PiccReader.TIMEOUT_ERROR) {
            stateString = "Time out";
            Log.e(TAG,"Time out");
            Message msg = new Message();
            msg.what = TIME_OUT;
            mHandler.sendMessage(msg);
        } else if(arg0 == PiccReader.USER_CANCEL) {
            stateString = "User cancel";
            Log.e(TAG,"User cancel");
            Message msg = new Message();
            msg.what = USER_CANCEL;
            mHandler.sendMessage(msg);
        }
    }
    public interface OnNfcDraftInteraction{
        void onNfcDraft(boolean isDone, Object nfcData);
    }

    public class MyThread extends Thread {
        public void run() {
            System.out.println("Open PICC....");

            Log.i("PcciReader","++++begin search++++");
            try {
                piccReader.search(ContactlessCard.TYPE_UNKOWN, 10000, NfcReader.this);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (PiccException e1) {
                e1.printStackTrace();
                strPiccException = e1.getMessage();
                Log.e("PcciReader","search Exception:"+strPiccException);
                stateString = strPiccException;
                Message msg1 = new Message();
                msg1.what = REFRESH_EXC;
                mHandler.sendMessage(msg1);
            }
            Log.i("PcciReader","++++End search++++");
            if(contactlessCard!=null) {
                Log.i("PcciReader","++++begin A card reset++++");
                if(contactlessCard.getType()  == ContactlessCard.TYPE_A){
                    try {
                        piccReader.reset(contactlessCard);

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (PiccException e1) {
                        e1.printStackTrace();
                        strPiccException = e1.getMessage();
                        Log.e("PcciReader","reset Exception:"+strPiccException);
                    }
                }


                Log.i("PcciReader","++++begin A B Card transmit++++");
                if(contactlessCard.getType()  == ContactlessCard.TYPE_A || contactlessCard.getType()  == ContactlessCard.TYPE_B){

                    try {
                        responseApdu = piccReader.transmit(commandApdu);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (PiccException e) {
                        e.printStackTrace();
                        strPiccException = e.getMessage();
                        Log.e("PcciReader","transmit Exception:"+strPiccException);
                    } finally {
                        if(responseApdu != null) {
                            Message msg = new Message();
                            msg.what = RESPONSE_APDU;
                            mHandler.sendMessage(msg);
                        }


                    }
                }
                if(contactlessCard.getType() == ContactlessCard.TYPE_MIFARE){

                    stateString = "";
                    byte[] pw = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
                    //byte[] pw = {(byte) 0x65,(byte) 0x70,(byte) 0x6c,(byte) 0x69,(byte) 0x6e,(byte) 0x6b};
                    byte[] serialNo = contactlessCard.getSerialNo();
                    byte[] bockValue = new byte[20];
                    byte[] test = new byte[20];
                    int ret = -1;
                    test[0] = (byte) serialNo.length;
                    System.arraycopy(serialNo , 0, test, 1, serialNo.length);
                    Log.v(TAG, "m1Authority begin ");
                    ret = piccReader.m1Authority('a', (char) 0, pw, test);
                    Log.v(TAG, "m1Authority ret = "+ret);
                    if(ret == 0){
                        stateString += "Authority successful \n";
                    } else {
                        stateString += "Authority fail \n";
                    }

                    ret = piccReader.m1ReadBlock((char) 0, bockValue);
                    Log.v(TAG, "m1ReadBlock ret = "+ret);
                    if(ret == 0){
                        mPayLoad = NfcParser.byte2String(bockValue);
                        stateString += "Block value:"
                                + NfcParser.byte2String(bockValue) + "\n";
                    } else {
                        stateString += "Read block fail";
                    }

                    Message msg = new Message();
                    msg.what = REFRESH_M;
                    mHandler.sendMessage(msg);
                }

            }

            try {
                piccReader.close();
                Log.i("PcciReader","picc close");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (PiccException e) {
                e.printStackTrace();
                strPiccException = e.getMessage();
                Log.e("PcciReader","close Exception:"+strPiccException);
            }

            mThread = null;

        }
    }
}
