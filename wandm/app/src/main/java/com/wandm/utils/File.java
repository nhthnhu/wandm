package com.wandm.utils;

/*
 * File.java
 * Copyright (C) 2014 Amin Bandali <me@aminb.org>
 *
 * id3r is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * id3r is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import android.util.Log;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.IOException;

public class File {

    private static final String TAG = "wm.com.utils.File";

    String fileName;
    Mp3File file;

    public File(String name) {
        fileName = name;
    }

    public ID3v2 getTags() {
        Mp3File file = null;
        try {
            file = new Mp3File(fileName);
        } catch (IOException | InvalidDataException | UnsupportedTagException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        this.file = file;
        if (file != null) {
            if (file.hasId3v2Tag())
                return file.getId3v2Tag();
            else if (file.hasId3v1Tag()) {
                // TODO: alert the user that ID3v1 is not supported
                return null;
            } else
                return new ID3v24Tag();
        }
        return null;
    }

    public void setTags(ID3v2 tags) {
        file.setId3v2Tag(tags);
    }

    public boolean save() {
        try {
            String tmp = fileName + "tmp";
            file.save(tmp);
            java.io.File ioFile = new java.io.File(tmp);
            boolean deleted = new java.io.File(fileName).delete();
            if (deleted)
                return ioFile.renameTo(new java.io.File(fileName));
        } catch (IOException | NotSupportedException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return false;
    }
}
