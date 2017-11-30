package com.wandm.download.report.exceptions;

/**
 * Created by Majid Golshadi on 4/23/2014.
 */
public class QueueDownloadInProgressException extends IllegalAccessException {

    public QueueDownloadInProgressException(){
        super("queue download is already in progress");
    }
}
