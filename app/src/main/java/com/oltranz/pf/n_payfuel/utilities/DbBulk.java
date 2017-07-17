package com.oltranz.pf.n_payfuel.utilities;

import com.oltranz.pf.n_payfuel.config.AppFlow;
import com.oltranz.pf.n_payfuel.config.SessionStatusConfig;
import com.oltranz.pf.n_payfuel.entities.MDevice;
import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MPump;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.entities.MUser;
import com.orm.util.NamingHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/9/2017.
 */

public class DbBulk {
    public static boolean isDeviceRegistered(){
        try {
            return MDevice.count(MDevice.class) > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static MDevice getDevice(){
        List<MDevice> mDevices = MDevice.listAll(MDevice.class);
        return mDevices == null ? null : mDevices.get(0);
    }

    public static List<MUser> getActiveUser(){
        try {
            return MUser.find(MUser.class, NamingHelper.toSQLNameDefault("sessionStatus")+
                    " = ? or "+NamingHelper.toSQLNameDefault("sessionStatus")+
                    " = ? or "+NamingHelper.toSQLNameDefault("sessionStatus")+" = ? ",
                    SessionStatusConfig.ACTIVE,
                    SessionStatusConfig.IDLE,
                    SessionStatusConfig.STARTING);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static MUser getLastLogin() throws Exception{
        return MUser.findWithQuery(MUser.class, "SELECT * FROM "+NamingHelper.toSQLName(MUser.class)+" WHERE "+
                NamingHelper.toSQLNameDefault("sessionStatus") +" = ?  ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC LIMIT 1",
                SessionStatusConfig.STARTING).get(0);
    }

    /*

     */

    public static MUser getUser(String userId){
        List<MUser> mUsers = MUser.findWithQuery(MUser.class, "SELECT * FROM "+NamingHelper.toSQLName(MUser.class)+" WHERE "+
                        NamingHelper.toSQLNameDefault("userId") +" = ?  ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC LIMIT 1",
                userId);
        return mUsers.isEmpty() ? null : mUsers.get(0);
    }

    public static MPump savePump(MPump mPump){
        try {
            List<MPump> myPumps = MPump.findWithQuery(MPump.class, "SELECT * FROM "+NamingHelper.toSQLName(MPump.class)+" WHERE "+
                    NamingHelper.toSQLNameDefault("pumpId") +" = ? ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC LIMIT 1",
                    mPump.getPumpId());
            if(myPumps.isEmpty()){
                long persistResult = mPump.save();
                if(persistResult > 0)
                    return MPump.findById(MPump.class, persistResult);
                else
                    return null;
            }
            return myPumps.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static MNozzle saveNozzle(MNozzle mNozzle){
        try {
            List<MNozzle> myNozzles = MNozzle.findWithQuery(MNozzle.class, "SELECT * FROM "+NamingHelper.toSQLName(MNozzle.class)+" WHERE "+
                            NamingHelper.toSQLNameDefault("nozzleId") +" = ? ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC LIMIT 1",
                    mNozzle.getNozzleId());
            if(myNozzles.isEmpty()){
                long persistResult = mNozzle.save();
                if(persistResult > 0)
                    return MNozzle.findById(MNozzle.class, persistResult);
                else
                    return null;
            }
            return myNozzles.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static MNozzle getNozzle(String nozzleId){
        List<MNozzle> mNozzleList = MNozzle.findWithQuery(MNozzle.class, "SELECT * FROM "+NamingHelper.toSQLName(MNozzle.class)+" WHERE "+
                        NamingHelper.toSQLNameDefault("nozzleId") +" = ?  ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC LIMIT 1",
                nozzleId);
        return mNozzleList.isEmpty() ? null : mNozzleList.get(0);
    }

    public static MSales getTransaction(String transactionId){
        List<MSales> mSalesList = MSales.findWithQuery(MSales.class, "SELECT * FROM "+NamingHelper.toSQLName(MSales.class)+" WHERE "+
                        NamingHelper.toSQLNameDefault("deviceTransactionId") +" = ?  ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC LIMIT 1",
                transactionId);
        return mSalesList.isEmpty() ? null : mSalesList.get(0);
    }
    public static List<MSales> getUserSalesHistory(String userId){
        List<MSales> mSalesList = MSales.findWithQuery(MSales.class, "SELECT * FROM "+NamingHelper.toSQLName(MSales.class)+" WHERE "+
                        NamingHelper.toSQLNameDefault("userId") +" = ?  ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC",
                userId);
        return mSalesList;
    }

    public static List<MSales> getUserSalesReport(String userId, String startDate, String endDate){
        List<MSales> tempSales = new ArrayList<>();
        try {
            List<MSales> mSalesList = MSales.findWithQuery(MSales.class, "SELECT * FROM "+NamingHelper.toSQLName(MSales.class)+" WHERE "+
                            NamingHelper.toSQLNameDefault("userId") +" = ?  ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC",
                    userId);
            if(mSalesList.isEmpty())
                return mSalesList;

            DateFormat dFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
            for(MSales mSales : mSalesList){
                if(dFormat.parse(mSales.getDeviceTransactionTime()).getTime() >= dFormat.parse(startDate).getTime() &&
                        dFormat.parse(mSales.getDeviceTransactionTime()).getTime() <= dFormat.parse(endDate).getTime())
                    tempSales.add(mSales);
            }
            return tempSales;
        }catch (Exception e){
            e.printStackTrace();
            return tempSales;
        }

//        List<MSales> mSalesList = MSales.findWithQuery(MSales.class, "SELECT * FROM "+NamingHelper.toSQLName(MSales.class)+" WHERE "+
//                        NamingHelper.toSQLNameDefault("userId") +" = ? AND ( "+
//                        NamingHelper.toSQLNameDefault("deviceTransactionTime")+" >= date(?) AND "+
//                        NamingHelper.toSQLNameDefault("deviceTransactionTime")+" <= date(?)) ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC",
//                userId, startDate, endDate);
    }

    public static List<MSales> getPendingTransaction(){
        return MSales.findWithQuery(MSales.class, "SELECT * FROM "+NamingHelper.toSQLName(MSales.class)+
                " WHERE ("+
                        NamingHelper.toSQLNameDefault("status") +" != 100 AND "+
                        NamingHelper.toSQLNameDefault("status")+" != 101) AND ("+
                        NamingHelper.toSQLNameDefault("status")+" != 500) ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC");
    }

    public static MPayment getPayment(String paymentId){
        List<MPayment> mPaymentList = MPayment.findWithQuery(MPayment.class, "SELECT * FROM "+NamingHelper.toSQLName(MPayment.class)+" WHERE "+
                        NamingHelper.toSQLNameDefault("paymentModeId") +" = ?  ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC LIMIT 1",
                paymentId);
        return mPaymentList.isEmpty() ? null : mPaymentList.get(0);
    }

    public static void deleteOldTransaction(){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, (-1* AppFlow.MAX_HISTORY));
            DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
            MSales.deleteAll(MSales.class,NamingHelper.toSQLNameDefault("deviceTransactionTime")+" <= Datetime(?) ",dateFormat.format(calendar.getTime()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void resetPump()throws Exception{
        MNozzle.deleteAll(MNozzle.class);
        MNozzle.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + NamingHelper.toSQLName(MNozzle.class) + "'");
        MPump.deleteAll(MPump.class);
        MPump.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + NamingHelper.toSQLName(MPump.class) + "'");
    }

    public static void resetData() throws Exception{
        MUser.deleteAll(MUser.class);
        MUser.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + NamingHelper.toSQLName(MUser.class) + "'");
        MPayment.deleteAll(MPayment.class);
        MPayment.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + NamingHelper.toSQLName(MPayment.class) + "'");
        MNozzle.deleteAll(MNozzle.class);
        MNozzle.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + NamingHelper.toSQLName(MNozzle.class) + "'");
        MPump.deleteAll(MPump.class);
        MPump.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + NamingHelper.toSQLName(MPump.class) + "'");
    }
}
