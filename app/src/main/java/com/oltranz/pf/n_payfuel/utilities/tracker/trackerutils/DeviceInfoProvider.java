package com.oltranz.pf.n_payfuel.utilities.tracker.trackerutils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.oltranz.pf.n_payfuel.entities.MDevice;
import com.oltranz.pf.n_payfuel.entities.MSales;
import com.oltranz.pf.n_payfuel.entities.MUser;
import com.oltranz.pf.n_payfuel.utilities.DbBulk;
import com.oltranz.pf.n_payfuel.utilities.DeviceUuidFactory;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackerconfig.AppConfig;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MBattery;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MGps;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MHardware;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MNetwork;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MSoftware;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 8/9/2017.
 */

public class DeviceInfoProvider {
    private Context context;

    public DeviceInfoProvider(Context context) {
        this.context = context;
    }

    public MBattery genBattery() {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent == null)
            return null;
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        String batteryState = isCharging ? usbCharge ? "USB_CHARGER" : acCharge ? "STOCKET_CHARGER" : "OTHER_DOCK" : "NOT_CHARGING";
        String accessDate = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Log.v("Battery", "Battery Percentage: " + batteryPct + " Level: " + level + " Scale: " + scale + " State: " + batteryState + " Access: " + accessDate);
        MBattery mBattery = new MBattery(scale + "", level + "", batteryPct + "", batteryState, accessDate);

        return mBattery;
    }

    public MHardware genHardware() {
        String serialNumber = Build.SERIAL;
        DeviceUuidFactory duf = new DeviceUuidFactory(context);
        serialNumber = serialNumber == null ? String.valueOf(duf.getDeviceUuid()) : serialNumber;
        String brandName = Build.BRAND;
        String manufacturer = Build.MANUFACTURER;
        String osVersion = Build.VERSION.CODENAME;
        String osRelease = Build.VERSION.RELEASE;
        String accessDate = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        MHardware mHardware = new MHardware(serialNumber, osVersion, osRelease, brandName, manufacturer, accessDate);
        Log.v("Hardware", "Hardware manufacturer: " + manufacturer + " Serial Number: " + serialNumber + " OS version: " + osVersion + " OS release: " + osRelease + " Access: " + accessDate);
        return mHardware;
    }

    @SuppressLint("HardwareIds")
    public MNetwork genNetwork() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String countryCode;
        countryCode = tManager.getNetworkCountryIso() != null ? tManager.getNetworkCountryIso() : "def";
        String subscriberId = tManager.getNetworkOperatorName();
        String carrierName = tManager.getNetworkOperatorName();
        String imei = tManager.getDeviceId();
        String networkType = tManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS ? "GPRS" :
                tManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE ? "EDGE" :
                        tManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_CDMA ? "CDMA" :
                                tManager.getNetworkType() == TelephonyManager.PHONE_TYPE_GSM ? "GSM" :
                                        tManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE ? "LTE" :
                                                tManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS ? "UMTS" :
                                                        tManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UNKNOWN ? "UNKNOWN" : "UNKNOWN";
        String simId = tManager.getSimSerialNumber();
        String connectivity = activeNetwork == null ? "CONNECTIVITY_MANAGER_NOT_FOUND" : activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ? "WIFI" :
                activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE ? "SIM_DATA" :
                        activeNetwork.getType() == ConnectivityManager.TYPE_VPN ? "VPN" :
                                activeNetwork.getType() == ConnectivityManager.TYPE_BLUETOOTH ? "BLUETOOTH" :
                                        activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX ? "WIMAX" :
                                                activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE_DUN ? "SIM_DATA_DUN" : "OTHER_NETWORK";
        String accessDate = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        MNetwork mNetwork = new MNetwork(simId, imei, countryCode, subscriberId, networkType, carrierName, connectivity, accessDate);

        Log.v("Network", "Network Subscriber: " + subscriberId + " Carrier Name: " + carrierName + " IMEI: " + imei + " SIM ID: " + tManager.getSimSerialNumber() + " SIM NETWOR TYPE: " + networkType + " Connectivity: " + connectivity + " Access: " + accessDate);
        return mNetwork;
    }

    public MSoftware genSoftware() {
        String accessDate = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String lastActivity = accessDate;
        List<MUser> mUsers = MUser.listAll(MUser.class);
        MUser user = null;
        List<MSales> mSales = new ArrayList<>();
        MSales mSell;
        if (!mUsers.isEmpty()) {
            user = mUsers.get(mUsers.size() - 1);
            mSales = DbBulk.getUserSalesHistory(mUsers.get(mUsers.size() - 1).getUserId());
            if (!mSales.isEmpty()) {
                mSell = mSales.get(0);
                lastActivity = mSell.getDeviceTransactionTime();
            }
        }

        String deviceSerial = Build.SERIAL;
        List<MDevice> mDevices = MDevice.listAll(MDevice.class);
        MDevice mDevice = null;
        if (!mDevices.isEmpty()) {
            mDevice = mDevices.get(mDevices.size()-1);
        }

        String deviceName = "NO_REGISTERED_DEVICE";// : mDevice.getDeviceName() == null ? Build.SERIAL : mDevice.getDeviceName();
        if(mDevice != null)
            if(mDevice.getDeviceName() != null)
                deviceName = mDevice.getDeviceName();
            else
                deviceName = Build.SERIAL;
        String activeUser = user != null ? user.getName() != null ? user.getName() : "NO_ACTIVE_USER" : "NO_USER_FOUND";

        MSoftware mSoftware = new MSoftware(deviceName,
                user != null ? user.getName() : "NO_ACTIVE_USER", lastActivity, AppConfig.APP_DESC, accessDate);

        Log.v("Software", "Software Device: " + deviceName + " Active User: " + activeUser + " Last User Activity: " + lastActivity + " Access: " + accessDate);
        return mSoftware;
    }

    public MGps genGps() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();

        int cellid = loc.getCid();
        int lac = loc.getLac();

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String lon = cellid + "";
        String lat = lac + "";
        String accessDate = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        if (location != null) {
            lon = location.getLongitude() + "";
            lat = location.getLatitude() + "";
        }

        Log.v("Gps", "Gps Longitude: " + lon + " Latitude: " + lat + " Access: " + accessDate);
        return new MGps(lon, lat, accessDate);
    }
}
