package com.wandm.services;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wandm.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by tien on 10/5/17.
 */

public class DownloadService extends IntentService {
    public static final String FILE_NAME = "file name";
    public static final String FILE_PATH = "file path";
    public static final String URL_PATH = "url path";
    public static final String NOTIFICATION = "notification";
    public static final String RESULT = "result";
    public static final String TAG = "Download";
    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private String fileName;
    private String filePath;

    public DownloadService() {
        super("DownLoadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        fileName = intent.getStringExtra(FILE_NAME);
        filePath = intent.getStringExtra(FILE_PATH);
        String urlPath = intent.getStringExtra(URL_PATH);

        doDownload(urlPath, fileName, filePath);

    }

    /**
     * This function used to download a file mp3
     *
     * @param urlPath:  url of file need download
     * @param fileName: name of file need download
     * @param filePath: path of file need download
     */
    public void doDownload(String urlPath, String fileName, String filePath) {

        setNotification();
        int result;
        try {
            result = Activity.RESULT_CANCELED;
            URL url = new URL(urlPath);
            URLConnection connection = url.openConnection();
            int fileLength = connection.getContentLength();

            File storagePath = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "\\" + filePath);
            //do download
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            OutputStream outputStream = new FileOutputStream(new File(storagePath, fileName));
            int downloaded = 0;

            try {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                    downloaded += bytesRead;
                    outputStream.write(buffer, 0, bytesRead);
                    int percent = downloaded * 100 / fileLength + 1;
                    if (percent < 100) {
                        builder.setContentText(getString(R.string.downloading) + ": " + (percent - 1));
                        builder.setProgress(100, percent, false);
                    } else {
                        builder.setProgress(0, 0, false);
                        builder.setContentText(getString(R.string.download_completed));
                    }
                    notificationManager.notify(1, builder.build());
                    result = 1;
                    publishResults(result);
                }

                result = Activity.RESULT_OK;

            } finally {
                publishResults(result);
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, "doDownload: Error URL", e);
            doDownloadFailed();
        } catch (IOException e) {

            Log.e(TAG, "doDownload: Can't connect", e);
            doDownloadFailed();
        }

    }

    /**
     * This function used to send info to main by BroadcastReceiver
     *
     * @param result: result = 1: downloading
     *                result = 0: download failed
     *                result = -1: download completed
     */
    private void publishResults(int result) {
        Intent intent = new Intent(DownloadService.NOTIFICATION);
        intent.putExtra(DownloadService.RESULT, result);
        sendBroadcast(intent);
    }

    private void setNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getString(R.string.download) + ": " + fileName);
    }

    /**
     * This function will be called when file download error
     */
    private void doDownloadFailed() {
        builder.setProgress(0, 0, false);
        builder.setContentText(getString(R.string.download_failed));
        notificationManager.notify(1, builder.build());
        publishResults(Activity.RESULT_CANCELED);
        File storage = new File(String.valueOf(Environment.getExternalStorageDirectory()));
        File file = new File(storage, fileName);
        if (!file.delete()) Log.d(TAG, "doDownloadFailed: can't delete");
        ;
    }
}
