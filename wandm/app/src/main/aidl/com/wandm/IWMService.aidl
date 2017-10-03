// IWMService.aidl
package com.wandm;

// Declare any non-default types here with import statements

interface IWMService {
    void playNext();
    void playPre();
    void stop();
    void pause();
    void play();
    void resume();
    boolean isPlaying();
}
