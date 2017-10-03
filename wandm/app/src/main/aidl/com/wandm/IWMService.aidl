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
    void playNew();
    int duration();
    int position();
    void seekTo(int position);
    void setVolume(float left, float right);
}
