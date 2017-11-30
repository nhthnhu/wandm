package com.wandm.loaders;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.wandm.models.MusicFolder;
import com.wandm.models.song.Song;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicFoldersLoader {

    private static final String TAG = "MusicFoldersLoader";

    public static final File externalStorage = Environment.getExternalStorageDirectory();
    public static final File internalStorage = new File("/storage/emulated");

    private static final String[] SUPPORTED_EXT = new String[]{"mp3"};
    private static final ArrayList<MusicFolder> musicFolders = new ArrayList<>();

    private OnLoadListener onLoadListener;

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    interface OnLoadListener {
        void completed(ArrayList<MusicFolder> musicFolders);
    }

    public static void getMusicFolders(final Activity activity,
                                       final File root,
                                       final OnLoadListener onLoadListener) {
        if (!musicFolders.isEmpty()) {
            onLoadListener.completed(musicFolders);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadMusicFolders(root, activity);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (onLoadListener != null) {
                            onLoadListener.completed(musicFolders);
                        }
                    }
                });
            }
        }).start();
    }

    private static void loadMusicFolders(File root, Context context) {
        ArrayList<File> listFiles = getAllDirs(root);
        if (listFiles.size() == 0) return;

        for (File file : listFiles) {
            if (hasMediaFile(file)) {
                ArrayList<Song> songs = SongLoader.getSongs(file, context);
                if (songs.size() != 0) {
                    MusicFolder folder = new MusicFolder(file, songs);
                    musicFolders.add(folder);
                }
            }
            loadMusicFolders(file, context);
        }
    }

    private static ArrayList<File> getAllDirs(File root) {
        ArrayList<File> dirs = new ArrayList<>();

        File f = new File(root.getAbsolutePath());
        File[] files = f.listFiles();

        for (File inFile : files) {
            if (inFile.isDirectory()) {
                if (!inFile.getName().isEmpty()) {
                    dirs.add(inFile);
                }
            }
        }

        return dirs;
    }

    public static List<File> getMediaFiles(File dir, final boolean acceptDirs) {
        ArrayList<File> list = new ArrayList<>();
        if (dir.isDirectory()) {
            List<File> files = Arrays.asList(dir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    if (file.isFile()) {
                        String name = file.getName();
                        return !".nomedia".equals(name) && checkFileExt(name);
                    } else
                        return file.isDirectory() && acceptDirs && checkDir(file);
                }
            }));
            Collections.sort(files, new FileNameComparator());
            Collections.sort(files, new DirFirstComparator());
            list.addAll(files);
        }

        return list;
    }

    private static boolean hasMediaFile(File root) {
        for (File file : root.listFiles()) {
            if (isMediaFile(file)) return true;
        }

        return false;
    }

    private static boolean isMediaFile(File file) {
        return file.exists() && file.canRead() && checkFileExt(file.getName());
    }

    private static boolean checkDir(File dir) {
        return dir.exists() && dir.canRead() && !".".equals(dir.getName()) && dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return !".".equals(name) && !"..".equals(name) && pathname.canRead() && (pathname.isDirectory() || (pathname.isFile() && checkFileExt(name)));
            }

        }).length != 0;
    }

    private static boolean checkFileExt(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        int p = name.lastIndexOf(".") + 1;
        if (p < 1) {
            return false;
        }
        String ext = name.substring(p).toLowerCase();
        for (String o : SUPPORTED_EXT) {
            if (o.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    private static class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File m2) {
            return f1.getName().compareTo(m2.getName());
        }
    }

    private static class DirFirstComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            if (f1.isDirectory() == f2.isDirectory())
                return 0;
            else if (f1.isDirectory() && !f2.isDirectory())
                return -1;
            else
                return 1;
        }
    }
}
