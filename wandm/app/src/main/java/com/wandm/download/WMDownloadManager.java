package com.wandm.download;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wandm.App;
import com.wandm.R;
import com.wandm.download.core.DownloadManagerPro;
import com.wandm.download.report.ReportStructure;
import com.wandm.download.report.listener.DownloadManagerListener;
import com.wandm.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;

import java.io.IOException;
import java.util.ArrayList;

public class WMDownloadManager {

    private static final String TAG = "WMDownloadManager";

    private static final String STATE_DOWNLOAD_STARTED = "state_download_started";
    private static final String STATE_DOWNLOAD_IN_PROGRESS = "state_download_in_progress";
    private static final String STATE_DOWNLOAD_COMPLETED = "state_download_completed";
    private static final String STATE_DOWNLOAD_ERROR = "state_download_error";

    private static WMDownloadManager instance = null;

    public static WMDownloadManager getInstance() {
        if (instance == null)
            instance = new WMDownloadManager();
        return instance;
    }

    private DownloadManagerPro downloadManager;
    private DownloadManagerListener downloadManagerListener = new DownloadManagerListener() {
        @Override
        public void OnDownloadStarted(long taskId) {
            Log.d(TAG, "OnDownloadStarted");
            for (WMDownloadManager.Listener listener : listeners)
                if (listener != null) listener.OnDownloadStarted(taskId);
            updateNotification((int) taskId, STATE_DOWNLOAD_STARTED, 0);
        }

        @Override
        public void OnDownloadPaused(long taskId) {
            Log.d(TAG, "OnDownloadPaused");
            for (WMDownloadManager.Listener listener : listeners)
                if (listener != null) listener.OnDownloadPaused(taskId);
        }

        @Override
        public void onDownloadProcess(long taskId, double percent, long downloadedLength) {
            Log.d(TAG, "onDownloadProcess: " + percent);
            for (WMDownloadManager.Listener listener : listeners)
                if (listener != null) listener.OnDownloadProcess(taskId, percent, downloadedLength);
            updateNotification((int) taskId, STATE_DOWNLOAD_IN_PROGRESS, percent);
        }

        @Override
        public void OnDownloadFinished(long taskId) {
            Log.d(TAG, "OnDownloadFinished");
            for (WMDownloadManager.Listener listener : listeners)
                if (listener != null) listener.OnDownloadFinished(taskId);
        }

        @Override
        public void OnDownloadRebuildStart(long taskId) {
            Log.d(TAG, "OnDownloadRebuildStart");
            for (WMDownloadManager.Listener listener : listeners)
                if (listener != null) listener.OnDownloadRebuildStart(taskId);
        }

        @Override
        public void OnDownloadRebuildFinished(long taskId) {
            Log.d(TAG, "OnDownloadRebuildFinished");
            for (WMDownloadManager.Listener listener : listeners)
                if (listener != null) listener.OnDownloadRebuildFinished(taskId);
        }

        @Override
        public void OnDownloadCompleted(long taskId) {
            Log.d(TAG, "OnDownloadCompleted");
            for (WMDownloadManager.Listener listener : listeners)
                if (listener != null) listener.OnDownloadCompleted(taskId);

            ReportStructure report = downloadManager.singleDownloadStatus((int) taskId);
            updateNotification((int) taskId, STATE_DOWNLOAD_COMPLETED, 100);
            Utils.INSTANCE.scanMp3File(App.instance, new String[]{report.saveAddress});
        }

        @Override
        public void connectionLost(long taskId) {
            Log.d(TAG, "connectionLost");
            for (WMDownloadManager.Listener listener : listeners)
                if (listener != null) listener.connectionLost(taskId);
            updateNotification((int) taskId, STATE_DOWNLOAD_ERROR, 0);
        }
    };

    private Context context;
    private static ArrayList<WMDownloadManager.Listener> listeners = new ArrayList<>();

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    private WMDownloadManager() {
        context = App.instance;
        downloadManager = new DownloadManagerPro(context);
        downloadManager.init("/WMDownload/",
                1, downloadManagerListener);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
    }

    private void updateNotification(int taskId, String state, double percent) {
        ReportStructure report = downloadManager.singleDownloadStatus(taskId);

        switch (state) {
            case STATE_DOWNLOAD_STARTED:
                notificationBuilder
                        .setContentTitle(report.name)
                        .setContentText(context.getString(R.string.downloading));

                break;

            case STATE_DOWNLOAD_IN_PROGRESS:
                notificationBuilder
                        .setContentText((int) percent + "%")
                        .setProgress(100, (int) percent, false);
                break;

            case STATE_DOWNLOAD_COMPLETED:
                notificationBuilder
                        .setContentText(context.getString(R.string.download_completed))
                        .setProgress(100, 100, false);
                break;

            case STATE_DOWNLOAD_ERROR:
                notificationBuilder
                        .setContentText(context.getString(R.string.download_failed))
                        .setSmallIcon(android.R.drawable.stat_notify_error);
                break;
        }
        notificationManager.notify(taskId, notificationBuilder.build());
    }

    public void download(final String saveName, final String url, final WMDownloadManager.Listener listener) {
        Thread downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String mp3Link = url;
                try {
                    Jsoup.connect(url).timeout(3000).get();
                } catch (UnsupportedMimeTypeException e) {
                    mp3Link = e.getUrl();
                    Log.d(TAG, "URLDownload: " + mp3Link);
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage(), e);
                }

                int taskToken = downloadManager.addTask(saveName, mp3Link, true, false);
                try {
                    downloadManager.startDownload(taskToken);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                listeners.add(listener);
            }
        });

        downloadThread.start();
    }

    public interface Listener {
        void OnDownloadStarted(long taskId);

        void OnDownloadRebuildStart(long taskId);

        void OnDownloadPaused(long taskId);

        void OnDownloadCompleted(long taskId);

        void OnDownloadProcess(long taskId, double percent, long downloadedLength);

        void OnDownloadRebuildFinished(long taskId);

        void OnDownloadFinished(long taskId);

        void connectionLost(long taskId);
    }
}
