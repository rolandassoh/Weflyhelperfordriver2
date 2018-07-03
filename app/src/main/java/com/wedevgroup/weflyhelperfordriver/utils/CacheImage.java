package com.wedevgroup.weflyhelperfordriver.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.wedevgroup.weflyhelperfordriver.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Obrina.KIMI on 1/31/2018.
 */

public class CacheImage {
    private String directoryName = Constants.SAVE_DIRECTORY_NAME;
    private String fileKey;
    private String fileExtension = ".png";
    private final String fileTempName  = "temp";
    private Context context;
    private boolean external = false;
    private AlertDialog dialog;
    private View view;
    private OnMovingCompleteListener listener;
    private final  String TAG = getClass().getSimpleName();

    public CacheImage(Context context) {
        this.context = context;

    }

    public CacheImage(@NonNull Context context, @NonNull View view) {
        this.context = context;
        this.view = view;
    }

    public CacheImage setFileKey(String fileName) {
        this.fileKey = fileName;
        return this;
    }

    public CacheImage setExternal(boolean external) {
        this.external = external;
        return this;
    }

    public CacheImage setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public boolean save(@NonNull String key,  Bitmap bitmapImage) {
        this.fileKey = key;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean deleteFile(@NonNull String key){
        this.fileKey = key;
        File file = createFile();
        boolean isDone = file.delete();
        return isDone;
    }

    public Bitmap load(@NonNull String key) {
        this.fileKey = key;
        FileInputStream inputStream = null;

        try {
            // tragic logic created OOME, but we can blame it on lack of memory
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch(OutOfMemoryError e) {
            // but what the hell will you do here :)
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            // get ready to be fired by your boss
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean saveTemp(@NonNull Bitmap bp){
        // file is Ecrase if is exist
        return save(fileTempName, bp);
    }

    public boolean saveFromTemp(@NonNull final String key){
        Bitmap bmp = load(fileTempName);
        if (bmp!= null){
            boolean isTempDel = false;
            isTempDel = deleteFile(fileTempName);
            if (isTempDel){
                if (save(key, bmp)){
                    return true;
                }
            }
        }
        return false;

    }

    public boolean isInternalSpaceEnough(@NonNull Bitmap bmp){
        long imageSize = byteSizeOf(bmp);
        return isMemorySizeAvailableAndroid(imageSize, false);
    }



    @NonNull
    private File createFile() {
        File directory;
        if(external){
            directory = getAlbumStorageDir(directoryName);
        }
        else {
            directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        }
        if(!directory.exists() && !directory.mkdirs()){
            Log.v(Constants.APP_NAME, TAG + "Error creating directory " + directory);
        }

        return new File(directory, fileKey + fileExtension);
    }

    public @Nullable File getAsFile(@NonNull String key) throws Exception {
        this.fileKey = key;
        File directory;
        if(external){
            directory = getAlbumStorageDir(directoryName);
        }
        else {
            directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        }
        if(!directory.exists() && !directory.mkdirs()){
            Log.v(Constants.APP_NAME, TAG + "Error creating directory " + directory);
        }

        return new File(directory, fileKey + fileExtension);

    }

    private File getAlbumStorageDir(String albumName) {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }

    private static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }

    private static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    private static boolean isMemorySizeAvailableAndroid(long download_bytes, boolean isExternalMemory) {
        boolean isMemoryAvailable = false;
        long freeSpace = 0;

        // if isExternalMemory get true to calculate external SD card available size
        if(isExternalMemory){
            try {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
                if(freeSpace > download_bytes){
                    isMemoryAvailable = true;
                }else{
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {e.printStackTrace(); isMemoryAvailable = false;}
        }else{
            // find phone available size
            try {
                StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
                freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
                if(freeSpace > download_bytes){
                    isMemoryAvailable = true;
                }else{
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {e.printStackTrace(); isMemoryAvailable = false;}
        }

        return isMemoryAvailable;
    }


    private static long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
    }

    public static boolean externalMemoryAvailable(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return true;
        else
            return false;

    }

    public void setOnMovingCompleteListener(@NonNull OnMovingCompleteListener listener) {
        this.listener = listener;

    }

    public static interface OnMovingCompleteListener {
        void onMovingError();
        void onMovingSucces(boolean fromInternalToExternal);
    }

    private void notifyOnMovingCompleteListener(boolean isDone, boolean toExternal) {
        Log.v(Constants.APP_NAME, TAG + " notifyOnMovingCompleteListener isDone" + isDone + " toExternal " + toExternal);
        if (listener != null ){
            if (isDone)
                listener.onMovingSucces(toExternal);
            else
                listener.onMovingError();
        }

    }

    public static long getFolderSize(Context context, File file) {
        File directory = readlink(file); // resolve symlinks to internal storage
        String path = directory.getAbsolutePath();
        Cursor cursor = null;
        long size = 0;
        try {
            cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                    new String[]{MediaStore.MediaColumns.SIZE},
                    MediaStore.MediaColumns.DATA + " LIKE ?",
                    new String[]{path + "/%/%"},
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    size += cursor.getLong(0);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return size;
    }


    /**
     * Canonicalize by following all symlinks. Same as "readlink -f file".
     *
     * @param file
     *     a {@link File}
     * @return The absolute canonical file
     */
    public static File readlink(File file) {
        File f;
        try {
            f = file.getCanonicalFile();
        } catch (IOException e) {
            return file;
        }
        if (f.getAbsolutePath().equals(file.getAbsolutePath())) {
            return f;
        }
        return readlink(f);
    }

    private @NonNull File getDirectory(@NonNull Context context){
        File directory;
        if(external){
            directory = getAlbumStorageDir(directoryName);
        }
        else {
            directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        }
        if(!directory.exists() && !directory.mkdirs()){
            Log.v(Constants.APP_NAME, TAG + "Error creating directory " + directory);
        }

        return directory;

    }

    public @NonNull String getCacheSize(){
        String size = "";
        if (context != null){
            File directory;
            directory = getDirectory(context);

            long directorySize = getFileSize(directory);
            size =  Formatter.formatFileSize(context, directorySize);

        }
        return size;

    }

    public void cleanCache(){
        if (context != null){
            try {
                deleteRecursive(getDirectory(context));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }




}
