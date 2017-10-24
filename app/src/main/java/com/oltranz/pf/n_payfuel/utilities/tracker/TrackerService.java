package com.oltranz.pf.n_payfuel.utilities.tracker;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.pf.n_payfuel.R;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackerclient.CommandConfig;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackerconfig.DeviceConf;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.CommandBean;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.CommandList;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.CommandReport;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MBattery;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MGps;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MHardware;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MNetwork;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.MSoftware;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.PingRequest;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels.VitalInfoReport;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackerutils.DeviceInfoProvider;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackerutils.MonitorPing;
import com.oltranz.pf.n_payfuel.utilities.tracker.trackerutils.MonitorReport;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class TrackerService extends IntentService {
    public static final String ACTION_PING = "MAKE_PING";

    public static final String EXTRA_DEVICE_IMEI = "EXTRA_DEVICE_IMEI";
    public static final String EXTRA_DEVICE_ID = "EXTRA_DEVICE_ID";

    private MonitorPing monitorPing;
    private MonitorReport monitorReport;

    public TrackerService() {
        super("MonitorService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void requestPing(Context context, String deviceImei, String deviceId) {
        Intent intent = new Intent(context, TrackerService.class);
        intent.setAction(ACTION_PING);
        intent.putExtra(EXTRA_DEVICE_IMEI, deviceImei);
        intent.putExtra(EXTRA_DEVICE_ID, deviceId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PING.equals(action)) {
                final String deviceImei = intent.getStringExtra(EXTRA_DEVICE_IMEI);
                final String deviceId = intent.getStringExtra(EXTRA_DEVICE_ID);
                handleActionPing(deviceImei, deviceId);
            }
        }
    }

    /**
     * parameters.
     */
    private void handleActionPing(String deviceImei, String deviceId) {
        Log.d("Make Ping", "Make Ping request from: " + deviceImei + " and ID: " + deviceId);
        DeviceInfoProvider deviceInfoProvider = new DeviceInfoProvider(TrackerService.this);
        //NamingHelper.toSQLName(type)
        MBattery mBattery = deviceInfoProvider.genBattery();
        MHardware mHardware = deviceInfoProvider.genHardware();
        MNetwork mNetwork = deviceInfoProvider.genNetwork();
        MSoftware mSoftware = deviceInfoProvider.genSoftware();
        MGps mGps = deviceInfoProvider.genGps();

        monitorPing = new MonitorPing(CommandList.class);
        CommandList commandList = monitorPing.pingMonitor(CommandConfig.PING, DeviceConf.DEVICE_TYPE, new PingRequest(mBattery, mHardware, mSoftware, mGps, mNetwork));
        if (commandList != null && commandList.getReportId() != null && !commandList.getExec().isEmpty()) {
            //make report according to command
            for (CommandBean commandBean : commandList.getExec()) {
                try {
                    switch (commandBean.getCmd()) {
                        case CommandConfig.GPS:
                            handleGps(deviceId, commandList.getReportId(), commandBean);
                            continue;
                        case CommandConfig.GVI:
                            handleVitalInfo(deviceId, commandList.getReportId(), commandBean);
                            continue;
                        case CommandConfig.NONE:
                            continue;
                        case CommandConfig.NOT:
                            handleNotify(deviceId, commandList.getReportId(), commandBean);
                            continue;
                        case CommandConfig.RIN:
                            handleRingAndVib(true, deviceId, commandList.getReportId(), commandBean);
                            continue;
                        case CommandConfig.SNP:
                            handleSnapPicture(deviceId, commandList.getReportId(), commandBean);
                            continue;
                        case CommandConfig.VIB:
                            handleRingAndVib(false, deviceId, commandList.getReportId(), commandBean);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleGps(String deviceId, String reportId, CommandBean commandBean) {

        MGps mGps = new DeviceInfoProvider(TrackerService.this).genGps();
        VitalInfoReport vitalInfoReport = new VitalInfoReport();
        vitalInfoReport.setGps(mGps);
        onCommandReport(new CommandReport(commandBean.getCmd(), "200", "SUCCESS", vitalInfoReport), DeviceConf.DEVICE_TYPE, deviceId, reportId);
    }

    private void handleRingAndVib(boolean isRing, final String deviceId, final String reportId, final CommandBean commandBean) {

        if (!isRing) {
            //vibrate
            try {
                if (((Vibrator) getSystemService(VIBRATOR_SERVICE)).hasVibrator()) {
                    long vibMillis = Long.parseLong(commandBean.getValue());
                    if (vibMillis > 1000 * 60 * 3)
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000 * 60 * 3);
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(vibMillis);
                    //report onCommand success
                    onCommandReport(new CommandReport(commandBean.getCmd(), "200", "SUCCESS"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
                } else {
                    //report onCommand failed
                    onCommandReport(new CommandReport(commandBean.getCmd(), "201", "FAILED"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //report onCommand failed
                onCommandReport(new CommandReport(commandBean.getCmd(), "201", "FAILED"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
            }
        } else {
            //ring
            long ringMillis = Long.parseLong(commandBean.getValue());
            if (ringMillis > 1000 * 60 * 3)
                ringMillis = 1000 * 60 * 3;
            boolean isRingCompleted = false;

            try {
                //mobilemode.setStreamVolume(AudioManager.STREAM_RING,audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),0);
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                final MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(getApplicationContext(), notification);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
                isRingCompleted = true;
//                final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                ringtone.play();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.stop();
//                        ringtone.stop();
                        onCommandReport(new CommandReport(commandBean.getCmd(), "200", "SUCCESS"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
                    }
                }, ringMillis);
            } catch (Exception e) {
                e.printStackTrace();
                //report onCommand failed
                isRingCompleted = false;
            }
            if (!isRingCompleted) {
                try {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    final MediaPlayer mediaPlayer = new MediaPlayer();
                    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    if (alert == null) {
                        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        if (alert == null) {
                            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
                        }
                    }

                    if (alert != null) {
                        mediaPlayer.setDataSource(getApplicationContext(), alert);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mediaPlayer.stop();
                                onCommandReport(new CommandReport(commandBean.getCmd(), "200", "SUCCESS"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
                            }
                        }, ringMillis);
                    } else {
                        //report onCommand failed
                        onCommandReport(new CommandReport(commandBean.getCmd(), "201", "FAILED"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
                    }
                } catch (Exception e) {
                    //report onCommand failed
                    e.printStackTrace();
                    onCommandReport(new CommandReport(commandBean.getCmd(), "201", "FAILED"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
                }
            }
        }
    }

    private void handleNotify(String deviceId, String reportId, CommandBean commandBean) {
        if (commandBean.getValue() != null) {
            uiFeed(commandBean.getValue());
            onCommandReport(new CommandReport(commandBean.getCmd(), "200", "SUCCESS"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
        } else {
            //report onCommand failed
            onCommandReport(new CommandReport(commandBean.getCmd(), "201", "FAILED"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
        }
    }

    private void handleSnapPicture(String deviceId, String reportId, CommandBean commandBean) {
        onCommandReport(new CommandReport(commandBean.getCmd(), "200", "SUCCESS"), DeviceConf.DEVICE_TYPE, deviceId, reportId);
    }

    private void handleVitalInfo(String deviceId, String reportId, CommandBean commandBean) {
        DeviceInfoProvider deviceInfoProvider = new DeviceInfoProvider(TrackerService.this);
        //NamingHelper.toSQLName(type)
        MBattery mBattery = deviceInfoProvider.genBattery();
        MHardware mHardware = deviceInfoProvider.genHardware();
        MNetwork mNetwork = deviceInfoProvider.genNetwork();
        MSoftware mSoftware = deviceInfoProvider.genSoftware();
        MGps mGps = deviceInfoProvider.genGps();

        onCommandReport(new CommandReport(commandBean.getCmd(), "200", "SUCCESS", new VitalInfoReport(mBattery, mHardware, mSoftware, mGps, mNetwork)), DeviceConf.DEVICE_TYPE, deviceId, reportId);
    }

    private void uiFeed(String feedBack) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.SimpleDialog);
            builder.setMessage(feedBack)
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            TextView textView = (TextView) dialog.getWindow().findViewById(android.R.id.message);
            TextView alertTitle = (TextView) dialog.getWindow().findViewById(R.id.alertTitle);
            Button button1 = (Button) dialog.getWindow().findViewById(android.R.id.button1);

            Toast.makeText(getApplicationContext(), feedBack, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //String cmd, String devicetype, String deviceId, String reportId, CommandReport commandReport
    private void onCommandReport(CommandReport commandReport, String deviceType, String deviceId, String reportId) {
        monitorReport = new MonitorReport(String.class);
        String result = monitorReport.reportCommand(CommandConfig.REPORT, deviceType, deviceId, reportId, commandReport);
        Log.d("Report Result", "Reporting result: " + result);

    }
}
