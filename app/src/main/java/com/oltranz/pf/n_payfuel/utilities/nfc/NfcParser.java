package com.oltranz.pf.n_payfuel.utilities.nfc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/30/2017.
 */

public class NfcParser {
    public static byte[] marshall(Parcelable parceable) {
        Parcel parcel = Parcel.obtain();
        parceable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // This is extremely important!
        return parcel;
    }

    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }
    public static final String byte2String(byte[] input){
        String str = "";
        try {
            for(byte aux : input) {
                int b = aux & 0xff;
                if (Integer.toHexString(b).length() == 1) str += "0";
                str += Integer.toHexString(b);
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public static String toHexString(String s)
    {
        String str="";
        for (int i=0;i<s.length();i++)
        {
            int ch = (int)s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    public static final String intToString(int arg)
    {
        String str="", strTemp="";
        int temp;

        temp = arg &0xff;
        if(temp <= 0xf){
            strTemp = "0";
            strTemp += Integer.toHexString(arg & 0xff);
        }else {
            strTemp = Integer.toHexString(arg & 0xff);
        }
        str = str+strTemp;

        return str;
    }

    public static final String byteToHex(byte[] input){
        StringBuilder sb = new StringBuilder();
        for (byte b : input) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

public static final String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }
        return sb.toString();
    }

        private String byteToString(byte[] arg, int length)
    {
        String str="", strTemp="";
        int temp;
        for(int i=0;i<length;i++) {
            temp = (int)arg[i] & 0xff;
            if(temp <= 0xf){
                strTemp = "0";
                strTemp += Integer.toHexString(arg[i] & 0xff);
            }else {
                strTemp = Integer.toHexString(arg[i] & 0xff);
            }
            str = str+strTemp;
        }
        return str;
    }}
