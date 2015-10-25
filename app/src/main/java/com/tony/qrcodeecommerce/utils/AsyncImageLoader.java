package com.tony.qrcodeecommerce.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.tony.qrcodeecommerce.utils.DiskLruCache.Editor;
import com.tony.qrcodeecommerce.utils.DiskLruCache.Snapshot;

public class AsyncImageLoader {

    private static final String TAG = "AsyncImageLoader";

    private Context context;

    // 内存缓存默认为5M
    private final int MEM_CACHE_DEFAULT_SIZE = 5 * 1024 * 1024;
    // 文件缓存默认为10M
    private final int DISK_CAHCE_DEFAULT_SIZE = 10 * 1024 * 1024;
    // 一级缓存基于LruCache
    private LruCache<String, Bitmap> memCache;
    // 二级缓存基于DiskLruCache
    private DiskLruCache diskCache;

    public AsyncImageLoader(Context context) {
        this.context = context;
        initMemCache();
        initDiskLruCache();

    }

    /**
     *
     * 初始化文件缓存
     *
     */
    private void initDiskLruCache() {

        try {
            File cacheDir = getDiskCacheDir(context, "bitmap");

            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            // 第三个参数指定同一个key可以对应多少个缓存文件,基本都是传1
            diskCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1,
                    DISK_CAHCE_DEFAULT_SIZE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * 初始化内存缓存
     *
     */
    private void initMemCache() {

        memCache = new LruCache<String, Bitmap>(MEM_CACHE_DEFAULT_SIZE) {

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }

        };

    }

    /**
     * 从内存缓存中拿
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapFromMem(String url) {
        return memCache.get(url);
    }

    /**
     *
     * 加入到内存缓存中
     *
     * @param url
     * @param bitmap
     */
    public void putBitmapToMem(String url, Bitmap bitmap) {
        memCache.put(url, bitmap);

    }

    /**
     *
     * 从文件缓存中拿
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapFromDisk(String url) {

        try {
            String key = hashKeyForDisk(url);

            Snapshot snapshot = diskCache.get(key);

            if (snapshot != null) {
                InputStream is = snapshot.getInputStream(0);

                Bitmap bitmap = BitmapFactory.decodeStream(is);

                return bitmap;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     *
     * 从url加载图片
     *
     * @param imageView
     * @param imageUrl
     * @return
     */
    public Bitmap loadImage(ImageView imageView, String imageUrl) {

        // 先从内存中拿
        Bitmap bitmap = getBitmapFromMem(imageUrl);

        if (bitmap != null) {
            Log.i(TAG, "image exists in memory");
            return bitmap;
        }
        // 再从文件中找
        bitmap = getBitmapFromDisk(imageUrl);

        if (bitmap != null) {

            Log.i(TAG, "image exists in file");
            // 重新缓存到内存中
            putBitmapToMem(imageUrl, bitmap);

            return bitmap;

        }

        // 内存和文件都不存在，则从网络上下载
        if (!TextUtils.isEmpty(imageUrl)) {


            Log.d(TAG, "缓存不存在，从网上下载...");

            new ImageDownloadTask(imageView).execute(imageUrl);

        }
        return null;

    }

    class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {

        private String imageUrl;

        private ImageView imageView;

        public ImageDownloadTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                imageUrl = params[0];

                String key = hashKeyForDisk(imageUrl);

                // 下载成功后直接将图片流写入到文件缓存

                Editor editor = diskCache.edit(key);

                if (editor != null) {

                    OutputStream outputStream = editor.newOutputStream(0);

                    if (downloadUrlToStream(imageUrl, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }

                }

                diskCache.flush();

                Bitmap bitmap = getBitmapFromDisk(imageUrl);

                if (bitmap != null) {

                    // 将图片加入到内存缓存中
                    putBitmapToMem(imageUrl, bitmap);

                }

                return bitmap;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            if (result != null) {

                // 通过设置tag，放置图片错位

                if (imageView.getTag() != null
                        && imageView.getTag().equals(imageUrl)) {

                    imageView.setImageBitmap(result);

                }

            }

        }

        private boolean downloadUrlToStream(String urlString,
                                            OutputStream outputStream) {

            HttpURLConnection urlConnection = null;

            BufferedOutputStream out = null;

            BufferedInputStream in = null;

            try {
                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream(),
                        8 * 1024);

                out = new BufferedOutputStream(outputStream, 8 * 1024);

                int b;

                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return false;
        }

    }

    private String hashKeyForDisk(String key) {

        String cacheKey;

        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());

            cacheKey = byteToHexString(mDigest.digest());

        } catch (Exception e) {
            e.printStackTrace();
            cacheKey = String.valueOf(key.hashCode());
        }

        return cacheKey;

    }

    private String byteToHexString(byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {

            String hex = Integer.toHexString(0xFF & bytes[i]);

            if (hex.length() == 1) {
                sb.append('0');
            }

            sb.append(hex);

        }

        return sb.toString();

    }

    private File getDiskCacheDir(Context context, String uniqueName) {

        String cachePath;

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {

            cachePath = context.getExternalCacheDir().getPath();

        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);

    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);

            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return 1;

    }

}