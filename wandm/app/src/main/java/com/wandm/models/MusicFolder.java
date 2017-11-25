package com.wandm.models;

import com.wandm.models.song.Song;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class MusicFolder implements Serializable {
    private File file;
    private ArrayList<Song> songs;

    public MusicFolder(File file, ArrayList<Song> songs) {
        this.file = file;
        this.songs = songs;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
