package ch.epfl.sweng.runpharaa.cache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.epfl.sweng.runpharaa.R;
import ch.epfl.sweng.runpharaa.utils.Util;

public class ImageLoader {
    private MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private final ExecutorService executor;
    private final int defaultId = R.mipmap.ic_launcher; //TODO: put id of default image here
    private boolean defaultWhileLoad;

    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executor = Executors.newFixedThreadPool(5);
        defaultWhileLoad = true;
    }

    public ImageLoader(Context context, boolean defaultWhileLoad) {
        fileCache = new FileCache(context);
        executor = Executors.newFixedThreadPool(5);
        this.defaultWhileLoad = defaultWhileLoad;
    }

    public void displayImage(String url, ImageView view) {
        imageViews.put(view, url);
        Bitmap b = memoryCache.get(url);
        if(b != null)
            view.setImageBitmap(b);
        else {
            queuePhoto(url, view);
            if(defaultWhileLoad)
                view.setImageResource(defaultId);
        }
    }

    private void queuePhoto(String url, ImageView image) {
        PhotoToLoad p = new PhotoToLoad(url, image);
        executor.submit(new PhotoLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);
        Bitmap b = decodeFile(f);
        if(b != null)
            return b;
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(true);
            InputStream is = connection.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Util.copyStream(is, os);
            os.close();
            b = decodeFile(f);
            return b;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }

    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE = 70;
            int tempWidth = o.outWidth, tempHeight = o.outHeight;
            int scale = 1;

            while(tempWidth / 2 < REQUIRED_SIZE || tempHeight / 2 < REQUIRED_SIZE) {
                tempWidth/= 2;
                tempHeight/= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Wrapper class representing a photo to be loaded into an image view
     */
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        PhotoToLoad(String url, ImageView view) {
            this.url = url;
            imageView = view;
        }
    }

    class PhotoLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotoLoader(PhotoToLoad p) {
            photoToLoad = p;
        }

        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap b = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, b);

            if(imageViewReused(photoToLoad))
                return;

            BitmapDisplayer bd = new BitmapDisplayer(b, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    private boolean imageViewReused(PhotoToLoad p) {
        String tag = imageViews.get(p.imageView);
        return (tag == null || !tag.equals(p.url));
    }

    class BitmapDisplayer implements Runnable {
        Bitmap b;
        PhotoToLoad p;

        BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            this.b = b;
            this.p = p;
        }

        @Override
        public void run() {
            if(imageViewReused(p))
                return;
            if(b != null)
                p.imageView.setImageBitmap(b);
            else
                p.imageView.setImageResource(defaultId);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
}